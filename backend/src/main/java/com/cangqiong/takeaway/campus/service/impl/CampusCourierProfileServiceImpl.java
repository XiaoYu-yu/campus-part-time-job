package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.dto.CampusCourierProfileSubmitDTO;
import com.cangqiong.takeaway.campus.dto.CampusCourierReviewDTO;
import com.cangqiong.takeaway.campus.dto.CampusCustomerCourierOnboardingSubmitDTO;
import com.cangqiong.takeaway.campus.entity.CampusCourierProfile;
import com.cangqiong.takeaway.campus.enums.CampusCourierReviewStatus;
import com.cangqiong.takeaway.campus.mapper.CampusCourierProfileMapper;
import com.cangqiong.takeaway.campus.query.CampusCourierQuery;
import com.cangqiong.takeaway.campus.service.CampusCourierProfileService;
import com.cangqiong.takeaway.campus.support.CampusRuleCatalog;
import com.cangqiong.takeaway.campus.vo.CampusCourierProfileVO;
import com.cangqiong.takeaway.campus.vo.CampusCourierReviewStatusVO;
import com.cangqiong.takeaway.campus.vo.CampusCourierTokenEligibilityVO;
import com.cangqiong.takeaway.campus.vo.CampusCustomerCourierOnboardingProfileVO;
import com.cangqiong.takeaway.campus.vo.CampusCustomerCourierOnboardingReviewStatusVO;
import com.cangqiong.takeaway.entity.User;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.mapper.UserMapper;
import com.cangqiong.takeaway.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class CampusCourierProfileServiceImpl implements CampusCourierProfileService {

    private static final int ENABLED = 1;
    private static final int DISABLED = 0;
    private static final String CONTROLLED_FILE_PREFIX = "/api/files/";
    private static final String PENDING_REVIEW_COMMENT = "待人工审核";
    private static final String NOT_SUBMITTED_REMARK = "未提交资料";
    private static final String ELIGIBLE_MESSAGE = "已通过，可申请 courier token";
    private static final String PENDING_MESSAGE = "审核中";
    private static final String REJECTED_MESSAGE = "已拒绝，请修改资料后重提";
    private static final String DISABLED_MESSAGE = "已禁用，请联系管理员";
    private static final String UNKNOWN_STATUS_MESSAGE = "当前状态暂不可申请 courier token";
    private static final Set<String> ALLOWED_GENDERS = Set.of("MALE", "FEMALE", "OTHER");

    @Autowired
    private CampusCourierProfileMapper campusCourierProfileMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult<CampusCourierProfileVO> pageQuery(CampusCourierQuery query) {
        int page = safePositive(query.getPage(), 1);
        int pageSize = safePageSize(query.getPageSize(), query.getSize());
        int offset = (page - 1) * pageSize;

        List<CampusCourierProfileVO> records = campusCourierProfileMapper.selectByCondition(
                normalizeText(query.getRealName()),
                normalizeText(query.getPhone()),
                normalizeText(query.getStudentNo()),
                normalizeText(query.getCollege()),
                normalizeText(query.getReviewStatus()),
                normalizeText(query.getDormitoryBuilding()),
                query.getEnabled(),
                offset,
                pageSize
        );

        Long total = campusCourierProfileMapper.countByCondition(
                normalizeText(query.getRealName()),
                normalizeText(query.getPhone()),
                normalizeText(query.getStudentNo()),
                normalizeText(query.getCollege()),
                normalizeText(query.getReviewStatus()),
                normalizeText(query.getDormitoryBuilding()),
                query.getEnabled()
        );

        PageResult<CampusCourierProfileVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        pageResult.setSize((long) pageSize);
        pageResult.setCurrent((long) page);
        pageResult.setPages((total + pageSize - 1) / pageSize);
        return pageResult;
    }

    @Override
    @Transactional
    public CampusCourierProfileVO submitProfile(CampusCourierProfileSubmitDTO dto, Long courierUserId) {
        User user = requireActiveUser(courierUserId);
        validateSubmitRequest(dto);

        saveSubmittedProfile(courierUserId, now -> profile -> applyFullSubmittedFields(profile, dto, now));
        return requireDetailProfile(courierUserId, user);
    }

    @Override
    public CampusCourierProfileVO getCurrentProfile(Long courierUserId) {
        requireActiveUser(courierUserId);
        CampusCourierProfileVO profileVO = campusCourierProfileMapper.selectDetailByUserId(courierUserId);
        if (profileVO == null) {
            throw new BusinessException(404, "配送员资料不存在");
        }
        return profileVO;
    }

    @Override
    public CampusCourierReviewStatusVO getCurrentReviewStatus(Long courierUserId) {
        requireActiveUser(courierUserId);
        CampusCourierReviewStatusVO reviewStatusVO = campusCourierProfileMapper.selectReviewStatusByUserId(courierUserId);
        if (reviewStatusVO == null) {
            throw new BusinessException(404, "配送员资料不存在");
        }
        return reviewStatusVO;
    }

    @Override
    @Transactional
    public CampusCustomerCourierOnboardingProfileVO submitOnboardingProfile(CampusCustomerCourierOnboardingSubmitDTO dto, Long customerUserId) {
        User user = requireActiveUser(customerUserId);
        validateOnboardingSubmitRequest(dto, user);

        saveSubmittedProfile(customerUserId, now -> profile -> applyOnboardingSubmittedFields(profile, dto, now));
        return getCurrentOnboardingProfile(customerUserId);
    }

    @Override
    public CampusCustomerCourierOnboardingProfileVO getCurrentOnboardingProfile(Long customerUserId) {
        User user = requireActiveUser(customerUserId);
        CampusCourierProfileVO profileVO = campusCourierProfileMapper.selectDetailByUserId(customerUserId);
        if (profileVO == null) {
            return buildEmptyOnboardingProfile(user);
        }
        if (!StringUtils.hasText(profileVO.getPhone())) {
            profileVO.setPhone(user.getPhone());
        }
        return buildOnboardingProfile(profileVO, user);
    }

    @Override
    public CampusCustomerCourierOnboardingReviewStatusVO getCurrentOnboardingReviewStatus(Long customerUserId) {
        requireActiveUser(customerUserId);
        CampusCourierReviewStatusVO reviewStatusVO = campusCourierProfileMapper.selectReviewStatusByUserId(customerUserId);
        if (reviewStatusVO == null) {
            CampusCustomerCourierOnboardingReviewStatusVO emptyStatus = new CampusCustomerCourierOnboardingReviewStatusVO();
            emptyStatus.setReviewRemark(NOT_SUBMITTED_REMARK);
            emptyStatus.setEnabled(DISABLED);
            emptyStatus.setCanApplyCourierToken(false);
            return emptyStatus;
        }

        CampusCustomerCourierOnboardingReviewStatusVO response = new CampusCustomerCourierOnboardingReviewStatusVO();
        response.setReviewStatus(reviewStatusVO.getReviewStatus());
        response.setEnabled(reviewStatusVO.getEnabled());
        response.setReviewRemark(reviewStatusVO.getReviewComment());
        response.setReviewedAt(reviewStatusVO.getReviewedAt());
        response.setCanApplyCourierToken(canApplyCourierToken(reviewStatusVO.getReviewStatus(), reviewStatusVO.getEnabled()));
        return response;
    }

    @Override
    public CampusCourierTokenEligibilityVO getTokenEligibility(Long customerUserId) {
        requireActiveUser(customerUserId);
        CampusCourierReviewStatusVO reviewStatusVO = campusCourierProfileMapper.selectReviewStatusByUserId(customerUserId);

        CampusCourierTokenEligibilityVO response = new CampusCourierTokenEligibilityVO();
        if (reviewStatusVO == null) {
            response.setEligible(false);
            response.setEnabled(DISABLED);
            response.setMessage(NOT_SUBMITTED_REMARK);
            return response;
        }

        boolean eligible = canApplyCourierToken(reviewStatusVO.getReviewStatus(), reviewStatusVO.getEnabled());
        response.setEligible(eligible);
        response.setReviewStatus(reviewStatusVO.getReviewStatus());
        response.setEnabled(reviewStatusVO.getEnabled());
        response.setMessage(resolveTokenEligibilityMessage(reviewStatusVO.getReviewStatus(), reviewStatusVO.getEnabled()));
        return response;
    }

    @Override
    @Transactional
    public void reviewByAdmin(Long id, CampusCourierReviewDTO dto, Long employeeId) {
        if (employeeId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }
        if (id == null) {
            throw new BusinessException("配送员资料 ID 不能为空");
        }
        if (dto == null) {
            throw new BusinessException("审核请求不能为空");
        }

        CampusCourierProfile profile = campusCourierProfileMapper.selectById(id);
        if (profile == null) {
            throw new BusinessException(404, "配送员资料不存在");
        }

        CampusCourierReviewStatus reviewStatus = resolveReviewStatus(dto.getReviewStatus());
        if (reviewStatus == CampusCourierReviewStatus.PENDING) {
            throw new BusinessException("审核接口不允许将状态直接设置为待审核");
        }

        String reviewComment = normalizeText(dto.getReviewComment());
        if ((reviewStatus == CampusCourierReviewStatus.REJECTED || reviewStatus == CampusCourierReviewStatus.DISABLED)
                && !StringUtils.hasText(reviewComment)) {
            throw new BusinessException("驳回或停用时必须填写审核说明");
        }

        if (reviewStatus == CampusCourierReviewStatus.APPROVED && !StringUtils.hasText(reviewComment)) {
            reviewComment = "审核通过";
        }

        LocalDateTime now = LocalDateTime.now();
        int enabled = reviewStatus == CampusCourierReviewStatus.APPROVED ? ENABLED : DISABLED;
        campusCourierProfileMapper.updateReviewDecision(
                id,
                reviewStatus.name(),
                reviewComment,
                employeeId,
                now,
                enabled,
                now
        );
    }

    @Override
    public CampusCourierProfile requireApprovedEnabledProfile(Long courierUserId) {
        requireActiveUser(courierUserId);

        CampusCourierProfile approvedProfile = campusCourierProfileMapper.selectApprovedEnabledByUserId(courierUserId);
        if (approvedProfile != null) {
            return approvedProfile;
        }

        CampusCourierProfile profile = campusCourierProfileMapper.selectByUserId(courierUserId);
        if (profile == null) {
            throw new BusinessException(403, "配送员资料不存在，请先提交资料");
        }
        if (!CampusCourierReviewStatus.APPROVED.name().equals(profile.getReviewStatus())) {
            throw new BusinessException(403, "配送员资料未审核通过，当前不可进入接单链路");
        }
        throw new BusinessException(403, "配送员账号已停用，当前不可进入接单链路");
    }

    private void saveSubmittedProfile(Long userId, ProfileMutatorFactory mutatorFactory) {
        LocalDateTime now = LocalDateTime.now();
        CampusCourierProfile existing = campusCourierProfileMapper.selectByUserId(userId);
        ProfileMutator mutator = mutatorFactory.create(now);
        if (existing == null) {
            CampusCourierProfile profile = new CampusCourierProfile();
            profile.setUserId(userId);
            profile.setCreatedAt(now);
            mutator.apply(profile);
            applyPendingReviewState(profile, now);
            campusCourierProfileMapper.insert(profile);
            return;
        }

        mutator.apply(existing);
        applyPendingReviewState(existing, now);
        campusCourierProfileMapper.updateSubmittedProfile(existing);
    }

    private void applyFullSubmittedFields(CampusCourierProfile profile, CampusCourierProfileSubmitDTO dto, LocalDateTime now) {
        profile.setRealName(normalizeText(dto.getRealName()));
        profile.setStudentNo(normalizeText(dto.getStudentNo()));
        profile.setCollege(normalizeText(dto.getCollege()));
        profile.setMajor(normalizeText(dto.getMajor()));
        profile.setClassName(normalizeText(dto.getClassName()));
        profile.setDormitoryBuilding(normalizeText(dto.getDormitoryBuilding()));
        profile.setDormitoryRoom(normalizeText(dto.getDormitoryRoom()));
        profile.setIdCardLast4(normalizeText(dto.getIdCardLast4()));
        profile.setEmergencyContactName(normalizeText(dto.getEmergencyContactName()));
        profile.setEmergencyContactPhone(normalizeText(dto.getEmergencyContactPhone()));
        profile.setVerificationPhotoUrl(normalizeText(dto.getVerificationPhotoUrl()));
        profile.setScheduleAttachmentUrl(normalizeText(dto.getScheduleAttachmentUrl()));
        if (!StringUtils.hasText(profile.getCampusZone()) && !CampusRuleCatalog.CAMPUS_ZONES.isEmpty()) {
            profile.setCampusZone(CampusRuleCatalog.CAMPUS_ZONES.get(0));
        }
        if (profile.getEnabledWorkInOwnBuilding() == null) {
            profile.setEnabledWorkInOwnBuilding(ENABLED);
        }
        profile.setUpdatedAt(now);
    }

    private void applyOnboardingSubmittedFields(CampusCourierProfile profile, CampusCustomerCourierOnboardingSubmitDTO dto, LocalDateTime now) {
        profile.setRealName(normalizeText(dto.getRealName()));
        profile.setGender(normalizeGender(dto.getGender()));
        profile.setCampusZone(normalizeText(dto.getCampusZone()));
        profile.setStudentNo(normalizeText(dto.getStudentNo()));
        profile.setDormitoryBuilding(normalizeText(dto.getDormBuilding()));
        profile.setEnabledWorkInOwnBuilding(dto.getEnabledWorkInOwnBuilding());
        profile.setApplicantRemark(normalizeText(dto.getRemark()));
        profile.setEmergencyContactName(normalizeText(dto.getEmergencyContactName()));
        profile.setEmergencyContactPhone(normalizeText(dto.getEmergencyContactPhone()));
        profile.setUpdatedAt(now);
    }

    private void applyPendingReviewState(CampusCourierProfile profile, LocalDateTime now) {
        profile.setReviewStatus(CampusCourierReviewStatus.PENDING.name());
        profile.setReviewComment(PENDING_REVIEW_COMMENT);
        profile.setReviewedByEmployeeId(null);
        profile.setReviewedAt(null);
        profile.setEnabled(DISABLED);
        profile.setUpdatedAt(now);
    }

    private CampusCustomerCourierOnboardingProfileVO buildOnboardingProfile(CampusCourierProfileVO profileVO, User user) {
        CampusCustomerCourierOnboardingProfileVO response = new CampusCustomerCourierOnboardingProfileVO();
        response.setProfileId(profileVO.getId());
        response.setUserId(profileVO.getUserId());
        response.setRealName(profileVO.getRealName());
        response.setPhone(StringUtils.hasText(profileVO.getPhone()) ? profileVO.getPhone() : user.getPhone());
        response.setGender(profileVO.getGender());
        response.setStudentNo(profileVO.getStudentNo());
        response.setCampusZone(profileVO.getCampusZone());
        response.setDormBuilding(profileVO.getDormitoryBuilding());
        response.setEnabledWorkInOwnBuilding(defaultOwnBuildingFlag(profileVO.getEnabledWorkInOwnBuilding()));
        response.setRemark(profileVO.getApplicantRemark());
        response.setEmergencyContactName(profileVO.getEmergencyContactName());
        response.setEmergencyContactPhone(profileVO.getEmergencyContactPhone());
        response.setReviewStatus(profileVO.getReviewStatus());
        response.setReviewRemark(profileVO.getReviewComment());
        response.setEnabled(profileVO.getEnabled());
        response.setCreatedAt(profileVO.getCreatedAt());
        response.setUpdatedAt(profileVO.getUpdatedAt());
        return response;
    }

    private CampusCustomerCourierOnboardingProfileVO buildEmptyOnboardingProfile(User user) {
        CampusCustomerCourierOnboardingProfileVO response = new CampusCustomerCourierOnboardingProfileVO();
        response.setUserId(user.getId());
        response.setRealName(user.getName());
        response.setPhone(user.getPhone());
        response.setCampusZone(CampusRuleCatalog.CAMPUS_ZONES.isEmpty() ? null : CampusRuleCatalog.CAMPUS_ZONES.get(0));
        response.setEnabledWorkInOwnBuilding(ENABLED);
        response.setEnabled(DISABLED);
        response.setReviewRemark(NOT_SUBMITTED_REMARK);
        return response;
    }

    private CampusCourierProfileVO requireDetailProfile(Long courierUserId, User user) {
        CampusCourierProfileVO profileVO = campusCourierProfileMapper.selectDetailByUserId(courierUserId);
        if (profileVO == null) {
            throw new BusinessException(404, "配送员资料不存在");
        }
        if (!StringUtils.hasText(profileVO.getPhone())) {
            profileVO.setPhone(user.getPhone());
        }
        return profileVO;
    }

    private void validateSubmitRequest(CampusCourierProfileSubmitDTO dto) {
        if (dto == null) {
            throw new BusinessException("请求体不能为空");
        }
        requireText(dto.getRealName(), "真实姓名不能为空");
        requireText(dto.getStudentNo(), "学号不能为空");
        requireText(dto.getCollege(), "学院不能为空");
        requireText(dto.getMajor(), "专业不能为空");
        requireText(dto.getClassName(), "班级不能为空");
        requireText(dto.getDormitoryBuilding(), "宿舍楼栋不能为空");
        requireText(dto.getDormitoryRoom(), "宿舍房间不能为空");
        requireText(dto.getIdCardLast4(), "身份证后四位不能为空");
        requireText(dto.getEmergencyContactName(), "紧急联系人不能为空");
        requireText(dto.getEmergencyContactPhone(), "紧急联系人电话不能为空");
        requireText(dto.getVerificationPhotoUrl(), "认证照片不能为空");
        requireText(dto.getScheduleAttachmentUrl(), "课程表附件不能为空");

        if (!CampusRuleCatalog.DORMITORY_BUILDINGS.contains(normalizeText(dto.getDormitoryBuilding()))) {
            throw new BusinessException("宿舍楼栋不在支持范围内");
        }

        String idCardLast4 = normalizeText(dto.getIdCardLast4());
        if (!idCardLast4.matches("\\d{4}")) {
            throw new BusinessException("身份证后四位必须为 4 位数字");
        }

        String emergencyContactPhone = normalizeText(dto.getEmergencyContactPhone());
        if (!emergencyContactPhone.matches("1\\d{10}")) {
            throw new BusinessException("紧急联系人电话格式不正确");
        }

        assertControlledFileReference(dto.getVerificationPhotoUrl(), "认证照片路径必须为受控文件路径");
        assertControlledFileReference(dto.getScheduleAttachmentUrl(), "课程表附件路径必须为受控文件路径");
    }

    private void validateOnboardingSubmitRequest(CampusCustomerCourierOnboardingSubmitDTO dto, User user) {
        if (dto == null) {
            throw new BusinessException("请求体不能为空");
        }
        requireText(dto.getRealName(), "真实姓名不能为空");
        requireText(dto.getPhone(), "手机号不能为空");
        requireText(dto.getGender(), "性别不能为空");
        requireText(dto.getStudentNo(), "学号不能为空");
        requireText(dto.getCampusZone(), "校区不能为空");
        requireText(dto.getDormBuilding(), "宿舍楼栋不能为空");
        requireText(dto.getEmergencyContactName(), "紧急联系人不能为空");
        requireText(dto.getEmergencyContactPhone(), "紧急联系人电话不能为空");

        String phone = normalizeText(dto.getPhone());
        if (!phone.matches("1\\d{10}")) {
            throw new BusinessException("手机号格式不正确");
        }
        if (!phone.equals(user.getPhone())) {
            throw new BusinessException("手机号需与当前登录账号一致");
        }

        String gender = normalizeGender(dto.getGender());
        if (!ALLOWED_GENDERS.contains(gender)) {
            throw new BusinessException("性别仅支持 MALE、FEMALE、OTHER");
        }
        if (!CampusRuleCatalog.CAMPUS_ZONES.contains(normalizeText(dto.getCampusZone()))) {
            throw new BusinessException("校区不在支持范围内");
        }
        if (!CampusRuleCatalog.DORMITORY_BUILDINGS.contains(normalizeText(dto.getDormBuilding()))) {
            throw new BusinessException("宿舍楼栋不在支持范围内");
        }
        if (dto.getEnabledWorkInOwnBuilding() == null
                || (dto.getEnabledWorkInOwnBuilding() != ENABLED && dto.getEnabledWorkInOwnBuilding() != DISABLED)) {
            throw new BusinessException("是否优先接本楼栋订单字段不合法");
        }

        String emergencyContactPhone = normalizeText(dto.getEmergencyContactPhone());
        if (!emergencyContactPhone.matches("1\\d{10}")) {
            throw new BusinessException("紧急联系人电话格式不正确");
        }
    }

    private void assertControlledFileReference(String value, String message) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized) || !normalized.startsWith(CONTROLLED_FILE_PREFIX)) {
            throw new BusinessException(message);
        }
    }

    private User requireActiveUser(Long userId) {
        if (userId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() == null || user.getStatus() != ENABLED) {
            throw new BusinessException("用户不存在或已禁用");
        }
        return user;
    }

    private CampusCourierReviewStatus resolveReviewStatus(String reviewStatus) {
        try {
            return CampusCourierReviewStatus.valueOf(normalizeText(reviewStatus).toUpperCase());
        } catch (Exception ex) {
            throw new BusinessException("不支持的审核状态");
        }
    }

    private boolean canApplyCourierToken(String reviewStatus, Integer enabled) {
        return CampusCourierReviewStatus.APPROVED.name().equals(reviewStatus) && ENABLED == (enabled == null ? DISABLED : enabled);
    }

    private String resolveTokenEligibilityMessage(String reviewStatus, Integer enabled) {
        if (!StringUtils.hasText(reviewStatus)) {
            return NOT_SUBMITTED_REMARK;
        }
        if (CampusCourierReviewStatus.APPROVED.name().equals(reviewStatus) && ENABLED == (enabled == null ? DISABLED : enabled)) {
            return ELIGIBLE_MESSAGE;
        }
        if (CampusCourierReviewStatus.PENDING.name().equals(reviewStatus)) {
            return PENDING_MESSAGE;
        }
        if (CampusCourierReviewStatus.REJECTED.name().equals(reviewStatus)) {
            return REJECTED_MESSAGE;
        }
        if (CampusCourierReviewStatus.DISABLED.name().equals(reviewStatus)
                || CampusCourierReviewStatus.APPROVED.name().equals(reviewStatus)) {
            return DISABLED_MESSAGE;
        }
        return UNKNOWN_STATUS_MESSAGE;
    }

    private int defaultOwnBuildingFlag(Integer value) {
        return value == null ? ENABLED : value;
    }

    private int safePositive(Integer value, int defaultValue) {
        return value == null || value < 1 ? defaultValue : value;
    }

    private int safePageSize(Integer pageSize, Integer size) {
        int resolved = size != null ? size : (pageSize != null ? pageSize : 10);
        resolved = resolved < 1 ? 10 : resolved;
        return Math.min(resolved, 100);
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeGender(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : normalized.toUpperCase();
    }

    private void requireText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(message);
        }
    }

    @FunctionalInterface
    private interface ProfileMutatorFactory {
        ProfileMutator create(LocalDateTime now);
    }

    @FunctionalInterface
    private interface ProfileMutator {
        void apply(CampusCourierProfile profile);
    }
}

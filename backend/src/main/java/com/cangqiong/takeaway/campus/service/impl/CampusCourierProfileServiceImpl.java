package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.dto.CampusCourierProfileSubmitDTO;
import com.cangqiong.takeaway.campus.dto.CampusCourierReviewDTO;
import com.cangqiong.takeaway.campus.entity.CampusCourierProfile;
import com.cangqiong.takeaway.campus.enums.CampusCourierReviewStatus;
import com.cangqiong.takeaway.campus.mapper.CampusCourierProfileMapper;
import com.cangqiong.takeaway.campus.query.CampusCourierQuery;
import com.cangqiong.takeaway.campus.service.CampusCourierProfileService;
import com.cangqiong.takeaway.campus.support.CampusRuleCatalog;
import com.cangqiong.takeaway.campus.vo.CampusCourierProfileVO;
import com.cangqiong.takeaway.campus.vo.CampusCourierReviewStatusVO;
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

@Service
public class CampusCourierProfileServiceImpl implements CampusCourierProfileService {

    private static final int ENABLED = 1;
    private static final int DISABLED = 0;
    private static final String CONTROLLED_FILE_PREFIX = "/api/files/";
    private static final String PENDING_REVIEW_COMMENT = "待人工审核";

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

        LocalDateTime now = LocalDateTime.now();
        CampusCourierProfile existing = campusCourierProfileMapper.selectByUserId(courierUserId);
        if (existing == null) {
            CampusCourierProfile profile = buildSubmittedProfile(dto, courierUserId, now);
            campusCourierProfileMapper.insert(profile);
        } else {
            applySubmittedFields(existing, dto, now);
            campusCourierProfileMapper.updateSubmittedProfile(existing);
        }

        CampusCourierProfileVO profileVO = campusCourierProfileMapper.selectDetailByUserId(courierUserId);
        if (profileVO == null) {
            throw new BusinessException(404, "配送员资料不存在");
        }
        if (!StringUtils.hasText(profileVO.getPhone())) {
            profileVO.setPhone(user.getPhone());
        }
        return profileVO;
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

    private CampusCourierProfile buildSubmittedProfile(CampusCourierProfileSubmitDTO dto, Long courierUserId, LocalDateTime now) {
        CampusCourierProfile profile = new CampusCourierProfile();
        profile.setUserId(courierUserId);
        applySubmittedFields(profile, dto, now);
        profile.setCreatedAt(now);
        return profile;
    }

    private void applySubmittedFields(CampusCourierProfile profile, CampusCourierProfileSubmitDTO dto, LocalDateTime now) {
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
        profile.setReviewStatus(CampusCourierReviewStatus.PENDING.name());
        profile.setReviewComment(PENDING_REVIEW_COMMENT);
        profile.setReviewedByEmployeeId(null);
        profile.setReviewedAt(null);
        profile.setEnabled(DISABLED);
        profile.setUpdatedAt(now);
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

    private void requireText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(message);
        }
    }
}

package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.dto.CampusCourierReviewDTO;
import com.cangqiong.takeaway.campus.query.CampusCourierQuery;
import com.cangqiong.takeaway.campus.service.CampusCourierProfileService;
import com.cangqiong.takeaway.campus.vo.CampusCourierProfileVO;
import com.cangqiong.takeaway.interceptor.BaseContext;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/campus/admin/couriers")
public class CampusAdminCourierController {

    @Autowired
    private CampusCourierProfileService campusCourierProfileService;

    @GetMapping
    public Result<PageResult<CampusCourierProfileVO>> pageQuery(CampusCourierQuery query) {
        log.info("校园配送员分页查询: {}", query);
        return Result.success(campusCourierProfileService.pageQuery(query));
    }

    @PostMapping("/{id}/review")
    public Result<Void> review(@PathVariable Long id, @RequestBody CampusCourierReviewDTO dto) {
        Long employeeId = BaseContext.getCurrentUserId();
        log.info("管理员审核校园配送员资料: employeeId={}, profileId={}, dto={}", employeeId, id, dto);
        campusCourierProfileService.reviewByAdmin(id, dto, employeeId);
        return Result.success();
    }
}

package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.service.CampusPickupPointService;
import com.cangqiong.takeaway.campus.support.CampusRuleCatalog;
import com.cangqiong.takeaway.campus.vo.CampusDeliveryRuleVO;
import com.cangqiong.takeaway.campus.vo.CampusPickupPointVO;
import com.cangqiong.takeaway.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/campus/public")
public class CampusPublicController {

    @Autowired
    private CampusPickupPointService campusPickupPointService;

    @GetMapping("/pickup-points")
    public Result<List<CampusPickupPointVO>> pickupPoints() {
        log.info("查询校园取餐点");
        return Result.success(campusPickupPointService.listEnabledPickupPoints());
    }

    @GetMapping("/delivery-rules")
    public Result<CampusDeliveryRuleVO> deliveryRules() {
        log.info("查询校园配送规则");
        return Result.success(CampusRuleCatalog.buildDeliveryRuleVO());
    }

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        return Result.success(Map.of(
                "status", "UP",
                "service", "campus-part-time-job",
                "checkedAt", OffsetDateTime.now().toString()
        ));
    }
}

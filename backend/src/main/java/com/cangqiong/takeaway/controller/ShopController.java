package com.cangqiong.takeaway.controller;

import com.cangqiong.takeaway.dto.BusinessHourDTO;
import com.cangqiong.takeaway.dto.ShopStatusDTO;
import com.cangqiong.takeaway.entity.ShopBusinessHour;
import com.cangqiong.takeaway.entity.ShopConfig;
import com.cangqiong.takeaway.mapper.ShopConfigMapper;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.ShopBusinessHourVO;
import com.cangqiong.takeaway.vo.ShopStatusVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class ShopController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ShopConfigMapper shopConfigMapper;

    @GetMapping("/api/public/shop/status")
    public Result<ShopStatusVO> getPublicStatus() {
        return Result.success(buildShopStatus());
    }

    @GetMapping("/api/shop/status")
    public Result<ShopStatusVO> getStatus() {
        return Result.success(buildShopStatus());
    }

    @PutMapping("/api/shop/status")
    public Result<Void> updateStatus(@RequestBody ShopStatusDTO dto) {
        ShopConfig config = new ShopConfig();
        config.setId(1L);
        config.setIsOpen(Boolean.TRUE.equals(dto.getIsOpen()) ? 1 : 0);
        config.setRestNotice(dto.getRestNotice());
        shopConfigMapper.updateConfig(config);

        if (dto.getBusinessHours() != null) {
            for (BusinessHourDTO item : dto.getBusinessHours()) {
                ShopBusinessHour hour = new ShopBusinessHour();
                hour.setDayKey(item.getDay());
                hour.setDayName(item.getDayName());
                hour.setIsOpen(Boolean.TRUE.equals(item.getIsOpen()) ? 1 : 0);
                hour.setOpenTime(item.getOpenTime());
                hour.setCloseTime(item.getCloseTime());
                shopConfigMapper.updateBusinessHour(hour);
            }
        }

        return Result.success();
    }

    private ShopStatusVO buildShopStatus() {
        ShopConfig config = shopConfigMapper.selectConfig();
        List<ShopBusinessHourVO> hours = shopConfigMapper.selectBusinessHours().stream().map(item -> {
            ShopBusinessHourVO vo = new ShopBusinessHourVO();
            vo.setDay(item.getDayKey());
            vo.setDayName(item.getDayName());
            vo.setIsOpen(item.getIsOpen() != null && item.getIsOpen() == 1);
            vo.setOpenTime(item.getOpenTime());
            vo.setCloseTime(item.getCloseTime());
            return vo;
        }).toList();

        ShopStatusVO vo = new ShopStatusVO();
        vo.setIsOpen(config != null && config.getIsOpen() != null && config.getIsOpen() == 1);
        vo.setRestNotice(config == null ? "" : config.getRestNotice());
        vo.setLastUpdated(config == null || config.getLastUpdated() == null ? "" : config.getLastUpdated().format(DATE_TIME_FORMATTER));
        vo.setBusinessHours(hours);
        return vo;
    }
}

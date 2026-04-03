package com.cangqiong.takeaway.controller;

import com.cangqiong.takeaway.dto.AddressDTO;
import com.cangqiong.takeaway.entity.Address;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.interceptor.BaseContext;
import com.cangqiong.takeaway.mapper.AddressMapper;
import com.cangqiong.takeaway.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class AddressController {

    @Autowired
    private AddressMapper addressMapper;

    @GetMapping("/api/user/addresses")
    public Result<List<Address>> list() {
        return Result.success(addressMapper.selectByUserId(BaseContext.getCurrentUserId()));
    }

    @PostMapping("/api/user/addresses")
    public Result<Long> add(@RequestBody AddressDTO dto) {
        Long userId = BaseContext.getCurrentUserId();
        Address address = new Address();
        BeanUtils.copyProperties(dto, address);
        address.setUserId(userId);
        address.setIsDefault(dto.getIsDefault() != null && dto.getIsDefault() == 1 ? 1 : 0);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        if (address.getIsDefault() == 1 || addressMapper.countByUserId(userId) == 0) {
            addressMapper.clearDefault(userId);
            address.setIsDefault(1);
        }
        addressMapper.insert(address);
        return Result.success(address.getId());
    }

    @PutMapping("/api/user/addresses/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AddressDTO dto) {
        Long userId = BaseContext.getCurrentUserId();
        Address existing = addressMapper.selectById(id, userId);
        if (existing == null) {
            throw new BusinessException("地址不存在");
        }

        Address address = new Address();
        BeanUtils.copyProperties(dto, address);
        address.setId(id);
        address.setUserId(userId);
        address.setIsDefault(dto.getIsDefault() != null && dto.getIsDefault() == 1 ? 1 : existing.getIsDefault());
        if (address.getIsDefault() == 1) {
            addressMapper.clearDefault(userId);
        }
        addressMapper.update(address);
        return Result.success();
    }

    @DeleteMapping("/api/user/addresses/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressMapper.deleteById(id, BaseContext.getCurrentUserId());
        return Result.success();
    }

    @PutMapping("/api/user/addresses/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id) {
        Long userId = BaseContext.getCurrentUserId();
        Address existing = addressMapper.selectById(id, userId);
        if (existing == null) {
            throw new BusinessException("地址不存在");
        }
        addressMapper.clearDefault(userId);
        addressMapper.setDefault(id, userId);
        return Result.success();
    }
}

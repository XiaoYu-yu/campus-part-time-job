package com.cangqiong.takeaway.controller;

import com.cangqiong.takeaway.dto.CartItemDTO;
import com.cangqiong.takeaway.dto.CartItemUpdateDTO;
import com.cangqiong.takeaway.entity.Cart;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.interceptor.BaseContext;
import com.cangqiong.takeaway.mapper.CartMapper;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.CartItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartMapper cartMapper;

    @GetMapping("/api/user/cart")
    public Result<List<CartItemVO>> list() {
        return Result.success(cartMapper.selectByUserId(BaseContext.getCurrentUserId()));
    }

    @GetMapping("/api/user/cart/count")
    public Result<Long> count() {
        return Result.success(cartMapper.countByUserId(BaseContext.getCurrentUserId()));
    }

    @PostMapping("/api/user/cart")
    public Result<Void> add(@RequestBody CartItemDTO dto) {
        Long userId = BaseContext.getCurrentUserId();
        if (dto.getDishId() == null && dto.getSetmealId() == null) {
            throw new BusinessException("请选择要加入购物车的商品");
        }

        Cart existing = cartMapper.selectExisting(userId, dto.getDishId(), dto.getSetmealId());
        int quantity = dto.getQuantity() == null || dto.getQuantity() < 1 ? 1 : dto.getQuantity();
        if (existing != null) {
            cartMapper.increaseQuantity(existing.getId(), quantity);
            return Result.success();
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setDishId(dto.getDishId());
        cart.setSetmealId(dto.getSetmealId());
        cart.setQuantity(quantity);
        cart.setChecked(1);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cartMapper.insert(cart);
        return Result.success();
    }

    @PutMapping("/api/user/cart/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CartItemUpdateDTO dto) {
        Long userId = BaseContext.getCurrentUserId();
        Cart cart = new Cart();
        cart.setId(id);
        cart.setUserId(userId);
        cart.setQuantity(dto.getQuantity() == null || dto.getQuantity() < 1 ? 1 : dto.getQuantity());
        cart.setChecked(dto.getChecked() == null ? 1 : dto.getChecked());
        cartMapper.update(cart);
        return Result.success();
    }

    @DeleteMapping("/api/user/cart/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cartMapper.deleteById(id, BaseContext.getCurrentUserId());
        return Result.success();
    }

    @DeleteMapping("/api/user/cart/clear")
    public Result<Void> clear() {
        cartMapper.deleteByUserId(BaseContext.getCurrentUserId());
        return Result.success();
    }
}

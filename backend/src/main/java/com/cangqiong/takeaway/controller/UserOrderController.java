package com.cangqiong.takeaway.controller;

import com.cangqiong.takeaway.dto.OrderDTO;
import com.cangqiong.takeaway.dto.OrderItemDTO;
import com.cangqiong.takeaway.dto.UserSubmitOrderDTO;
import com.cangqiong.takeaway.entity.Address;
import com.cangqiong.takeaway.entity.Cart;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.interceptor.BaseContext;
import com.cangqiong.takeaway.mapper.AddressMapper;
import com.cangqiong.takeaway.mapper.CartMapper;
import com.cangqiong.takeaway.mapper.OrderItemMapper;
import com.cangqiong.takeaway.mapper.OrderMapper;
import com.cangqiong.takeaway.service.OrderService;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.CartItemVO;
import com.cangqiong.takeaway.vo.OrderItemVO;
import com.cangqiong.takeaway.vo.OrderVO;
import com.cangqiong.takeaway.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private AddressMapper addressMapper;

    @GetMapping("/api/user/orders")
    public Result<PageResult<OrderVO>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = BaseContext.getCurrentUserId();
        int safePage = page == null || page < 1 ? 1 : page;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int offset = (safePage - 1) * safePageSize;
        List<OrderVO> records = orderMapper.selectByUserCondition(userId, status, offset, safePageSize);
        for (OrderVO record : records) {
            record.setItems(orderItemMapper.selectByOrderId(record.getId()));
        }

        Long total = orderMapper.countByUserCondition(userId, status);
        PageResult<OrderVO> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setCurrent((long) safePage);
        result.setSize((long) safePageSize);
        result.setPages((total + safePageSize - 1) / safePageSize);
        return Result.success(result);
    }

    @GetMapping("/api/user/orders/{id}")
    public Result<OrderVO> getById(@PathVariable String id) {
        OrderVO orderVO = orderService.getById(id);
        if (!BaseContext.getCurrentUserId().equals(orderVO.getUserId())) {
            throw new BusinessException("无权查看该订单");
        }
        return Result.success(orderVO);
    }

    @PostMapping("/api/user/orders")
    public Result<String> submit(@RequestBody UserSubmitOrderDTO dto) {
        Long userId = BaseContext.getCurrentUserId();
        Address address = dto.getAddressId() == null ? addressMapper.selectDefaultByUserId(userId) : addressMapper.selectById(dto.getAddressId(), userId);
        if (address == null) {
            throw new BusinessException("请先选择收货地址");
        }

        List<CartItemVO> checkedItems = cartMapper.selectByUserId(userId).stream()
                .filter(item -> item.getChecked() != null && item.getChecked() == 1)
                .toList();
        if (checkedItems.isEmpty()) {
            throw new BusinessException("请先勾选要结算的商品");
        }

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCustomerName(address.getConsignee());
        orderDTO.setCustomerPhone(address.getPhone());
        orderDTO.setCustomerAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetail());

        List<OrderItemDTO> items = new ArrayList<>();
        List<Long> cartIds = new ArrayList<>();
        for (CartItemVO cartItem : checkedItems) {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setDishId(cartItem.getDishId());
            itemDTO.setSetmealId(cartItem.getSetmealId());
            itemDTO.setQuantity(cartItem.getQuantity());
            items.add(itemDTO);
            cartIds.add(cartItem.getId());
        }
        orderDTO.setItems(items);

        String orderId = orderService.create(orderDTO);
        cartMapper.deleteByIds(userId, cartIds);
        return Result.success(orderId);
    }

    @PutMapping("/api/user/orders/{id}/pay")
    public Result<Void> pay(@PathVariable String id) {
        assertUserOwnsOrder(id);
        orderService.pay(id);
        return Result.success();
    }

    @PutMapping("/api/user/orders/{id}/cancel")
    public Result<Void> cancel(@PathVariable String id) {
        assertUserOwnsOrder(id);
        orderService.cancel(id);
        return Result.success();
    }

    @PostMapping("/api/user/orders/{id}/reorder")
    public Result<Void> reorder(@PathVariable String id) {
        OrderVO orderVO = assertUserOwnsOrder(id);
        for (OrderItemVO item : orderVO.getItems()) {
            Cart existing = cartMapper.selectExisting(BaseContext.getCurrentUserId(), item.getDishId(), item.getSetmealId());
            if (existing != null) {
                cartMapper.increaseQuantity(existing.getId(), item.getQuantity());
            } else {
                Cart cart = new Cart();
                cart.setUserId(BaseContext.getCurrentUserId());
                cart.setDishId(item.getDishId());
                cart.setSetmealId(item.getSetmealId());
                cart.setQuantity(item.getQuantity());
                cart.setChecked(1);
                cart.setCreatedAt(java.time.LocalDateTime.now());
                cart.setUpdatedAt(java.time.LocalDateTime.now());
                cartMapper.insert(cart);
            }
        }
        return Result.success();
    }

    private OrderVO assertUserOwnsOrder(String id) {
        OrderVO orderVO = orderService.getById(id);
        if (!BaseContext.getCurrentUserId().equals(orderVO.getUserId())) {
            throw new BusinessException("无权操作该订单");
        }
        return orderVO;
    }
}

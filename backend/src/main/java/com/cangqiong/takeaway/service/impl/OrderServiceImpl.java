package com.cangqiong.takeaway.service.impl;

import com.cangqiong.takeaway.dto.OrderDTO;
import com.cangqiong.takeaway.dto.OrderItemDTO;
import com.cangqiong.takeaway.entity.Order;
import com.cangqiong.takeaway.entity.OrderItem;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.interceptor.BaseContext;
import com.cangqiong.takeaway.mapper.DishMapper;
import com.cangqiong.takeaway.mapper.OrderItemMapper;
import com.cangqiong.takeaway.mapper.OrderMapper;
import com.cangqiong.takeaway.mapper.SetmealMapper;
import com.cangqiong.takeaway.query.OrderQuery;
import com.cangqiong.takeaway.service.OrderService;
import com.cangqiong.takeaway.vo.DishVO;
import com.cangqiong.takeaway.vo.OrderItemVO;
import com.cangqiong.takeaway.vo.OrderVO;
import com.cangqiong.takeaway.vo.PageResult;
import com.cangqiong.takeaway.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public PageResult<OrderVO> pageQuery(OrderQuery query) {
        log.info("订单分页查询，参数：{}", query);

        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        int offset = (page - 1) * pageSize;
        List<OrderVO> records = orderMapper.selectByCondition(
                query.getOrderNo(),
                query.getCustomerName(),
                query.getStatus(),
                query.getBeginTime(),
                query.getEndTime(),
                offset,
                pageSize
        );

        for (OrderVO orderVO : records) {
            List<OrderItemVO> items = orderItemMapper.selectByOrderId(orderVO.getId());
            orderVO.setItems(items);
        }

        Long total = orderMapper.countByCondition(
                query.getOrderNo(),
                query.getCustomerName(),
                query.getStatus(),
                query.getBeginTime(),
                query.getEndTime()
        );

        PageResult<OrderVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        pageResult.setSize(Long.valueOf(pageSize));
        pageResult.setCurrent(Long.valueOf(page));
        pageResult.setPages((total + pageSize - 1) / pageSize);

        return pageResult;
    }

    @Override
    public OrderVO getById(String id) {
        log.info("获取订单详情，ID：{}", id);

        OrderVO orderVO = orderMapper.selectById(id);
        if (orderVO == null) {
            throw new BusinessException("订单不存在");
        }

        List<OrderItemVO> items = orderItemMapper.selectByOrderId(id);
        orderVO.setItems(items);

        return orderVO;
    }

    @Override
    @Transactional
    public String create(OrderDTO dto) {
        log.info("创建订单：{}", dto);

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("订单商品不能为空");
        }

        String orderId = generateOrderNumber();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDTO itemDTO : dto.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setDishId(itemDTO.getDishId());
            orderItem.setSetmealId(itemDTO.getSetmealId());
            orderItem.setQuantity(itemDTO.getQuantity());

            if (itemDTO.getDishId() != null) {
                DishVO dishVO = dishMapper.selectById(itemDTO.getDishId());
                if (dishVO == null) {
                    throw new BusinessException("菜品不存在：" + itemDTO.getDishId());
                }
                orderItem.setName(dishVO.getName());
                orderItem.setPrice(dishVO.getPrice());
            } else if (itemDTO.getSetmealId() != null) {
                SetmealVO setmealVO = setmealMapper.selectById(itemDTO.getSetmealId());
                if (setmealVO == null) {
                    throw new BusinessException("套餐不存在：" + itemDTO.getSetmealId());
                }
                orderItem.setName(setmealVO.getName());
                orderItem.setPrice(setmealVO.getPrice());
            } else {
                throw new BusinessException("商品ID不能为空");
            }

            BigDecimal itemTotal = orderItem.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            orderItem.setTotal(itemTotal);
            totalAmount = totalAmount.add(itemTotal);

            orderItems.add(orderItem);
        }

        Order order = new Order();
        order.setId(orderId);
        Long currentUserId = BaseContext.getCurrentUserId();
        order.setUserId(currentUserId != null ? currentUserId : 1L);
        order.setCustomerName(dto.getCustomerName());
        order.setCustomerPhone(dto.getCustomerPhone());
        order.setCustomerAddress(dto.getCustomerAddress());
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.insert(order);
        orderItemMapper.batchInsert(orderItems);

        log.info("订单创建成功，订单号：{}，总金额：{}", orderId, totalAmount);

        return orderId;
    }

    @Override
    @Transactional
    public void updateStatus(String id, Integer status) {
        log.info("更新订单状态，ID：{}，状态：{}", id, status);

        OrderVO existingOrder = orderMapper.selectById(id);
        if (existingOrder == null) {
            throw new BusinessException("订单不存在");
        }

        orderMapper.updateStatus(id, status);
        log.info("订单状态更新成功，ID：{}，状态：{}", id, status);
    }

    @Override
    @Transactional
    public void pay(String id) {
        log.info("支付订单，ID：{}", id);

        OrderVO existingOrder = orderMapper.selectById(id);
        if (existingOrder == null) {
            throw new BusinessException("订单不存在");
        }

        if (existingOrder.getStatus() != 0) {
            throw new BusinessException("订单状态不正确，无法支付");
        }

        orderMapper.updatePaymentTime(id, LocalDateTime.now(), 1);
        log.info("订单支付成功，ID：{}", id);
    }

    @Override
    @Transactional
    public void cancel(String id) {
        log.info("取消订单，ID：{}", id);

        OrderVO existingOrder = orderMapper.selectById(id);
        if (existingOrder == null) {
            throw new BusinessException("订单不存在");
        }

        if (existingOrder.getStatus() == 2) {
            throw new BusinessException("订单已配送，无法取消");
        }

        if (existingOrder.getStatus() == 3) {
            throw new BusinessException("订单已完成，无法取消");
        }

        orderMapper.updateStatus(id, 4);
        log.info("订单取消成功，ID：{}", id);
    }

    private String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String dateTime = now.format(formatter);
        String random = String.format("%06d", new Random().nextInt(1000000));
        return dateTime + random;
    }
}

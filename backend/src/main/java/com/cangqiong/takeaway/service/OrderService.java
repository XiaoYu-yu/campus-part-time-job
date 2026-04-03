package com.cangqiong.takeaway.service;

import com.cangqiong.takeaway.dto.OrderDTO;
import com.cangqiong.takeaway.query.OrderQuery;
import com.cangqiong.takeaway.vo.OrderVO;
import com.cangqiong.takeaway.vo.PageResult;

/**
 * 订单服务接口
 * 定义订单相关的业务逻辑操作
 */
public interface OrderService {

    /**
     * 分页查询订单列表
     * @param query 查询参数
     * @return 订单分页结果
     */
    PageResult<OrderVO> pageQuery(OrderQuery query);

    /**
     * 根据 ID 获取订单详情
     * @param id 订单 ID
     * @return 订单详情
     */
    OrderVO getById(String id);

    /**
     * 创建订单
     * @param dto 订单信息
     * @return 创建的订单 ID
     */
    String create(OrderDTO dto);

    /**
     * 更新订单状态
     * @param id 订单 ID
     * @param status 订单状态
     */
    void updateStatus(String id, Integer status);

    /**
     * 支付订单
     * @param id 订单 ID
     */
    void pay(String id);

    /**
     * 取消订单
     * @param id 订单 ID
     */
    void cancel(String id);
}

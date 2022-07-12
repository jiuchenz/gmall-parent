package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface OrderInfoService extends IService<OrderInfo> {


    void saveDetail(OrderInfo orderInfo, OrderSubmitVo order);

    void updateOrderStatus(Long orderId, Long userId, String name, String name1, String name2);

    OrderInfo getOrderInfoByIdAndUserId(Long id);
}

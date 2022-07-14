package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.model.vo.ware.OrderSpiltVo;
import com.atguigu.gmall.model.vo.ware.OrderSplitRespVo;
import com.atguigu.gmall.model.vo.ware.WareFenBuVo;

import java.util.List;

public interface OrderBizService {
    OrderConfirmVo getOrderConfirmData();

    /**
     * 生成一个交易令牌
     * @return
     */
    String generateTradeToken();

    boolean checkTradeToken(String token);

    Long submitOrder(String tradeNo, OrderSubmitVo order);

    OrderInfo saveOrder(String tradeNo, OrderSubmitVo order);

    void closeOrder(Long orderId, Long userId);

    List<OrderSplitRespVo> splitOrder(OrderSpiltVo vo);

    OrderSplitRespVo saveChildOrder(OrderInfo orderInfo, WareFenBuVo buVo);
}

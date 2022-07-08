package com.atguigu.gmall.front.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    OrderFeignClient orderFeignClient;

    @GetMapping("/trade.html")
    public String orderConfirmPage(Model model){
        Result<Map<String, Object>> mapResult = orderFeignClient.getOrderConfirmData();
        model.addAllAttributes(mapResult.getData());
        return "order/trade";
    }
}

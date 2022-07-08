package com.atguigu.gmall.order.rpc;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.order.service.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rpc/inner/order")
public class OrderRpcController {

    @Autowired
    OrderBizService orderBizServicel;

    @GetMapping("/confirm/data")
    public Result<OrderConfirmVo> getOrderConfirmData(){
        OrderConfirmVo vo = orderBizServicel.getOrderConfirmData();
        return Result.ok(vo);
    }
}

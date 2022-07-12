package com.atguigu.gmall.order.rpc;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.order.service.OrderBizService;
import com.atguigu.gmall.order.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rpc/inner/order")
public class OrderRpcController {

    @Autowired
    OrderBizService orderBizServicel;

    @Autowired
    OrderInfoService orderInfoService;

    @GetMapping("/confirm/data")
    public Result<OrderConfirmVo> getOrderConfirmData(){
        OrderConfirmVo vo = orderBizServicel.getOrderConfirmData();
        return Result.ok(vo);
    }


    /**
     * 获取某个用户的指定订单信息
     * @param id
     * @return
     */
    @GetMapping("/info/{id}")
    public Result<OrderInfo> getOrderInfoByIdAndUserId(@PathVariable("id") Long id){
        OrderInfo orderInfo = orderInfoService.getOrderInfoByIdAndUserId(id);
        return Result.ok(orderInfo);
    }
}

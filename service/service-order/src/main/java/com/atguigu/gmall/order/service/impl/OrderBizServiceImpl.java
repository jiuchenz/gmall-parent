package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.CartOrderDetailVo;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.order.service.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class OrderBizServiceImpl implements OrderBizService {

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    SkuFeignClient skuFeignClient;

    @Autowired
    WareFeignClient wareFeignClient;

    @Override
    public OrderConfirmVo getOrderConfirmData() {
        OrderConfirmVo vo = new OrderConfirmVo();
        //获取用户地址列表
        Result<List<UserAddress>> userAddress = userFeignClient.getUserAddress();
        vo.setUserAddressList(userAddress.getData());
        //获取选中商品列表
        Result<List<CartInfo>> infoResult = cartFeignClient.getCheckedCartItems();
        List<CartInfo> infos = infoResult.getData();
        List<CartOrderDetailVo> detailVos = infos.stream().map(info -> {
            CartOrderDetailVo detailVo = new CartOrderDetailVo();
            detailVo.setImgUrl(info.getImgUrl());
            detailVo.setSkuName(info.getSkuName());
            //远程调用
            Result<BigDecimal> price = skuFeignClient.get1010SkuPrice(info.getSkuId());
            detailVo.setOrderPrice(price.getData());
            detailVo.setSkuNum(info.getSkuNum());
            String stock = wareFeignClient.hasStock(info.getSkuId(), info.getSkuNum());
            detailVo.setStock(stock);
            return detailVo;
        }).collect(Collectors.toList());
        vo.setDetailArrayList(detailVos);

        //获取总数量
        Integer num = infos.stream()
                .map(info -> info.getSkuNum())
                .reduce((o1, o2) -> o1 + o2)
                .get();
        vo.setTotalNum(num);

        //获取总金额
        BigDecimal amount = detailVos.stream().map(detailVo -> detailVo.getOrderPrice().multiply(new BigDecimal(detailVo.getSkuNum())))
                .reduce((o1, o2) -> o1.add(o2)).get();
        vo.setTotalAmount(amount);

        //获取标识码
        String token = UUID.randomUUID().toString().replace("-", "");
        vo.setTradeNo(token);

        return vo;
    }
}

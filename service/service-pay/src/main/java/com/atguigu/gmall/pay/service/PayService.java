package com.atguigu.gmall.pay.service;

import com.alipay.api.AlipayApiException;

public interface PayService {
    String generatePayPage(Long orderId) throws AlipayApiException;
}

package com.atguigu.gmall.gateway.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {
    private List<String> loginUrl;//需要验证
    private List<String> noAuthUrl;//不能访问
    private String loginPage;//登录页地址
}

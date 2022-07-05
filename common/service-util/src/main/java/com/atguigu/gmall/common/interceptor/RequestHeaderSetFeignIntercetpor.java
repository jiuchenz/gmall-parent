package com.atguigu.gmall.common.interceptor;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Component
public class RequestHeaderSetFeignIntercetpor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //拿取浏览器发送的请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //把原来所有的请求头放入新的模板中
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if("UserTempId".equalsIgnoreCase(headerName) || "UserId".equalsIgnoreCase(headerName) ){
                requestTemplate.header(headerName,headerValue);
            }
        }
    }
}

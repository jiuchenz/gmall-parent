package com.atguigu.gmall.gateway.filter;


import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.gateway.properties.AuthProperties;
import com.atguigu.gmall.model.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class GlobalAuthFilter implements GlobalFilter {
    @Autowired
    AuthProperties properties;

    @Autowired
    StringRedisTemplate redisTemplate;

    //ant风格的路径匹配器
    AntPathMatcher pathMatchers = new AntPathMatcher();


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        log.info("拦截到请求[{}]", path);
        //1.判断是需要拦截还是校验
        if (pathMatch(properties.getNoAuthUrl(),path)){
            //不能放行，全部拦截
            return wirteJson(response, ResultCodeEnum.NOAUTH_URL);
        }
        //2.查看是否是需要登录的访问
        if (pathMatch(properties.getLoginUrl(),path)){
            //需要登录
            //3.拿取令牌
            String token = getToken(request);
            return checkTokenOrRedirect(exchange, chain, request, response, token);
        }

        //3.不需要登录就可以访问，如果携带令牌,查询令牌是否正确，正确后见userId和临时id全部放进请求头
        String token = getToken(request);
        if (!StringUtils.isEmpty(token)){
            //携带token
            return checkTokenOrRedirect(exchange,chain,request,response,token);
        }
        //4.不携带令牌，将携带的临时id放入请求头放行
        String tempId = getTempId(exchange);
        ServerWebExchange newExchange = exchange.mutate()
                .request(request.mutate().header("UserTempId", tempId).build())
                .response(response).build();

        return chain.filter(newExchange);
    }

    private Mono<Void> checkTokenOrRedirect(ServerWebExchange exchange, GatewayFilterChain chain, ServerHttpRequest request, ServerHttpResponse response, String token) {
        UserInfo userinfo = validToken(token);
        if (userinfo!=null){
            //4.验证token通过
            Long id = userinfo.getId();
            String tempId = getTempId(exchange);
            //5.放行之前，添加userId字段，利用mutate()
            ServerHttpRequest newRequest = request.mutate()
                    .header("UserId", id.toString())
                    .header("UserTempId",tempId)
                    .build();
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).response(response).build();
            return chain.filter(newExchange);
        }else {
            //6.验证不通过
            log.info("用户令牌【{}】非法，打回登录页", token);
            return LocationToPage(response,properties.getLoginPage());
        }
    }

    private String getTempId(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String id = "";
        HttpCookie cookieUserTempId = request.getCookies().getFirst("userTempId");
        if (cookieUserTempId!=null){
            id = cookieUserTempId.getValue();
            if (StringUtils.isEmpty(id)){
                id = request.getHeaders().getFirst("userTempId");
            }
        }
        else {
            //再查询header中是否有token
            id = request.getHeaders().getFirst("userTempId");
        }
        return id;
    }

    private Mono<Void> LocationToPage(ServerHttpResponse response,String loginPage) {
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().set("location",loginPage);
        return response.setComplete();
    }

    private UserInfo validToken(String token) {
        //1.没令牌
        if (StringUtils.isEmpty(token)){
            return null;
        }
        UserInfo info = getUserInfo(token);
        if (info==null){
            return null;
        }
        return info;
    }

    private UserInfo getUserInfo(String token) {
        String json = redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_PREFIX + token);
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        UserInfo info = JSONs.toObj(json, UserInfo.class);
        return info;
    }

    private String getToken(ServerHttpRequest request) {
        String token = "";
        HttpCookie cookieToken = request.getCookies().getFirst("token");
        if (cookieToken!=null){
            token = cookieToken.getValue();
            if (StringUtils.isEmpty(token)){
                token = request.getHeaders().getFirst("token");
            }
        }
        else {
            //再查询header中是否有token
             token = request.getHeaders().getFirst("token");
        }
        return token;
    }


    private Mono<Void> wirteJson(ServerHttpResponse response, ResultCodeEnum codeEnum) {
        Result<String> result = Result.build("", codeEnum);
        DataBuffer wrap = response.bufferFactory().wrap(JSONs.toStr(result).getBytes(StandardCharsets.UTF_8));
        //指定字符集编码
        response.getHeaders().add("content-type", "application/json;charset=utf-8");
        return response.writeWith(Mono.just(wrap));
    }

    private Boolean pathMatch(List<String> patterns, String path) {
        Long count = patterns.stream().filter(pattern->
            pathMatchers.match(pattern,path)).count();
        return count>0;
    }
}

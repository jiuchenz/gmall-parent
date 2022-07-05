package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessRespVo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService{

    @Autowired
    UserInfoMapper userInfoMapperl;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public LoginSuccessRespVo login(UserInfo userInfo, String ipAddress) {
        String loginName = userInfo.getLoginName();
        String password = userInfo.getPasswd();
        UserInfo loginUser = userInfoMapperl.getUserInfoByNameAndPassword(loginName, MD5.encrypt(password));
        if (loginUser==null){
            //登录失败
            throw new GmallException(ResultCodeEnum.LOGIN_FAIL);
        }
        //1.登录成功，生成toker
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        //2.把用户信息按照令牌保存到redislim
        String toStr = JSONs.toStr(loginUser);
        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_PREFIX+token,toStr,7, TimeUnit.DAYS);
        //3。准备登陆后返回的数据
        LoginSuccessRespVo vo = new LoginSuccessRespVo();
        vo.setToken(token);
        vo.setNickName(loginUser.getNickName());
        return vo;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(RedisConst.USER_LOGIN_PREFIX+token);
    }
}





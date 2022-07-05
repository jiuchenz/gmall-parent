package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.model.user.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Entity com.atguigu.gmall.user.domain.UserInfo
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    UserInfo getUserInfoByNameAndPassword(@Param("loginName") String loginName, @Param("password") String password);
}





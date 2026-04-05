package com.liepin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liepin.mapper.UserPassportMapper;
import com.liepin.pojo.UserPassport;
import com.liepin.service.UserPassportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserPassportServiceImpl implements UserPassportService {

    @Autowired
    private UserPassportMapper userPassportMapper;

    @Override
    public UserPassport queryUserInfo(String userId, String pwd) {
        return userPassportMapper.selectOne(new QueryWrapper<UserPassport>()
                .eq("imooc_user_id", userId)
                .eq("password", pwd)
        );
    }

}

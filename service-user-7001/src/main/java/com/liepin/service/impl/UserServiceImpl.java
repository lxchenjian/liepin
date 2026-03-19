package com.liepin.service.impl;

import com.liepin.base.BaseInfoProperties;
import com.liepin.exceptions.GraceException;
import com.liepin.grace.result.ResponseStatusEnum;
import com.liepin.mapper.UsersMapper;
import com.liepin.pojo.Users;
import com.liepin.pojo.bo.ModifyUserBO;
import com.liepin.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends BaseInfoProperties implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Transactional
    @Override
    public void modifyUserInfo(ModifyUserBO userBO) {

        String userId = userBO.getUserId();
        if (StringUtils.isBlank(userId))
            GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_ERROR);

        Users pendingUser = new Users();
        pendingUser.setId(userId);
        pendingUser.setUpdatedTime(LocalDateTime.now());

        BeanUtils.copyProperties(userBO, pendingUser);

        usersMapper.updateById(pendingUser);
    }

    @Override
    public Users getById(String uid) {
        return usersMapper.selectById(uid);
    }
}

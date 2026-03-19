package com.liepin.controller;

import com.google.gson.Gson;
import com.liepin.base.BaseInfoProperties;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.pojo.Users;
import com.liepin.pojo.bo.ModifyUserBO;
import com.liepin.pojo.vo.UsersVO;
import com.liepin.service.UserService;
import com.liepin.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("userinfo")
@Slf4j
public class UserInfoController extends BaseInfoProperties {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping("modify")
    public GraceJSONResult modify(@RequestBody ModifyUserBO userBO) throws Exception {

        // 修改用户信息
        userService.modifyUserInfo(userBO);

        // 返回最新用户信息
        UsersVO usersVO = getUserInfo(userBO.getUserId());

        return GraceJSONResult.ok(usersVO);
    }

    private UsersVO getUserInfo(String userId) {
        // 查询获得用户的最新信息
        Users latestUser = userService.getById(userId);

        // 重新生成并且覆盖原来的token
        String uToken = jwtUtils.createJWTWithPrefix(new Gson().toJson(latestUser),
                                                    TOKEN_USER_PREFIX);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(latestUser, usersVO);
        usersVO.setUserToken(uToken);

        return usersVO;
    }

}

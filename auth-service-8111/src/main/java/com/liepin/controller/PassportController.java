package com.liepin.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.liepin.base.BaseInfoProperties;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @className: PassPortController
 * @Description: TODO
 * @version: v1.0.0
 * @author: GONGWENXUE
 * @date: 2026/3/14 15:36
 */

@RestController
@RequestMapping("passport")
@Slf4j
public class PassportController extends BaseInfoProperties {

    @GetMapping("getSMSCode")
    public GraceJSONResult getSMSCode(String mobile,
                                      HttpServletRequest request) throws Exception {
        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.error();
        }

        // 获得用户ip
        String userIp = IPUtil.getRequestIp(request);
        // 限制用户只能在60s以内获得一次验证码
        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, mobile);

        String code = (int)((Math.random() * 9 + 1) * 100000) + "";
//        smsUtils.sendSMS(mobile, code);   先不发送
        log.info("验证码为：{}", code);

        // 把验证码存入到redis，用于后续的注册登录进行校验
        redis.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);

        return GraceJSONResult.ok();
    }

}

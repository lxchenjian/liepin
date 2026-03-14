package com.liepin.intercept;

import com.liepin.base.BaseInfoProperties;
import com.liepin.exceptions.GraceException;
import com.liepin.grace.result.ResponseStatusEnum;
import com.liepin.utils.IPUtil;
import com.liepin.base.BaseInfoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SMSInterceptor extends BaseInfoProperties implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 获得用户ip
        String userIp = IPUtil.getRequestIp(request);
        // 获得用于判断的boolean
        boolean ipExist = redis.keyIsExist(MOBILE_SMSCODE + ":" + userIp);

        if (ipExist) {
            log.error("短信发送频率太高了~~！！！");
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }

        /**
         * false: 请求被拦截
         * true: 放行，请求验证通过
         */
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}

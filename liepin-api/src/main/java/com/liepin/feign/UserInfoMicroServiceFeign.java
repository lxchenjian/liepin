package com.liepin.feign;

import com.liepin.grace.result.GraceJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserInfoMicroServiceFeign {

    @PostMapping("/userinfo/getCountsByCompanyId")
    public GraceJSONResult getCountsByCompanyId(
            @RequestParam("companyId") String companyId);


    @PostMapping("/userinfo/bindingHRToCompany")
    public GraceJSONResult bindingHRToCompany(
            @RequestParam("hrUserId") String hrUserId,
            @RequestParam("realname") String realname,
            @RequestParam("companyId") String companyId);

    @PostMapping("/userinfo/get")
    public GraceJSONResult get(@RequestParam("userId") String userId);

    @PostMapping("/userinfo/changeUserToHR")
    public GraceJSONResult changeUserToHR(
            @RequestParam("hrUserId") String hrUserId);
}

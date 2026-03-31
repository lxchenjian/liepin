package com.liepin.feign;

import com.liepin.grace.result.GraceJSONResult;
import com.liepin.pojo.bo.SearchBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("company-service")
public interface CompanyMicroServiceFeign {

    @PostMapping("/company/list/get")
    public GraceJSONResult getList(@RequestBody SearchBO searchBO);

}

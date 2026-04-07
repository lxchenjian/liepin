package com.liepin.controller;

import com.liepin.grace.result.GraceJSONResult;
import com.liepin.service.ResumeSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("resumeSearch")
public class ResumeSearchController {

    @Autowired
    private ResumeSearchService resumeSearchService;

    /**
     * 模拟接口
     * 刷新简历后触发
     * 传入用户id，根据用户id获得简历相关信息并且刷入到es中
     * @param userId
     * @return
     */
    @GetMapping("flush")
    public GraceJSONResult flush(String userId) {
        resumeSearchService.transformAndFlush(userId);
        return GraceJSONResult.ok();
    }

}

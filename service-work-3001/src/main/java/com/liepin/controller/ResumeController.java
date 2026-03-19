package com.liepin.controller;

import com.liepin.grace.result.GraceJSONResult;
import com.liepin.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    /**
     * 初始化用户简历
     * @param userId
     * @return
     */
    @PostMapping("init")
    public GraceJSONResult init(@RequestParam("userId") String userId) {
        resumeService.initResume(userId);
        return GraceJSONResult.ok();
    }

}

package com.liepin.controller;

import com.liepin.intercept.JWTCurrentUserInterceptor;
import com.liepin.base.BaseInfoProperties;
import com.liepin.enums.InterviewStatusEnum;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.grace.result.ResponseStatusEnum;
import com.liepin.pojo.bo.CreateInterviewBO;
import com.liepin.service.InterviewService;
import com.liepin.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("interview")
public class InterviewController extends BaseInfoProperties {

    @Autowired
    private InterviewService interviewService;

    @PostMapping("create")
    public GraceJSONResult create(@RequestBody @Valid CreateInterviewBO interviewBO) {

        // TODO CreateInterviewBO 自行校验

        String interviewId = interviewService.create(interviewBO);
        return GraceJSONResult.ok(interviewId);
    }

    @PostMapping("detail")
    public GraceJSONResult detail(String interviewId,
                                  String hrUserId,
                                  String companyId) {
        return GraceJSONResult.ok(
                interviewService.detail(
                        interviewId,
                        hrUserId,
                        companyId));
    }

    @PostMapping("cancel")
    public GraceJSONResult cancel(String interviewId) {
        interviewService.updateInterviewStatus(interviewId, InterviewStatusEnum.CANCEL);
        return GraceJSONResult.ok();
    }

    @PostMapping("accept")
    public GraceJSONResult accept(String interviewId) {
        interviewService.updateInterviewStatus(interviewId, InterviewStatusEnum.ACCEPT);
        return GraceJSONResult.ok();
    }

    @PostMapping("refuse")
    public GraceJSONResult refuse(String interviewId) {
        interviewService.updateInterviewStatus(interviewId, InterviewStatusEnum.REFUSE);
        return GraceJSONResult.ok();
    }

    @PostMapping("listOfHr")
    public GraceJSONResult listOfHr(String hrId,
                                    String companyId,
                                    Integer page,
                                    Integer limit) {

        if (StringUtils.isBlank(hrId) || StringUtils.isBlank(companyId)) {
            return GraceJSONResult.ok(ResponseStatusEnum.USER_PARAMS_ERROR);
        }

        PagedGridResult result = interviewService.queryInterviewList(hrId, companyId, page, limit);
        return GraceJSONResult.ok(result);
    }

    @PostMapping("listOfCand")
    public GraceJSONResult listOfCand(String candUserId,
                                      Integer page,
                                      Integer limit) {

        if (StringUtils.isBlank(candUserId)) {
            return GraceJSONResult.ok(ResponseStatusEnum.USER_PARAMS_ERROR);
        }

        PagedGridResult result = interviewService.queryInterviewList(candUserId, page, limit);
        return GraceJSONResult.ok(result);
    }

    @PostMapping("list")
    public GraceJSONResult list(Integer page, Integer limit) {

        String companyId = JWTCurrentUserInterceptor.currentUser.get().getHrInWhichCompanyId();

        PagedGridResult result = interviewService.queryInterviewListCompany(companyId, page, limit);
        return GraceJSONResult.ok(result);
    }
}

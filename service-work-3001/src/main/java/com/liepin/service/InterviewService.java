package com.liepin.service;

import com.liepin.enums.InterviewStatusEnum;
import com.liepin.pojo.Interview;
import com.liepin.pojo.bo.CreateInterviewBO;
import com.liepin.pojo.mo.*;
import com.liepin.utils.PagedGridResult;

public interface InterviewService {

    /**
     * 创建面试邀约记录
     * @return
     */
    public String create(CreateInterviewBO interviewBO);

    /**
     * 获得面试详情
     * @param interviewId
     * @param hrUserId
     * @param companyId
     * @return
     */
    public Interview detail(String interviewId,
                            String hrUserId,
                            String companyId);

    /**
     * 更新面试状态
     * @param interviewId
     * @param statusEnum
     * @return
     */
    public void updateInterviewStatus(String interviewId,
                                      InterviewStatusEnum statusEnum);

    /**
     * 分页查询面试记录 - HR
     * @param hrId
     * @param companyId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryInterviewList(String hrId,
                                              String companyId,
                                              Integer page,
                                              Integer pageSize);

    /**
     * 分页查询面试记录 - 求职者
     * @param candUserId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryInterviewList(String candUserId,
                                              Integer page,
                                              Integer pageSize);

    /**
     * 分页查询面试记录 - 企业
     * @param companyId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryInterviewListCompany(String companyId,
                                                     Integer page,
                                                     Integer pageSize);
}

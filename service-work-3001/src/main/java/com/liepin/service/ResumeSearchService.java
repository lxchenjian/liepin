package com.liepin.service;

import com.liepin.pojo.bo.SearchResumesBO;
import com.liepin.utils.PagedGridResult;

/**
 * 简历检索service
 */
public interface ResumeSearchService {

    /**
     * 改造重构简历数据并且输入es
     * @param userId
     */
    public void transformAndFlush(String userId);

    /**
     * 搜索简历 (es检索)
     * @param searchResumesBO
     * @return
     */
    public PagedGridResult searchResumesByES(SearchResumesBO searchResumesBO,
                                             Integer page,
                                             Integer pageSize);

}

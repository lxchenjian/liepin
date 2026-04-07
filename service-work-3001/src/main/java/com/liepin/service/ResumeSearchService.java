package com.liepin.service;

import com.liepin.pojo.bo.SearchResumesBO;
import com.liepin.pojo.eo.SearchResumesEO;
import com.liepin.utils.PagedGridResult;

import java.util.List;

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

    /**
     * 根据ids检索es中的简历列表
     * @param ids
     * @return
     */
    public List<SearchResumesEO> searchCollectResumes(List<String> ids);

}

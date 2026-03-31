package com.liepin.service;

import com.liepin.enums.DealStatus;
import com.liepin.pojo.bo.SearchReportJobBO;
import com.liepin.pojo.mo.ReportMO;
import com.liepin.utils.PagedGridResult;

public interface ReportService {

    /**
     * 新增举报记录
     * @param reportMO
     */
    public void saveReportRecord(ReportMO reportMO);

    /**
     * 判断是否已经被同一个用户举报过
     * @param reportUserId
     * @param jobId
     * @return
     */
    public boolean isReportRecordExist(String reportUserId, String jobId);

    /**
     * 条件搜索举报记录列表
     * @param reportJobBO
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult pagedReportRecordList(SearchReportJobBO reportJobBO,
                                                 Integer page,
                                                 Integer pageSize);


    /**
     * 修改举报状态
     * @param reportId
     * @param status
     */
    public void updateReportRecordStatus(String reportId, DealStatus status);
}

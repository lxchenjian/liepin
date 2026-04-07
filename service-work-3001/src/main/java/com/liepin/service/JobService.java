package com.liepin.service;

import com.liepin.enums.JobStatus;
import com.liepin.pojo.Job;
import com.liepin.pojo.bo.EditJobBO;
import com.liepin.pojo.bo.SearchJobsBO;
import com.liepin.pojo.vo.SearchJobsVO;
import com.liepin.utils.PagedGridResult;

import java.util.List;

/**
 * <p>
 * HR发布的职位表 服务类
 * </p>
 *
 * @author 风间影月
 * @since 2022-09-04
 */
public interface JobService {

    /**
     * 编辑职位信息
     * @param editJobBO
     */
    public void modifyJobDetail(EditJobBO editJobBO);

    /**
     * 查询职位列表
     * @param hrId
     * @param companyId
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    public PagedGridResult queryJobList(String hrId,
                                        String companyId,
                                        Integer page,
                                        Integer pageSize,
                                        Integer status);

    /**
     * 查询职位详情
     * @param hrId
     * @param companyId
     * @param jobId
     * @return
     */
    public Job queryJobDetail(String hrId, String companyId, String jobId);

    /**
     * 修改职位状态
     * @param hrId
     * @param companyId
     * @param jobId
     * @param jobStatus
     */
    public void modifyJobStatus(String hrId,
                                String companyId,
                                String jobId,
                                JobStatus jobStatus);

    /**
     * 搜索职位
     * @param searchJobsBO
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchJobs(SearchJobsBO searchJobsBO,
                                      Integer page,
                                      Integer pageSize);

    /**
     * 候选人搜索收藏的职位列表（根据id查询joblist）
     * @param jobIdList
     * @return
     */
    public List<SearchJobsVO> searchCollectJobs(List<String> jobIdList);
}

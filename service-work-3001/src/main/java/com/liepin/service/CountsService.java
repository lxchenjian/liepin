package com.liepin.service;

import com.liepin.pojo.mo.*;

import java.util.List;

public interface CountsService {

    public void addCollect(String hrId, String resumeExpectId);
    public void removeCollect(String hrId, String resumeExpectId);
    public boolean isHrCollectResume(String hrId, String resumeExpectId);
    public List<HrCollectResumeMO> queryPagedCollectResumeList(String hrId,
                                                               Integer page,
                                                               Integer pageSize);

    public void saveReadResumeRecord(String hrId, String resumeExpectId);
    public List<HrReadResumeRecordMO> queryPagedReadResumeList(String hrId,
                                                                  Integer page,
                                                                  Integer pageSize);

    public void saveWhoLookMe(WhoLookMeMO whoLookMeMO);
    public List<WhoLookMeMO> pagedWhoLookMe(String candUserId,
                                            Integer page,
                                            Integer pageSize);

    public void saveFollowHr(CandFollowHrMO followHrMO);
    public void deleteFollowHr(String candUserId, String hrId);
    public boolean doesCandFollowHr(String candUserId, String hrId);
    public List<CandFollowHrMO> pagedCandFollowHr(String candUserId,
                                                   Integer page,
                                                   Integer pageSize);

    public void addCollectJob(String candUserId, String jobId);
    public void removeCollectJob(String candUserId, String jobId);
    public boolean isCandCollectJob(String candUserId, String jobId);
    public List<CandCollectJobMO> pagedCollectJobList(String candUserId,
                                                      Integer page,
                                                      Integer pageSize);

}

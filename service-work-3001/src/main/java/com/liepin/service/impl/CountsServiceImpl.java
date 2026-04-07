package com.liepin.service.impl;

import com.liepin.base.BaseInfoProperties;
import com.liepin.pojo.mo.*;
import com.liepin.repository.*;
import com.liepin.service.CountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CountsServiceImpl extends BaseInfoProperties implements CountsService {

    @Autowired
    private HrCollectResumeRepository collectResumeRepository;

    @Autowired
    private HrReadResumeRepository readResumeRepository;

    @Autowired
    private WhoLookMeRepository whoLookMeRepository;

    @Autowired
    private CandFollowHrRepository candFollowHrRepository;

    @Autowired
    private CandCollectJobRepository collectJobRepository;

    @Override
    public void addCollect(String hrId, String resumeExpectId) {

        HrCollectResumeMO resumeMO = new HrCollectResumeMO();
        resumeMO.setHrId(hrId);
        resumeMO.setResumeExpectId(resumeExpectId);
        resumeMO.setCreateTime(LocalDateTime.now());

        collectResumeRepository.save(resumeMO);
        redis.increment(HR_COLLECT_RESUME_COUNTS + ":" + hrId, 1);
    }

    @Override
    public void removeCollect(String hrId, String resumeExpectId) {
        collectResumeRepository.deleteByHrIdAndResumeExpectId(hrId, resumeExpectId);
        redis.decrement(HR_COLLECT_RESUME_COUNTS + ":" + hrId, 1);
    }

    @Override
    public boolean isHrCollectResume(String hrId, String resumeExpectId) {

        List<HrCollectResumeMO> list = collectResumeRepository
                                .findByHrIdAndResumeExpectId(hrId, resumeExpectId);

        //CollectionUtils.isEmpty(list)
        if (list != null && !list.isEmpty() && list.size() > 0) {
            return true;
        }

        return false;
    }

    @Override
    public List<HrCollectResumeMO> queryPagedCollectResumeList(String hrId,
                                                               Integer page,
                                                               Integer pageSize) {
        Pageable pageable = PageRequest.of(page,
                                            pageSize,
                                            Sort.Direction.DESC,
                                            "createTime");

        List<HrCollectResumeMO> list = collectResumeRepository
                .findAllByHrIdOrderByCreateTimeDesc(hrId, pageable);

        return list;
    }

    @Override
    public void saveReadResumeRecord(String hrId, String resumeExpectId) {

        // 查询是否存在记录
        HrReadResumeRecordMO alreadyRecord = readResumeRepository
                .findByHrIdAndResumeExpectId(hrId, resumeExpectId);

        // 初始化并且保存到mongo
        HrReadResumeRecordMO recordMO = new HrReadResumeRecordMO();

        if (alreadyRecord != null) {
            recordMO.setId(alreadyRecord.getId());
        }

        recordMO.setHrId(hrId);
        recordMO.setResumeExpectId(resumeExpectId);
        recordMO.setCreateTime(LocalDateTime.now());

        readResumeRepository.save(recordMO);

        if (alreadyRecord == null) {
            redis.increment(HR_READ_RESUME_RECORD_COUNTS + ":" + hrId, 1);
        }
    }

    @Override
    public List<HrReadResumeRecordMO> queryPagedReadResumeList(String hrId,
                                                               Integer page,
                                                               Integer pageSize) {
        Pageable pageable = PageRequest.of(page,
                pageSize,
                Sort.Direction.DESC,
                "createTime");

        List<HrReadResumeRecordMO> list = readResumeRepository
                        .findAllByHrIdOrderByCreateTime(hrId, pageable);

        return list;
    }

    @Override
    public void saveWhoLookMe(WhoLookMeMO whoLookMeMO) {

        WhoLookMeMO old = whoLookMeRepository.findByHrIdAndHrCompanyIdAndCandUserId(
                                                    whoLookMeMO.getHrId(),
                                                    whoLookMeMO.getHrCompanyId(),
                                                    whoLookMeMO.getCandUserId());
        if (old != null) {
            whoLookMeMO.setId(old.getId());
        }

        whoLookMeMO.setCreateTime(LocalDateTime.now());
        whoLookMeRepository.save(whoLookMeMO);

        if (old == null) {
            redis.increment(WHO_LOOK_ME_COUNTS + ":" + whoLookMeMO.getCandUserId(), 1);
        }
    }

    @Override
    public List<WhoLookMeMO> pagedWhoLookMe(String candUserId,
                                            Integer page,
                                            Integer pageSize) {

        Pageable pageable = PageRequest.of(page,
                pageSize,
                Sort.Direction.DESC,
                "createTime");

        List<WhoLookMeMO> list = whoLookMeRepository
                .findAllByCandUserIdOrderByCreateTimeDesc(candUserId, pageable);

        return list;
    }

    @Override
    public void saveFollowHr(CandFollowHrMO followHrMO) {
        followHrMO.setCreateTime(LocalDateTime.now());
        candFollowHrRepository.save(followHrMO);
        redis.increment(CAND_FOLLOW_HR_COUNTS + ":" + followHrMO.getCandUserId(), 1);
    }

    @Override
    public void deleteFollowHr(String candUserId, String hrId) {
        candFollowHrRepository.deleteByCandUserIdAndHrId(candUserId, hrId);
        redis.decrement(CAND_FOLLOW_HR_COUNTS + ":" + candUserId, 1);
    }

    @Override
    public boolean doesCandFollowHr(String candUserId, String hrId) {

        CandFollowHrMO followHrMO = candFollowHrRepository
                .findByCandUserIdAndHrId(candUserId, hrId);

        return followHrMO == null ? false : true;
    }

    @Override
    public List<CandFollowHrMO> pagedCandFollowHr(String candUserId,
                                                  Integer page,
                                                  Integer pageSize) {

        Pageable pageable = PageRequest.of(page,
                pageSize,
                Sort.Direction.DESC,
                "createTime");

        List<CandFollowHrMO> list = candFollowHrRepository
                .findAllByCandUserIdOrderByCreateTimeDesc(candUserId, pageable);

        return list;
    }

    @Override
    public void addCollectJob(String candUserId, String jobId) {

        CandCollectJobMO jobMO = new CandCollectJobMO();
        jobMO.setJobId(jobId);
        jobMO.setCandUserId(candUserId);
        jobMO.setCreateTime(LocalDateTime.now());

        collectJobRepository.save(jobMO);

        redis.increment(CAND_COLLECT_JOB_COUNTS + ":" + candUserId, 1);
    }

    @Override
    public void removeCollectJob(String candUserId, String jobId) {
        collectJobRepository.deleteByCandUserIdAndJobId(candUserId, jobId);
        redis.decrement(CAND_COLLECT_JOB_COUNTS + ":" + candUserId, 1);
    }

    @Override
    public boolean isCandCollectJob(String candUserId, String jobId) {
        CandCollectJobMO collectJobMO = collectJobRepository
                .findByCandUserIdAndAndJobId(candUserId, jobId);

        return collectJobMO == null ? false : true;
    }

    @Override
    public List<CandCollectJobMO> pagedCollectJobList(String candUserId,
                                                      Integer page,
                                                      Integer pageSize) {
        Pageable pageable = PageRequest.of(page,
                pageSize,
                Sort.Direction.DESC,
                "createTime");

        List<CandCollectJobMO> list = collectJobRepository
                .findAllByCandUserIdOrderByCreateTimeDesc(candUserId, pageable);

        return list;
    }
}

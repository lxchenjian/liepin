package com.liepin.repository;

import com.liepin.pojo.mo.HrCollectResumeMO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HrCollectResumeRepository extends MongoRepository<HrCollectResumeMO, String> {

    public void deleteByHrIdAndResumeExpectId(String hrId, String resumeExpectId);

    public List<HrCollectResumeMO> findByHrIdAndResumeExpectId(String hrId, String resumeExpectId);

    public List<HrCollectResumeMO> findAllByHrIdOrderByCreateTimeDesc(String hrId, Pageable pageable);

}

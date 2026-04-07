package com.liepin.repository;

import com.liepin.pojo.mo.HrReadResumeRecordMO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HrReadResumeRepository extends MongoRepository<HrReadResumeRecordMO, String> {

    public HrReadResumeRecordMO findByHrIdAndResumeExpectId(String hrId,
                                                            String resumeExpectId);

    public List<HrReadResumeRecordMO> findAllByHrIdOrderByCreateTime(String hrId,
                                                                     Pageable pageable);

}

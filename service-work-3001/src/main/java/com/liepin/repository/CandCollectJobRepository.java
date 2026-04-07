package com.liepin.repository;

import com.liepin.pojo.mo.CandCollectJobMO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandCollectJobRepository extends MongoRepository<CandCollectJobMO, String> {

    public void deleteByCandUserIdAndJobId(String candUserId, String jobId);

    public CandCollectJobMO findByCandUserIdAndAndJobId(String candUserId, String jobId);

    public List<CandCollectJobMO> findAllByCandUserIdOrderByCreateTimeDesc(String candUserId, Pageable pageable
    );

}

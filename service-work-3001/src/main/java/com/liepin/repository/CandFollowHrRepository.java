package com.liepin.repository;

import com.liepin.pojo.mo.CandFollowHrMO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandFollowHrRepository extends MongoRepository<CandFollowHrMO, String> {


    public void deleteByCandUserIdAndHrId(String candUserId, String hrId);

    public CandFollowHrMO findByCandUserIdAndHrId(String candUserId, String hrId);

    public List<CandFollowHrMO> findAllByCandUserIdOrderByCreateTimeDesc(String userId,
                                                                         Pageable pageable);

}

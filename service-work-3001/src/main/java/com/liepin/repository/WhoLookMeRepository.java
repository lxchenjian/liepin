package com.liepin.repository;

import com.liepin.pojo.mo.WhoLookMeMO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WhoLookMeRepository extends MongoRepository<WhoLookMeMO, String> {


    public WhoLookMeMO findByHrIdAndHrCompanyIdAndCandUserId(String hrId,
                                                             String companyId,
                                                             String userId);

    public List<WhoLookMeMO> findAllByCandUserIdOrderByCreateTimeDesc(String userId,
                                                                      Pageable pageable);

}

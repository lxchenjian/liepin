package com.liepin.service.impl;

import com.liepin.mapper.StuMapper;
import com.liepin.pojo.test.Stu;
import com.liepin.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 风间影月
 * @since 2022-09-04
 */
@Service
public class StuServiceImpl implements StuService {

    @Autowired
    private StuMapper stuMapper;

    @Transactional
    @Override
    public void save(Stu stu) {
        stuMapper.insert(stu);
    }

}

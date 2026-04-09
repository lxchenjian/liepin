package com.liepin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liepin.pojo.Interview;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 面试邀约表
本表为次表，可做冗余，可以用mongo或者es替代 Mapper 接口
 * </p>
 *
 * @author 风间影月
 * @since 2022-09-04
 */
@Repository
public interface InterviewMapper extends BaseMapper<Interview> {

}

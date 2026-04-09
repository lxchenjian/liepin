package com.liepin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.liepin.base.BaseInfoProperties;
import com.liepin.enums.InterviewStatusEnum;
import com.liepin.mapper.InterviewMapper;
import com.liepin.pojo.Interview;
import com.liepin.pojo.bo.CreateInterviewBO;
import com.liepin.pojo.mo.*;
import com.liepin.repository.*;
import com.liepin.service.InterviewService;
import com.liepin.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InterviewServiceImpl extends BaseInfoProperties implements InterviewService {

    @Autowired
    private InterviewMapper interviewMapper;

    @Transactional
    @Override
    public String create(CreateInterviewBO interviewBO) {

        Interview interview = new Interview();
        BeanUtils.copyProperties(interviewBO, interview);
        interview.setStatus(InterviewStatusEnum.WAITING.type);

        interviewMapper.insert(interview);

        redis.increment(HR_INTERVIEW_RECORD_COUNTS + ":" + interviewBO.getHrUserId(), 1);
        redis.increment(CAND_INTERVIEW_RECORD_COUNTS + ":" + interviewBO.getCandUserId(), 1);

        return interview.getId();
    }

    @Override
    public Interview detail(String interviewId,
                         String hrUserId,
                         String companyId) {

        return interviewMapper.selectOne(new QueryWrapper<Interview>()
                .eq("id", interviewId)
                .eq("hr_user_id", hrUserId)
                .eq("company_id", companyId)
        );
    }

    @Transactional
    @Override
    public void updateInterviewStatus(String interviewId,
                                           InterviewStatusEnum statusEnum) {
        Interview interview = new Interview();
        interview.setId(interviewId);
        interview.setStatus(statusEnum.type);

        interviewMapper.updateById(interview);

        // 面试记录一旦被处理，则进行累减1
        Interview record = interviewMapper.selectById(interviewId);
        redis.decrement(HR_INTERVIEW_RECORD_COUNTS + ":" + record.getHrUserId(),
                1);
        redis.decrement(CAND_INTERVIEW_RECORD_COUNTS + ":" + record.getCandUserId(),
                1);
    }

    @Override
    public PagedGridResult queryInterviewList(String hrId,
                                              String companyId,
                                              Integer page,
                                              Integer pageSize) {
        List<Interview> list = queryList(hrId, companyId, null, page, pageSize);
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryInterviewList(String candUserId,
                                              Integer page,
                                              Integer pageSize) {
        List<Interview> list = queryList(null, null, candUserId, page, pageSize);
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryInterviewListCompany(String companyId,
                                                     Integer page,
                                                     Integer pageSize) {
        List<Interview> list = queryList(null, companyId, null, page, pageSize);
        return setterPagedGrid(list, page);
    }

    private List<Interview> queryList(String hrId,
                                      String companyId,
                                      String candUserId,
                                      Integer page,
                                      Integer pageSize) {

        PageHelper.startPage(page, pageSize);

        QueryWrapper queryWrapper = new QueryWrapper<Interview>();
        if (StringUtils.isNotBlank(hrId)) {
            queryWrapper.eq("hr_user_id", hrId);
        }
        if (StringUtils.isNotBlank(companyId)) {
            queryWrapper.eq("company_id", companyId);
        }
        if (StringUtils.isNotBlank(candUserId)) {
            queryWrapper.eq("cand_user_id", candUserId);
        }
        queryWrapper.orderByDesc("interview_time");

        List<Interview> list = interviewMapper.selectList(queryWrapper);
        return list;
    }
}

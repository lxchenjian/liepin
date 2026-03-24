package com.liepin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liepin.enums.CompanyReviewStatus;
import com.liepin.enums.YesOrNo;
import com.liepin.mapper.CompanyMapper;
import com.liepin.pojo.Company;
import com.liepin.pojo.bo.CreateCompanyBO;
import com.liepin.pojo.bo.ReviewCompanyBO;
import com.liepin.service.CompanyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 企业表 服务实现类
 * </p>
 *
 * @author 风间影月
 * @since 2022-09-04
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyMapper companyMapper;

    @Override
    public Company getByFullName(String fullName) {

        Company tempCompany = companyMapper.selectOne(
                new QueryWrapper<Company>()
                    .eq("company_name", fullName)
        );

        return tempCompany;
    }

    @Transactional
    @Override
    public String createNewCompany(CreateCompanyBO createCompanyBO) {

        Company newCompany = new Company();

        BeanUtils.copyProperties(createCompanyBO, newCompany);

        newCompany.setIsVip(YesOrNo.NO.type);
        newCompany.setReviewStatus(CompanyReviewStatus.NOTHING.type);
        newCompany.setCreatedTime(LocalDateTime.now());
        newCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.insert(newCompany);

        return newCompany.getId();
    }

    @Transactional
    @Override
    public String resetNewCompany(CreateCompanyBO createCompanyBO) {

        Company newCompany = new Company();

        BeanUtils.copyProperties(createCompanyBO, newCompany);

        newCompany.setId(createCompanyBO.getCompanyId());
        newCompany.setReviewStatus(CompanyReviewStatus.NOTHING.type);
        newCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(newCompany);

        return createCompanyBO.getCompanyId();
    }

    @Override
    public Company getById(String id) {
        return companyMapper.selectById(id);
    }

    @Transactional
    @Override
    public void commitReviewCompanyInfo(ReviewCompanyBO reviewCompanyBO) {

        Company pendingCompany = new Company();
        pendingCompany.setId(reviewCompanyBO.getCompanyId());

        pendingCompany.setReviewStatus(CompanyReviewStatus.REVIEW_ING.type);
        pendingCompany.setReviewReplay(""); // 如果有内容，则重置覆盖之前的审核意见
        pendingCompany.setAuthLetter(reviewCompanyBO.getAuthLetter());

        pendingCompany.setCommitUserId(reviewCompanyBO.getHrUserId());
        pendingCompany.setCommitUserMobile(reviewCompanyBO.getHrMobile());
        pendingCompany.setCommitDate(LocalDate.now());

        pendingCompany.setUpdatedTime(LocalDateTime.now());

        companyMapper.updateById(pendingCompany);
    }
}

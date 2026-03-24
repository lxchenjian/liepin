package com.liepin.service;

import com.liepin.pojo.Company;
import com.liepin.pojo.bo.CreateCompanyBO;
import com.liepin.pojo.bo.ReviewCompanyBO;

/**
 * <p>
 * 企业表 服务类
 * </p>
 *
 * @author 风间影月
 * @since 2022-09-04
 */
public interface CompanyService {

    /**
     * 根据企业全称查询企业信息
     * @param fullName
     * @return
     */
    public Company getByFullName(String fullName);

    /**
     * 创建企业（状态：未发起审核）
     * @param createCompanyBO
     * @return
     */
    public String createNewCompany(CreateCompanyBO createCompanyBO);

    /**
     * 重启发起审核，修改企业（状态：未发起审核）
     * @param createCompanyBO
     * @return
     */
    public String resetNewCompany(CreateCompanyBO createCompanyBO);

    /**
     * 根据企业id获得企业信息
     * @param id
     * @return
     */
    public Company getById(String id);

    /**
     * 审核提交的企业信息
     * @param reviewCompanyBO
     */
    public void commitReviewCompanyInfo(ReviewCompanyBO reviewCompanyBO);

}

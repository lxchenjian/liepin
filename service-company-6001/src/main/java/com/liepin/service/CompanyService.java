package com.liepin.service;

import com.liepin.pojo.Company;
import com.liepin.pojo.CompanyPhoto;
import com.liepin.pojo.bo.CreateCompanyBO;
import com.liepin.pojo.bo.ModifyCompanyInfoBO;
import com.liepin.pojo.bo.QueryCompanyBO;
import com.liepin.pojo.bo.ReviewCompanyBO;
import com.liepin.pojo.vo.CompanyInfoVO;
import com.liepin.utils.PagedGridResult;

import java.util.List;

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

    /**
     * admin在运营平台查询企业列表
     * @param companyBO
     * @param page
     * @param limit
     * @return
     */
    public PagedGridResult queryCompanyListPaged(QueryCompanyBO companyBO,
                                                 Integer page,
                                                 Integer limit);

    /**
     * 根据企业id查询数据库获得最新企业信息
     * @param companyId
     * @return
     */
    public CompanyInfoVO getCompanyInfo(String companyId);

    /**
     * 更新审核后的信息
     * @param reviewCompanyBO
     */
    public void updateReviewInfo(ReviewCompanyBO reviewCompanyBO);

    /**
     * 修改企业信息
     * @param companyInfoBO
     */
    public void modifyCompanyInfo(ModifyCompanyInfoBO companyInfoBO, Integer num) throws Exception;

    /**
     * 修改企业相册
     * @param companyInfoBO
     */
    public void savePhotos(ModifyCompanyInfoBO companyInfoBO);

    /**
     * 根据企业id获得相册内容
     * @param companyId
     * @return
     */
    public CompanyPhoto getPhotos(String companyId);


    public void testReadLock();
    public void testWriteLock();

    public void testSemaphoreLock(Integer num) throws Exception;
    public void testSemaphoreRelease(Integer num) throws Exception;

    public void testCountDownLatch() throws Exception;
    public void testDoneStep() throws Exception;


    /**
     * 根据企业id查询列表
     * @param companyIds
     * @return
     */
    public List<Company> getByIds(List<String> companyIds);
}

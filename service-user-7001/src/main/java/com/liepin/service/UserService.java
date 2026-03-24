package com.liepin.service;

import com.liepin.pojo.Users;
import com.liepin.pojo.bo.ModifyUserBO;

public interface UserService {

    /**
     * 修改用户信息
     * @param userBO
     */
    public void modifyUserInfo(ModifyUserBO userBO);

    /**
     * 获得用户信息
     * @param uid
     * @return
     */
    public Users getById(String uid);

    /**
     * 查询企业下HR数量
     * @param companyId
     * @return
     */
    public Long getCountsByCompanyId(String companyId);

    /**
     * 更新用户的企业id（绑定公司与hr的关系）
     * @param hrUserId
     * @param realname
     * @param companyId
     */
    public void updateUserCompanyId(String hrUserId,
                                    String realname,
                                    String companyId);

}

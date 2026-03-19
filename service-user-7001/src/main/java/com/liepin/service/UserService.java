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

}

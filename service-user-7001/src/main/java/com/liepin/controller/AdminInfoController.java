package com.liepin.controller;

import com.liepin.base.BaseInfoProperties;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.intercept.JWTCurrentUserInterceptor;
import com.liepin.pojo.Admin;
import com.liepin.pojo.bo.CreateAdminBO;
import com.liepin.pojo.bo.ResetPwdBO;
import com.liepin.pojo.bo.UpdateAdminBO;
import com.liepin.pojo.vo.AdminInfoVO;
import com.liepin.service.AdminService;
import com.liepin.utils.PagedGridResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("admininfo")
@Slf4j
public class AdminInfoController extends BaseInfoProperties {

    @Autowired
    private AdminService adminService;

    @PostMapping("create")
    public GraceJSONResult create(@Valid @RequestBody CreateAdminBO createAdminBO) {
        adminService.createAdmin(createAdminBO);
        return GraceJSONResult.ok();
    }

    @PostMapping("list")
    public GraceJSONResult list(String accountName,
                                Integer page,
                                Integer limit) {

        if (page == null) page = 1;
        if (limit == null) page = 10;

        PagedGridResult listResult = adminService.getAdminList(accountName,
                                    page,
                                    limit);

        return GraceJSONResult.ok(listResult);
    }

    @PostMapping("delete")
    public GraceJSONResult delete(String username) {
        adminService.deleteAdmin(username);
        return GraceJSONResult.ok();
    }

    @PostMapping("resetPwd")
    public GraceJSONResult resetPwd(@RequestBody ResetPwdBO resetPwdBO) {

        // resetPwdBO 校验
        // adminService 重置密码

        resetPwdBO.modifyPwd();
        return GraceJSONResult.ok();
    }

    @PostMapping("myInfo")
    public GraceJSONResult myInfo() {

        Admin admin = JWTCurrentUserInterceptor.adminUser.get();

        Admin adminInfo = adminService.getById(admin.getId());

        AdminInfoVO adminInfoVO = new AdminInfoVO();
        BeanUtils.copyProperties(adminInfo, adminInfoVO);

        return GraceJSONResult.ok(adminInfoVO);
    }

    @PostMapping("updateMyInfo")
    public GraceJSONResult updateMyInfo(@RequestBody @Valid UpdateAdminBO adminBO) {
        Admin admin = JWTCurrentUserInterceptor.adminUser.get();

        adminBO.setId(admin.getId());
        adminService.updateAdmin(adminBO);

        return GraceJSONResult.ok();
    }



}

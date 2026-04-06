package com.liepin.feign.fallback;

import com.liepin.feign.UserInfoMicroServiceFeign;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.pojo.Users;
import com.liepin.pojo.bo.SearchBO;
import org.springframework.stereotype.Component;

@Component
public class UserInfoMicroServiceFeignFallback implements UserInfoMicroServiceFeign {

    @Override
    public GraceJSONResult getList(SearchBO searchBO) {
        return null;
    }

    @Override
    public GraceJSONResult getCountsByCompanyId(String companyId) {
        return null;
    }

    @Override
    public GraceJSONResult bindingHRToCompany(String hrUserId, String realname, String companyId) {
        return null;
    }

    @Override
    public GraceJSONResult get(String userId) {

        // 返回用户的空对象，或者返回error错误信息，或者在这里直接调用数据库（不建议）
        Users users = new Users();
        users.setId(userId);
        return GraceJSONResult.ok(users);
    }

    @Override
    public GraceJSONResult changeUserToHR(String hrUserId) {
        return null;
    }
}

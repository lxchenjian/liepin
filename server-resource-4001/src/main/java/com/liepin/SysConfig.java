package com.liepin;

import com.liepin.base.BaseInfoProperties;
import com.liepin.exceptions.GraceException;
import com.liepin.grace.result.ResponseStatusEnum;
import com.liepin.pojo.SysParams;
import com.liepin.service.SysParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * 通过监听接口的方式，启动服务，服务初始化之后，执行方法
 * 缺陷：如果数据量过大，则不适合在这里预热
 * 更好落地方案：接口预热
 */
@Configuration
public class SysConfig extends BaseInfoProperties implements CommandLineRunner {

    @Autowired
    private SysParamsService sysParamsService;

    @Override
    public void run(String... args) throws Exception {
        SysParams sysParams =  sysParamsService.getSysParams();
        if (sysParams == null) GraceException.display(ResponseStatusEnum.SYS_DATA_ERROR);
        this.dealMaxResumeRefreshCounts(sysParams.getMaxResumeRefreshCounts());
    }

    private void dealMaxResumeRefreshCounts(int counts) {
        // 1. 把数据预热存储到redis中
        redis.set(REDIS_MAX_RESUME_REFRESH_COUNTS, counts+"");
    }
}

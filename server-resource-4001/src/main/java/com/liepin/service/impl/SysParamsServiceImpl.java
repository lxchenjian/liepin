package com.liepin.service.impl;

import com.liepin.base.BaseInfoProperties;
import com.liepin.mapper.SysParamsMapper;
import com.liepin.pojo.SysParams;
import com.liepin.service.SysParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 系统参数配置表，本表仅有一条记录 服务实现类
 * </p>
 *
 * @author 风间影月
 * @since 2022-09-04
 */
@Service
public class SysParamsServiceImpl extends BaseInfoProperties implements SysParamsService {

    @Autowired
    private SysParamsMapper sysParamsMapper;

    @Transactional
    @Override
    public void updateMaxResumeRefreshCounts(Integer maxCounts, Integer version) {

        SysParams params = new SysParams();
        params.setId(SYS_PARAMS_PK);
        params.setMaxResumeRefreshCounts(maxCounts);

        sysParamsMapper.updateById(params);

        // 这里进行redis更新，但是重启项目检查不到。 解决：缓存预热
        //redis.set(REDIS_MAX_RESUME_REFRESH_COUNTS,maxCounts+"");
    }

    @Override
    public SysParams getSysParams() {
        return sysParamsMapper.selectById(SYS_PARAMS_PK);
    }
}

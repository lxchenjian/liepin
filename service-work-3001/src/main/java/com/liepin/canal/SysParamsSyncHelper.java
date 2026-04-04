package com.liepin.canal;

import com.github.benmanes.caffeine.cache.Cache;
import com.liepin.base.BaseInfoProperties;
import com.liepin.pojo.co.SysParamsCO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@Slf4j
//@CanalTable("sys_params")      // 指定监听的表名
//@Component
public class SysParamsSyncHelper extends BaseInfoProperties
        implements EntryHandler<SysParamsCO> // 指定表关联的实体对象（javabean）
{

    @Autowired
    private Cache<String, Integer> resumeRefreshCountsCache;

    @Override
    public void insert(SysParamsCO co) {
    }

    @Override
    public void update(SysParamsCO before, SysParamsCO after) {
        //System.out.println(before);
        //System.out.println(after);

        Integer maxCounts = after.getMax_resume_refresh_counts();
        // 各个微服务节点监听到变动，则更新各自的本地缓存
        resumeRefreshCountsCache.put(CACHE_MAX_RESUME_REFRESH_COUNTS, maxCounts);

        log.info("简历微服务节点本地缓存已更新...更新后的[最大刷新阈值]为：{}", maxCounts);

        // 更新到缓存redis中  无缓存击穿，是先删除后增加有缓存击穿风险
        redis.set(REDIS_MAX_RESUME_REFRESH_COUNTS, maxCounts + "");
    }

    @Override
    public void delete(SysParamsCO co) {
    }

}

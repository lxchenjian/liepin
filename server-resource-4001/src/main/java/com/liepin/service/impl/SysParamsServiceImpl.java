package com.liepin.service.impl;

import com.liepin.base.BaseInfoProperties;
import com.liepin.mapper.SysParamsMapper;
import com.liepin.mq.DelayConfig_Industry;
import com.liepin.mq.DelayConfig_MaxCounts;
import com.liepin.pojo.SysParams;
import com.liepin.service.SysParamsService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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

    @Resource(name = "curatorClient")
    private CuratorFramework zkClient;
    @Transactional
    @Override
    public int updateMaxResumeRefreshCounts(Integer maxCounts, Integer version) throws Exception {

        // 问题：数据库在失败以后会回滚，但是 缓存不会
        // 1. 把数据写入到数据库
        SysParams params = new SysParams();
        params.setId(SYS_PARAMS_PK);
        params.setMaxResumeRefreshCounts(maxCounts);

        sysParamsMapper.updateById(params);

        // 2. 把数据携带版本号保存到zk节点  解耦，可以把这段代码转移到canal
        String path = "/" + ZK_MAX_RESUME_REFRESH_COUNTS;

        Stat stat = zkClient.setData()
                .withVersion(version)
                .forPath(path,
                        maxCounts.toString().getBytes());

        // 3. 更新到缓存redis中
        redis.set(REDIS_MAX_RESUME_REFRESH_COUNTS, maxCounts + "");


        // 发送延迟队列来校验数据的一致性，db、redis、zk 这三者的数据需要达到一致性
        //int delayTimes = 20 * 1000;
        /*int delayTimes =  DelayTimes.getDelayTimes(1);

        MessagePostProcessor processor = DelayConfig_Industry.setDelayTimes(delayTimes);
        rabbitTemplate.convertAndSend(
                DelayConfig_MaxCounts.EXCHANGE_DELAY_MAX_COUNTS,
                DelayConfig_MaxCounts.DELAY_MAX_COUNTS_REFRESH,
                "123456",
                processor);*/

        return stat.getVersion();
    }

    @Override
    public SysParams getSysParams() {
        return sysParamsMapper.selectById(SYS_PARAMS_PK);
    }
}

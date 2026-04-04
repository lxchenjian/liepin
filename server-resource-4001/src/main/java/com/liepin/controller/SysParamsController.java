package com.liepin.controller;

import com.liepin.base.BaseInfoProperties;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.grace.result.ResponseStatusEnum;
import com.liepin.pojo.SysParams;
import com.liepin.pojo.vo.SysParamsVO;
import com.liepin.service.SysParamsService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.recipes.locks.Lease;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("sys")
public class SysParamsController extends BaseInfoProperties {

    @Autowired
    private SysParamsService sysParamsService;

    //@Autowired
    //private ZKConnecter zkConnecter;

    @Resource(name = "curatorClient")
    private CuratorFramework zkClient;

    /**
     * 更新简历的最大刷新次数
     * @param maxCounts
     * @param version
     * @return
     */
    @PostMapping("modifyMaxResumeRefreshCounts")
    public GraceJSONResult modifyMaxResumeRefreshCounts(Integer maxCounts, Integer version) throws Exception {

        if (maxCounts == null || maxCounts < 1)
            return GraceJSONResult.errorCustom(
                    ResponseStatusEnum.SYSTEM_PARAMS_SETTINGS_ERROR);
        // zookeeper：原生
        //ZKLock zkLock = zkConnecter.getLock("imooc-lock");
        //zkLock.get();

        // 可重入分布式锁
        //InterProcessMutex processMutex = new InterProcessMutex(zkClient, "/mutex-locks");
        // 不可重入分布式锁
        //InterProcessSemaphoreMutex processMutex = new InterProcessSemaphoreMutex(zkClient, "/mutex-locks");
        //processMutex.acquire(); // 获得锁
        //processMutex.acquire(3, TimeUnit.SECONDS);

        // 读写锁
        InterProcessReadWriteLock readWriteLock = new InterProcessReadWriteLock(zkClient, "/rw-locks");
        readWriteLock.writeLock().acquire();

        // 测试zookeeper分布式锁：原生
        //try {
        //    Thread.sleep(2500);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
        try {
            sysParamsService.updateMaxResumeRefreshCounts(maxCounts, version);
        } finally {
            readWriteLock.writeLock().release();
            //processMutex.release();
        }

        //zkLock.release();

        // TODO version zk的乐观锁机制（后面再讲）
        return GraceJSONResult.ok(0);
    }

    @PostMapping("modifyMaxResumeRefreshCounts2")
    public GraceJSONResult modifyMaxResumeRefreshCounts2(Integer maxCounts,
                                                        Integer version) throws Exception {
        if (maxCounts == null || maxCounts < 1)
            return GraceJSONResult.errorCustom(
                    ResponseStatusEnum.SYSTEM_PARAMS_SETTINGS_ERROR);

        Integer newVersion = sysParamsService.updateMaxResumeRefreshCounts(maxCounts, version);

        // version zk的乐观锁机制
        return GraceJSONResult.ok(newVersion);
    }

    // 可重入
    private void lockAgain(InterProcessMutex processMutex) throws Exception{
        processMutex.acquire();
        System.out.println("do something...");
        processMutex.release();
    }

    @GetMapping("counts")
    public GraceJSONResult counts() throws Exception {
        //分布式计数器
        SharedCount sharedCount = new SharedCount(zkClient,
                "/sharedCount",
                88);
        sharedCount.start();

        int value = sharedCount.getCount();
        System.out.println("当前值：" + value);

        sharedCount.setCount(99);

        return GraceJSONResult.ok(sharedCount.getCount());
    }
    /**
     * 获得参数
     * @return
     */
    @PostMapping("params")
    public GraceJSONResult params() throws Exception {

        // 版本二：真正的信号量 分布式信号量
        InterProcessSemaphoreV2 semaphoreV2 = new InterProcessSemaphoreV2(zkClient,
                "/semaphoreV2-locks",
                5);
        Lease lease = semaphoreV2.acquire();

        //读锁
        //InterProcessReadWriteLock readWriteLock = new InterProcessReadWriteLock(zkClient, "/rw-locks");
        //readWriteLock.readLock().acquire();


        SysParams sysParams = sysParamsService.getSysParams();


        semaphoreV2.returnLease(lease);

        //readWriteLock.readLock().release();

        SysParamsVO sysParamsVO = new SysParamsVO();
        BeanUtils.copyProperties(sysParams, sysParamsVO);
        sysParamsVO.setVersion(0);

        return GraceJSONResult.ok(sysParamsVO);
    }

    /**
     * 乐观锁，用户在一个界面长时间没操作 然后点设置
     * 内存，高并发查询更快
     * @return
     * @throws Exception
     */
    @PostMapping("params2")
    public GraceJSONResult params2() throws Exception {

        String path = "/" + ZK_MAX_RESUME_REFRESH_COUNTS;

        String dataString = new String(zkClient.getData().forPath(path));
        Integer maxCounts = Integer.valueOf(dataString);

        Stat stat = zkClient.checkExists().forPath(path);
        Integer version = stat.getVersion();

        SysParamsVO sysParamsVO = new SysParamsVO();
        sysParamsVO.setMaxResumeRefreshCounts(maxCounts);
        sysParamsVO.setVersion(version);

        return GraceJSONResult.ok(sysParamsVO);
    }

}

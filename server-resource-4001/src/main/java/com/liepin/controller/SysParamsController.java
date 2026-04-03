package com.liepin.controller;

import com.liepin.grace.result.GraceJSONResult;
import com.liepin.grace.result.ResponseStatusEnum;
import com.liepin.pojo.SysParams;
import com.liepin.pojo.vo.SysParamsVO;
import com.liepin.service.SysParamsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sys")
public class SysParamsController {

    @Autowired
    private SysParamsService sysParamsService;

    /**
     * 更新简历的最大刷新次数
     * @param maxCounts
     * @param version
     * @return
     */
    @PostMapping("modifyMaxResumeRefreshCounts")
    public GraceJSONResult modifyMaxResumeRefreshCounts(Integer maxCounts, Integer version) {

        if (maxCounts == null || maxCounts < 1)
            return GraceJSONResult.errorCustom(
                    ResponseStatusEnum.SYSTEM_PARAMS_SETTINGS_ERROR);

        //ZKLock zkLock = zkConnecter.getLock("imooc-lock");
        //zkLock.get();

        // 测试zookeeper分布式锁
        //try {
        //    Thread.sleep(2500);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
        sysParamsService.updateMaxResumeRefreshCounts(maxCounts, version);

        //zkLock.release();

        // TODO version zk的乐观锁机制（后面再讲）
        return GraceJSONResult.ok(0);
    }

    /**
     * 获得参数
     * @return
     */
    @PostMapping("params")
    public GraceJSONResult params() {

        SysParams sysParams = sysParamsService.getSysParams();

        SysParamsVO sysParamsVO = new SysParamsVO();
        BeanUtils.copyProperties(sysParams, sysParamsVO);
        sysParamsVO.setVersion(0);

        return GraceJSONResult.ok(sysParamsVO);
    }

}

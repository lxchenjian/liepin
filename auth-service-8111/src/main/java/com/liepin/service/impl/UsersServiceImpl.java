package com.liepin.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liepin.enums.Sex;
import com.liepin.enums.ShowWhichName;
import com.liepin.enums.UserRole;
import com.liepin.exceptions.GraceException;
import com.liepin.feign.WorkMicroServiceFeign;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.grace.result.ResponseStatusEnum;
import com.liepin.mapper.UsersMapper;
import com.liepin.mq.InitResumeMQConfig;
import com.liepin.mq.InitResumeMQProducerHandler;
import com.liepin.pojo.Users;
import com.liepin.service.UsersService;
import com.liepin.utils.DesensitizationUtil;
import com.liepin.utils.LocalDateUtils;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.GlobalTransactionContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 风间影月
 * @since 2022-09-04
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Autowired
    private WorkMicroServiceFeign workMicroServiceFeign;

    @Autowired
    private UsersMapper usersMapper;

    private static final String USER_FACE1 = "http://122.152.205.72:88/group1/M00/00/05/CpoxxF6ZUySASMbOAABBAXhjY0Y649.png";

    @Override
    public Users queryMobileIsExist(String mobile) {

        Users user = usersMapper.selectOne(new QueryWrapper<Users>()
                .eq("mobile", mobile));

        return user;
    }

    @Autowired
    public RabbitTemplate rabbitTemplate;
    @Autowired
    public InitResumeMQProducerHandler producerHandler;

    @Transactional
    @Override
    public Users createUsersAndInitResumeMQ(String mobile) {

        // 创建用户
        Users user = createUsers(mobile);

        // 通过消息助手类进行本地消息的存储
        producerHandler.saveLocalMsg(
                InitResumeMQConfig.INIT_RESUME_EXCHANGE,
                InitResumeMQConfig.ROUTING_KEY_INIT_RESUME,
                user.getId());


        // 发送消息，初始化简历  不够优雅，不和规范  如何修改？MyTransactionManager 自定义事务管理器
//        rabbitTemplate.convertAndSend(
//                InitResumeMQConfig.INIT_RESUME_EXCHANGE,
//                InitResumeMQConfig.ROUTING_KEY_INIT_RESUME,
//                user.getId());

        return user;
    }


    //@Transactional
    @GlobalTransactional
    @Override
    public Users createUsers(String mobile) {

        Users user = new Users();

        user.setMobile(mobile);
        user.setNickname("用户" + DesensitizationUtil.commonDisplay(mobile));
        user.setRealName("用户" + DesensitizationUtil.commonDisplay(mobile));
        user.setShowWhichName(ShowWhichName.nickname.type);

        user.setSex(Sex.secret.type);
        user.setFace(USER_FACE1);
        user.setEmail("");

        LocalDate birthday = LocalDateUtils
                .parseLocalDate("1980-01-01",
                        LocalDateUtils.DATE_PATTERN);
        user.setBirthday(birthday);

        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");
        user.setDescription("这家伙很懒，什么都没留下~");

        // 我参加工作的日期，默认使用注册当天的日期
        user.setStartWorkDate(LocalDate.now());
        user.setPosition("底层码农");
        user.setRole(UserRole.CANDIDATE.type);
        user.setHrInWhichCompanyId("");

        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        usersMapper.insert(user);

        // 发起远程调用，初始化用户简历，新增一条空记录
        // 注意：测试mq最终一致性注释掉
        // workMicroServiceFeign.init(user.getId());


        // 发起远程调用，初始化用户简历，新增一条空记录   配合切面
//        GraceJSONResult graceJSONResult = workMicroServiceFeign.init(user.getId());
//        if (graceJSONResult.getStatus() != 200) {
//            // 如果调用状态不是200，则手动回滚全局事务
//            String xid = RootContext.getXID();
//            if (StringUtils.isNotBlank(xid)) {
//                try {
//                    GlobalTransactionContext.reload(xid).rollback();
//                } catch (TransactionException e) {
//                    e.printStackTrace();
//                } finally {
//                    GraceException.display(ResponseStatusEnum.USER_REGISTER_ERROR);
//                }
//            }
//        }

        // 模拟除零异常——测试分布式事务是否可以回滚
       // int a = 1 / 0;

        return user;
    }
}

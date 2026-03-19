# 项目结构
hire-common: 通用工程，工具类、枚举类、异常类、常用属性类、资源类等等

hire-pojo: 所有实体类的管理，pojo、bean、entity、bo、vo、dto 等

hire-api: web接口（应用）的公用依赖

service-user: 用户微服务（7001）

service-company: 企业微服务（6001）


# 第三周 后端微服务基础架构与前端项目联调配备
1、项目层次与Maven聚合工程搭建
hire-common
hire-pojo
hire-api
service-user
service-company

2、Maven依赖面向对象配置

3、如何快速构建web接口并且暴露api？

4、SpringBoot多环境配置与启动配置

5、高效率开发插件Lombok讲解

6、如何进行优雅的Restful响应封装？

7、数据库表的初始化与无实体外键约束

8、如何配置MyBatisPlus逆向生成工具？

9、如何整合MyBatisPlus与四种主键模式？

10、SourceTree代码同步Gitee与分支操作

11、idea与Gitee代码同步

# 第四周 如何学会大厂微服务基础架构构建？
单体、分布式、集群、SOA技术架构演变

如何真正认识微服务？

微服务AKF拆分原则

微服务的CAP定理与数据一致性抉择

微服务Netflix与Alibaba的爱恨情仇

Spring Boot&amp;Cloud&amp;Alibaba 版本依赖兼容

微服务注册中心帮你上户口

Docker安装配置Nacos

微服务节点整合Nacos

构建多实例集群进行Nacos注册
1、添加${port:6001}
2、VM options - > -DPORT=7002

Nacos写入不同项目的通用配置
${prefix}-${spring.profiles.active}.${file-extension}

微服务网关Gateway与端口规范

构建微服务网关Gateway与负载均衡loadbalancer

本章小节与作业（03:09）

# 第五周 云短信注册登录全流程落地与JWT实现
第1章 云短信注册登录全流程落地

手机短信验证码一键注册登录流程阐述

详述用户表设计

申请腾讯云短信与秘钥配置

SpringBoot 集成腾讯云短信

快速构建授权中心

整合Redis并存储验证码

Redis锁机制限制ip发短信

使用拦截器限制60秒短信发送

封装优雅异常降低代码侵入性

Postman&ApiPost与Apifox 规范化接口文档与对接
post请求需要

使用Hibernate-Validate进行参数校验
参数校验
异常拦截

用户一键注册登录

分布式会话RedisToken

作业：RedisToken接口认证


第2章 JWT实现用户令牌的签发&状态切换
Switchhost 虚拟域名解决ip变动问题

服务状态与认证方案

无状态JWT(Json Web Token)

签发JWT令牌
测试类

校验JWT是否有效
测试类

自定义JWT工具类生成令牌
一键登录里面的工具类

第3章 基于微服务网关Gateway实现接口访问权限控制
Gateway过滤器校验JWT(1) - 路径匹配规则器


Gateway过滤器校验JWT(2) - 包装自定义错误

Gateway过滤器校验JWT(3) - 校验令牌

Gateway过滤器校验JWT(4) - header组装用户信息传递

Gateway过滤器校验JWT(5) - ThreadLocal存取用户信息

RedisToken校验与枚举应用
Redis token有状态的过滤器拦截校验
用户类型前缀以及header-json用枚举管理


视频：3-7 Nacos共享配置统一管理JWT秘钥最近学习


# 第六周 扫码登录与加盐登录实现与AR模式落地
第1章 扫描、加盐登录业务的实现与落地

扫码登录流程分析


扫码登录 - 获得登录二维码
测试扫码的时候，需要把数据库的role改为2

Gateway设置跨域解决方案

扫码登录 - 手机扫码预登录

扫码登录 - 检查二维码是否被读取

扫码登录 - 手机确认登录

扫码登录 - 刷新并跳转至首页

IP黑名单网关限流 - 流程分析

IP黑名单网关限流 - 代码实现

第2章 管理平台Admin登录业务落地实现

Admin表结构梳理与密码加盐法则

管理平台admin登录业务的落地实现

[admin管理平台登录]

Admin页面刷新信息

admin账号 - 分配账号

[admin账号管理]

admin账号 - 账号列表

admin账号 - 删除账号

admin账号 - 重置密码（AR领域驱动）
mybatisplus-AR模式-Active Record(活动记录)-领域驱动模型

一个模型(model)对应数据库一张表模型的一个实例的一个实例对应一条记录

AOP计算统计service业务执行时间
时间计算方式一：System.currentTimeMillis()

视频：2-9 StopWatch 秒表时间的优雅统计最近学习
时间计算方式二：片段

视频：2-10 本章小节（06:02）


# 第七周 接口重试机制设计与消息队列（重试机制SpringRetry、Spring异步任务、RabbitMQ异步解耦）
本章概述最近学习

SpringRetry重试机制

Spring异步任务时序图

Spring异步任务发送短信

大白话阐述解耦场景需求

大白话告诉你什么是MQ

MQ选型与应用场景

RabbitMQ工作模型

Docker安装配置RabbitMQ

RabbitMQ管理控制台

RabbitMQ 简单模式构建生产者
FooProducer

RabbitMQ 简单模式构建消费者


RabbitMQ工作模式 - WorkQueues
WorkQueuesProducer、WorkQueuesConsumerA、WorkQueuesConsumerB

RabbitMQ工作模式 - 发布订阅
PubSubProducer、PubSubConsumerA、PubSubConsumerB

RabbitMQ工作模式 - 路由模式

RabbitMQ工作模式 - 通配符模式

RabbitMQ集成SpringBoot(上) - 异步解耦发送短信

RabbitMQ集成SpringBoot(下) - 监听消费短信发送
扩展作业：短信适合工作模式

jasypt配置文件密码加解密
加盐工具类方法重载

消息的可靠性投递Confirm机制

消息的可靠性投递Return机制

消费端可靠性ACK机制

RabbitMQ 消费者消息限流

RabbitMQ ttl特性控制短信队列超时

RabbitMQ 死信队列的实现

本章小结

# 第八周 分布式事务与数据一致性主流解决方案落地
第1章 分布式事务与数据一致性主流解决方案落地
本章概述
初始化用户简历
微服务远程调用OpenFeign
客户端与服务端负载均衡机制
微服务负载均衡NacosLoadBalancer
拓展：OSI七层网络模型
微服务分布式环境下的事务问题
BASE理论与强弱一致性
常见分布式事务解决方案-2PC
常见分布式事务解决方案-TCC
常见分布式事务解决方案-最大努力通知
常见分布式事务解决方案-最终一致性
Seata介绍与术语
Seata生命周期 
Seata数据表初始化
Docker安装配置Seata服务
Seata 客户端依赖坐标引入与踩坑排雷
Seata 客户端全局事务配置与实现最近学习
全局异常-Seata还会生效吗？
全局异常-Seata手动回滚
本周小结

步骤：
借助消息队列操作事务的弱一致性（最终一致性）
最终一致性落地（1）-异步解耦微服务
最终一致性落地（2）-存储本地消息
最终一致性落地（3）-自定义事务管理器发送MQ消息
最终一致性落地（4）-确认并删除本地消息
初始化简历的最佳方案
本章小节
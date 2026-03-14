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

用户一键注册登录

分布式会话RedisToken

作业：RedisToken接口认证


第2章 JWT实现用户令牌的签发&状态切换
视频：2-1 Switchhost 虚拟域名解决ip变动问题（09:31）

视频：2-2 服务状态与认证方案（17:24）

视频：2-3 无状态JWT(Json Web Token)（09:59）

视频：2-4 签发JWT令牌（11:21）

视频：2-5 校验JWT是否有效（08:22）

视频：2-6 自定义JWT工具类生成令牌（24:42）

第3章 基于微服务网关Gateway实现接口访问权限控制
视频：3-1 Gateway过滤器校验JWT(1) - 路径匹配规则器（17:14）

视频：3-2 Gateway过滤器校验JWT(2) - 包装自定义错误（13:31）

视频：3-3 Gateway过滤器校验JWT(3) - 校验令牌（20:50）

视频：3-4 Gateway过滤器校验JWT(4) - header组装用户信息传递（13:19）

视频：3-5 Gateway过滤器校验JWT(5) - ThreadLocal存取用户信息（14:28）

视频：3-6 作业：RedisToken校验与枚举应用（02:22）

视频：3-7 Nacos共享配置统一管理JWT秘钥最近学习

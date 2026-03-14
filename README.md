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
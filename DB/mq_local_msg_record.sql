CREATE TABLE `mq_local_msg_record` (
                                       `id` VARCHAR(32) NOT NULL COMMENT '主键ID',
                                       `target_exchange` VARCHAR(128) NOT NULL COMMENT '目标交换机',
                                       `routing_key` VARCHAR(128) NOT NULL COMMENT '消息路由键',
                                       `msg_content` VARCHAR(255) NOT NULL COMMENT '消息内容',
                                       `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='本地消息记录表';
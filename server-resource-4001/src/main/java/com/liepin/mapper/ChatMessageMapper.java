package com.liepin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liepin.pojo.ChatMessage;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 聊天信息存储表 Mapper 接口
 * </p>
 *
 * @author 风间影月
 * @since 2022-09-04
 */
@Repository
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

}

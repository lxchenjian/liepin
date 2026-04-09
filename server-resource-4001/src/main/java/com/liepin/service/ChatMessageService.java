package com.liepin.service;

import com.liepin.pojo.netty.ChatMsg;
import com.liepin.utils.PagedGridResult;

/**
 * 聊天消息的service
 */
public interface ChatMessageService {

    /**
     * 查询分页的聊天记录
     * @param senderId
     * @param receiverId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryChatMsgList(String senderId,
                                            String receiverId,
                                            Integer page,
                                            Integer pageSize);
    /**
     * 保存聊天消息
     */
    public void saveMsg(ChatMsg chatMsg);

    /**
     * 修改消息的语音状态
     * @param msgId
     */
    public void updateMsgSignRead(String msgId);
}

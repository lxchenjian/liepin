package com.liepin.controller;

import com.liepin.base.BaseInfoProperties;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.service.ChatMessageService;
import com.liepin.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("chat")
public class ChatController extends BaseInfoProperties {

    @Autowired
    private ChatMessageService messageService;

    /**
     * 分页查询聊天记录
     * @param senderId
     * @param receiverId
     * @param page
     * @param pageSize
     * @return
     */
    @PostMapping("list/{senderId}/{receiverId}")
    public GraceJSONResult list(@PathVariable("senderId") String senderId,
                                @PathVariable("receiverId") String receiverId,
                                Integer page,
                                Integer pageSize) {

        if (page == null) page = 1;
        if (pageSize == null) pageSize = 20;

        PagedGridResult gridResult = messageService.queryChatMsgList(senderId,
                receiverId, page, pageSize);
        return GraceJSONResult.ok(gridResult);
    }

    /**
     * 获得我消息的未读数
     * @param myId
     * @return
     */
    @PostMapping("getMyUnReadCounts")
    public GraceJSONResult getMyUnReadCounts(String myId) {
        Map map = redis.hgetall(CHAT_MSG_LIST + ":" + myId);
        return GraceJSONResult.ok(map);
    }

    /**
     * 清理我的未读消息，变为0
     * @param myId
     * @param oppositeId
     * @return
     */
    @PostMapping("clearMyUnReadCounts")
    public GraceJSONResult getMyUnReadCounts(String myId, String oppositeId) {
        redis.setHashValue(CHAT_MSG_LIST + ":" + myId, oppositeId, "0");
        return GraceJSONResult.ok();
    }

    /**
     * 签收语音消息，标记已读
     * @param msgId
     * @return
     */
    @PostMapping("signRead/{msgId}")
    public GraceJSONResult signRead(@PathVariable("msgId") String msgId) {
        messageService.updateMsgSignRead(msgId);
        return GraceJSONResult.ok();
    }

}

package com.liepin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.liepin.base.BaseInfoProperties;
import com.liepin.mapper.ChatMessageMapper;
import com.liepin.pojo.ChatMessage;
import com.liepin.pojo.netty.ChatMsg;
import com.liepin.service.ChatMessageService;
import com.liepin.utils.PagedGridResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatMessageServiceImpl extends BaseInfoProperties implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    public PagedGridResult queryChatMsgList(String senderId,
                                            String receiverId,
                                            Integer page,
                                            Integer pageSize) {
        PageHelper.startPage(page, pageSize);

        QueryWrapper queryWrapper = new QueryWrapper<ChatMessage>()
                .or(qw -> qw.eq("sender_id", senderId)
                            .eq("receiver_id", receiverId))
                .or(qw -> qw.eq("sender_id", receiverId)
                            .eq("receiver_id", senderId))
                .orderByDesc("chat_time");
        List<ChatMessage> list = chatMessageMapper.selectList(queryWrapper);

        // 获得列表后，倒着排序，因为聊天记录是展现最新的数据在最下方，旧的数据在上方
        // 逆向逆序处理
        List<ChatMessage> msgList = list.stream().sorted(
                Comparator.comparing(ChatMessage::getChatTime)
        ).collect(Collectors.toList());

        return setterPagedGrid(msgList, page);
    }

    @Transactional
    @Override
    public void saveMsg(ChatMsg chatMsg) {

        ChatMessage chatMessage = new ChatMessage();
        BeanUtils.copyProperties(chatMsg, chatMessage);

        // FIXME: 思考 为何主键id不在当前服务自动生成？而是在netty中生成
        chatMessage.setId(chatMsg.getMsgId());

        chatMessageMapper.insert(chatMessage);

        String receiverId = chatMsg.getReceiverId();
        String senderId = chatMsg.getSenderId();

        redis.incrementHash(CHAT_MSG_LIST + ":" + receiverId, senderId, 1);
    }

    @Transactional
    @Override
    public void updateMsgSignRead(String msgId) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(msgId);
        chatMessage.setIsRead(true);

        chatMessageMapper.updateById(chatMessage);
    }
}

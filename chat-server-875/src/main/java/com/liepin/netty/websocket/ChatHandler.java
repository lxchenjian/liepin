package com.liepin.netty.websocket;

import com.a3test.component.idworker.IdWorkerConfigBean;
import com.a3test.component.idworker.Snowflake;
import com.liepin.enums.MsgTypeEnum;
import com.liepin.netty.mq.MessagePublisher;
import com.liepin.pojo.netty.ChatMsg;
import com.liepin.pojo.netty.DataContent;
import com.liepin.utils.GsonUtils;
import com.liepin.utils.LocalDateUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 处理消息的handler
 */
// TextWebSocketFrame 消息的载体
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 用于记录和管理所有客户端的channel
    public static ChannelGroup clients =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        // 获取客户端传输过来的消息
        String content = msg.text();
        System.out.println("接受到的数据：" + content);

        // 获得当前用户连接的客户端channel
        Channel currentChannel = ctx.channel();
        String currentChannelId = currentChannel.id().asLongText();
        //String currentChannelIdShort = currentChannel.id().asShortText();
        //System.out.println("客户端currentChannelId：" + currentChannelId);
        //System.out.println("客户端currentChannelIdShort：" + currentChannelIdShort);

        // 1. 获取客户端发来的消息并且解析
        DataContent dataContent = GsonUtils.stringToBean(content, DataContent.class);
        ChatMsg chatMsg = dataContent.getChatMsg();

        String msgText = chatMsg.getMsg();
        String receiverId = chatMsg.getReceiverId();
        String senderId = chatMsg.getSenderId();

        // 时间校准，以服务器的时间为主
        chatMsg.setChatTime(LocalDateTime.now());

        // 获得消息类型，用于判断
        Integer msgType = chatMsg.getMsgType();

        if (msgType == MsgTypeEnum.CONNECT_INIT.type) {
            // 当websocket初次open的时候，初始化channel会话，把用户和channel关联起来
            UserChannelSession.putUserChannelIdRelation(currentChannelId, senderId);
            UserChannelSession.putMultiChannels(senderId, currentChannel);
        } else if (msgType == MsgTypeEnum.WORDS.type
                || msgType == MsgTypeEnum.IMAGE.type
                || msgType == MsgTypeEnum.VIDEO.type
                || msgType == MsgTypeEnum.VOICE.type
                || msgType == MsgTypeEnum.RESUME.type
                || msgType == MsgTypeEnum.INVITE.type
                || msgType == MsgTypeEnum.MSG_INTERVIEW_CANCEL.type
                || msgType == MsgTypeEnum.MSG_INTERVIEW_REFUSE.type
                || msgType == MsgTypeEnum.MSG_INTERVIEW_ACCEPT.type
        ) {

            /**
             * 发送消息
             */

            // 此处为mq异步解耦，保存到数据库无法获得消息的id主键，固使用Snowflake直接生成
            Snowflake snowflake = new Snowflake(new IdWorkerConfigBean());
            String sid = snowflake.nextId();
            System.out.println("sid = " + sid);
            chatMsg.setMsgId(sid);

            // 从全局用户关系中获得对方（接受消息方）的channel
            List<Channel> multiChannels = UserChannelSession.getMultiChannels(receiverId);
            if (multiChannels == null || multiChannels.size() == 0 || multiChannels.isEmpty()) {
                // multiChannels 为空代表用户离/断线状态，消息不需要发送，后续可以直接存储到(数据)库中
                chatMsg.setIsReceiverOnLine(false);
            } else {
                chatMsg.setIsReceiverOnLine(true);

                // 当multiChannels不为空，则同步账户多端接受消息
                for (Channel c : multiChannels) {
                    Channel findChannel = clients.find(c.id());
                    if (findChannel != null) {

                        if (msgType == MsgTypeEnum.VOICE.type) {
                            chatMsg.setIsRead(false);
                        }
                        dataContent.setChatMsg(chatMsg);

                        String chatTimeFormat = LocalDateUtils.format(
                                                    chatMsg.getChatTime(),
                                                    LocalDateUtils.DATETIME_PATTERN_2);
                        dataContent.setChatTime(chatTimeFormat);

                        findChannel.writeAndFlush(
                                new TextWebSocketFrame(
                                        GsonUtils.object2String(dataContent)
                                )
                        );
                    }
                }
            }

            // 如果发送方在多个设备登录，则自己的消息同步给其他设备
            List<Channel> myOtherChannels = UserChannelSession
                    .getMyOtherChannels(senderId, currentChannelId);
            for (Channel c : myOtherChannels) {
                Channel findChannel = clients.find(c.id());
                if (findChannel != null) {
                    dataContent.setChatMsg(chatMsg);

                    String chatTimeFormat = LocalDateUtils.format(
                            chatMsg.getChatTime(),
                            LocalDateUtils.DATETIME_PATTERN_2);
                    dataContent.setChatTime(chatTimeFormat);

                    findChannel.writeAndFlush(
                            new TextWebSocketFrame(
                                    GsonUtils.object2String(dataContent)
                            )
                    );
                }
            }

            MessagePublisher.sendMsgToSave(chatMsg);
        }


        //TextWebSocketFrame replayMsg = new TextWebSocketFrame("当前客户端的id为：" + currentChannelId);
        //currentChannel.writeAndFlush(replayMsg);

        // 测试向所有客户端群发消息
        //for (Channel channel : clients) {
        //    channel.writeAndFlush(replayMsg);
        //}
        //clients.writeAndFlush(replayMsg);

        UserChannelSession.outputMulti();
    }

    /**
     * 客户端连接服务端之后（打开链接）
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        System.out.println("客户端连接，channel对应的长id为：" + channelId);

        // 获取客户端的channel，并且放到ChannelGroup中进行管理（可以作为一个群组）
        clients.add(ctx.channel());
    }

    /**
     * 关闭链接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当触发handlerRemoved，ChannelGroup自动移除对应客户端的channel
        String channelId = ctx.channel().id().asLongText();
        System.out.println("客户端断开，channel对应的长id为：" + channelId);

        clients.remove(ctx.channel());

        // 移除多余会话
        String userId = UserChannelSession.getUserIdByChannelId(channelId);
        UserChannelSession.removeUselessChannels(channelId, userId);


        UserChannelSession.outputMulti();
    }

    /**
     * 捕获异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();

        // 发生异常之后关闭连接（关闭channel）
        ctx.channel().close();
        // 随后从ChannelGroup中移除对应的channel
        clients.remove(ctx.channel());

        String channelId = ctx.channel().id().asLongText();
        // 移除多余会话
        String userId = UserChannelSession.getUserIdByChannelId(channelId);
        UserChannelSession.removeUselessChannels(channelId, userId);
    }
}

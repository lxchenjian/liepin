package com.liepin.netty.websocket;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会话管理
 * 用户id和channel的管理关系处理
 */
public class UserChannelSession {

    // 用于多端多设备同时接受消息，允许同一个账号在多个设备同时在线，比如iPad与iPhone都能同时收到消息
    private static Map<String, List<Channel>> multiSession = new HashMap<>();

    // 用于记录用户id和客户端longId的关联关系
    private static Map<String, String> userChannelIdRelation = new HashMap<>();
    public static void putUserChannelIdRelation(String channelId, String userId) {
        userChannelIdRelation.put(channelId, userId);
    }
    public static String getUserIdByChannelId(String channelId) {
        return userChannelIdRelation.get(channelId);
    }

    /**
     * 建立连接后初始化用户会话
     * @param userId
     * @param channel
     */
    public static void putMultiChannels(String userId, Channel channel) {
        List<Channel> channels = getMultiChannels(userId);

        if (channels == null || channels.size() == 0) {
            channels = new ArrayList<>();
        }

        channels.add(channel);

        multiSession.put(userId, channels);
    }
    public static List<Channel> getMultiChannels(String userId) {
        return multiSession.get(userId);
    }


    /**
     * 获得我的其他设备（端）的channel，可以用于在我发送消息的时候，进行同步给其他设备
     * @param userId
     * @param channelId
     * @return
     */
    public static List<Channel> getMyOtherChannels(String userId, String channelId) {

        List<Channel> channels = getMultiChannels(userId);
        if (channels == null || channels.size() == 0) {
            return null;
        }

        List<Channel> myOtherChannels = new ArrayList<>();
        for (int i = 0 ; i < channels.size() ; i ++ ) {
            Channel tmpChannel = channels.get(i);
            if (!tmpChannel.id().asLongText().equals(channelId)) {
                myOtherChannels.add(tmpChannel);
            }
        }

        return myOtherChannels;
    }


    /**
     * 移除多余的无用channel（会话）
     * @param channelId
     * @param userId
     */
    public static void removeUselessChannels(String channelId, String userId) {
        List<Channel> channels = getMultiChannels(userId);
        if (channels == null || channels.size() == 0) {
            return;
        }

        for (int i = 0 ; i < channels.size() ; i ++) {
            Channel tmpChannel = channels.get(i);
            if (tmpChannel.id().asLongText().equals(channelId)) {
                channels.remove(i);
            }
        }

        if (channels.size() > 0) {
            multiSession.put(userId, channels);
        } else {
            multiSession.remove(userId);
        }

    }



    public static void outputMulti() {
        System.out.println("------------------------------------------");

        for (HashMap.Entry<String, List<Channel>> entry: multiSession.entrySet()) {
            System.out.println("++++++++++");

            System.out.println("UserId：" + entry.getKey());
            List<Channel> temp = entry.getValue();
            for (Channel c : temp) {
                System.out.println("\t\tChannelId：" + c.id().asLongText());
            }

            System.out.println("++++++++++");
        }


        System.out.println("------------------------------------------");
    }

}

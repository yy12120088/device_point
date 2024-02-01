package com.qinglan.example.device_point.server.session;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DeviceRegSession {

    public DeviceRegSession() {
        // TODO document why this constructor is empty
    }
    //缓存通道
    private static Map<String, Channel> regSession = new ConcurrentHashMap<>();

    private static Map<ChannelId, String> channelInfo = new ConcurrentHashMap<>();

    //判断是否注册
    public Channel isReg(String uid) {
        return regSession.get(uid);
    }

    public static void connect(Channel channel, String uid){
        //重复链接清除旧链接
        if(regSession.containsKey(uid)){
            Channel oldChannel = regSession.get(uid);
            channelInfo.remove(oldChannel.id());
            oldChannel.close();
            log.info("----------------offline uid------{}--------", uid);
        }
        log.info("---------------------uid:{}--------------online----", uid);
        regSession.put(uid, channel);
        channelInfo.put(channel.id(), uid);
    }

    public static void disconnect(Channel channel){
        String uid = channelInfo.remove(channel.id());
        if (uid != null){
            log.info("----------------offline uid------{}--------", uid);
            regSession.remove(uid);
        }
    }

    public String subDeviceData(String uid){
        return null;
    }

    /**
     * 响应消息缓存
     */
    public static Cache<String, BlockingQueue<String>> responseMsgCache = CacheBuilder.newBuilder()
            .maximumSize(50000)
            .expireAfterWrite(4, TimeUnit.SECONDS)
            .build();


    /**
     * 等待响应消息
     * @param key 消息唯一标识
     * @return ReceiveDdcMsgVo
     */
    public String waitReceiveMsg(String key) {
//        System.out.println("waitReceiveMsg.size()->>>>>>>>>>>>>>>>>>>" + responseMsgCache.size());
        try {
            //设置超时时间
            String vo = Objects.requireNonNull(responseMsgCache.getIfPresent(key))
                    .poll(4000, TimeUnit.MILLISECONDS);
            //删除key
            responseMsgCache.invalidate(key);
            return vo;
        } catch (Exception e) {
            log.error("Fetch data exception,sn={},msg=null",key);
            return null;
        }
    }

    /**
     * 初始化响应消息的队列
     * @param key 消息唯一标识
     */
    public void initReceiveMsg(String key) {
        responseMsgCache.put(key,new LinkedBlockingQueue<String>(1));
//        System.out.println("initReceiveMsg.size()->>>>>>>>>>>>>>>>>>>" + responseMsgCache.size());
    }

    /**
     * 设置响应消息
     * @param key 消息唯一标识
     */
    public void setReceiveMsg(String key, String msg) {
//        System.out.println("setReceiveMsg.size()->>>>>>>>>>>>>>>>>>>" + responseMsgCache.size());
        if(responseMsgCache.getIfPresent(key) != null){
            responseMsgCache.getIfPresent(key).add(msg);
            return;
        }
        log.warn("sn {} not empty",key);
    }
}

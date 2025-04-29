package com.qinglan.example.device_point.controller;

import com.qinglan.example.device_point.server.msg.DeviceInfo;
import com.qinglan.example.device_point.server.msg.ServerLBSInfo;
import com.qinglan.example.device_point.server.session.DeviceRegSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    DeviceRegSession deviceRegSession;

    /**
     * 订阅设备数据
     * @param uid
     * @return  Subscription device data
     * @throws InterruptedException
     */
    @GetMapping("/test")
    public String test(@RequestParam("uid") String uid) throws InterruptedException {
        Channel channel = deviceRegSession.isReg(uid);
        if (channel == null){
            return null;
        }
        String channelId = channel.id().asLongText();
        ServerLBSInfo.SetModeReq.Builder modeBuild = ServerLBSInfo.SetModeReq.newBuilder();
        int type = 26;
        String key = type + channelId;
        modeBuild.setSeq(26);
        modeBuild.setSeconds(30);
        ByteBuf buffer = channel.alloc().buffer();
        buffer.writeByte(26);
        buffer.writeBytes(modeBuild.build().toByteArray());
        deviceRegSession.initReceiveMsg(key);
        channel.writeAndFlush(buffer);
        String s = deviceRegSession.waitReceiveMsg(key);
        return s;
    }

    /**
     * 获取设备属性
     * @param uid
     * @return Device prop
     * @throws InterruptedException
     */
    @GetMapping("/get/prop")
    public String getProp(@RequestParam("uid") String uid) throws InterruptedException {
        Channel channel = deviceRegSession.isReg(uid);
        if (channel == null){
            return null;
        }
        String channelId = channel.id().asLongText();
        int type = 11;
        String key = type + channelId;
        ByteBuf buffer = channel.alloc().buffer();
        System.out.println("before write and flush, buf.refCnt(): " + buffer.refCnt());
        buffer.writeByte(type);
        deviceRegSession.initReceiveMsg(key);
        ChannelFuture channelFuture = channel.writeAndFlush(buffer);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                // 发送数据成功以后，再看看引用计数
                System.out.println("after write and flush completed, buf.refCnt(): " + buffer.refCnt());
            }
        });
        String s = deviceRegSession.waitReceiveMsg(key);
        return s;
    }

    /**
     * 设置设备属性
     * @param uid
     * @return Device prop
     * @throws InterruptedException
     */
    @GetMapping("/set/prop")
    public String getProp(@RequestParam("uid") String uid, @RequestParam("key") String key, @RequestParam("value") String value) throws InterruptedException {
        Channel channel = deviceRegSession.isReg(uid);
        if (channel == null){
            return null;
        }
        String channelId = channel.id().asLongText();
        ServerLBSInfo.SetDeviceProperty.Builder builder = ServerLBSInfo.SetDeviceProperty
                .newBuilder();
        builder.setSeq(1);
        builder.setKey(key);
        builder.setValue(value);
        int type = 9;
        String channelKey = type + channelId;
        ByteBuf buffer = channel.alloc().buffer();
        System.out.println("before write and flush, buf.refCnt(): " + buffer.refCnt());
        buffer.writeByte(type);
        buffer.writeBytes(builder.build().toByteArray());
        deviceRegSession.initReceiveMsg(channelKey);
        ChannelFuture channelFuture = channel.writeAndFlush(buffer);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                // 发送数据成功以后，再看看引用计数
                System.out.println("after write and flush completed, buf.refCnt(): " + buffer.refCnt());
            }
        });
        String s = deviceRegSession.waitReceiveMsg(channelKey);
        return s;
    }

    /**
     * 开启通话
     * @param uid
     * @return Device prop
     * @throws InterruptedException
     */
    @PostMapping("/startVoice")
    public String startHdVoice(@RequestParam("uid") String uid) {
        Channel channel = deviceRegSession.isReg(uid);
        if (channel == null){
            return null;
        }
        //todo 远程调用清澜服务器获取license token appid


        String channelId = channel.id().asLongText();
        DeviceInfo.StartVoiceReq.Builder builder = DeviceInfo.StartVoiceReq
                .newBuilder().setSeq(1).setAppid("appid").setLicense("license").setToken("token").setChannel("name");
        int type = 50;
        String channelKey = type + channelId;
        ByteBuf buffer = channel.alloc().buffer();
        System.out.println("before write and flush, buf.refCnt(): " + buffer.refCnt());
        buffer.writeByte(type);
        buffer.writeBytes(builder.build().toByteArray());
        deviceRegSession.initReceiveMsg(channelKey);
        ChannelFuture channelFuture = channel.writeAndFlush(buffer);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                // 发送数据成功以后，再看看引用计数
                System.out.println("after write and flush completed, buf.refCnt(): " + buffer.refCnt());
            }
        });
        String s = deviceRegSession.waitReceiveMsg(channelKey);
        return s;
    }

    /**
     * ota
     * @param uid
     * @return Device prop
     * @throws InterruptedException
     */
    @PostMapping("/ota")
    public String startOta(@RequestParam("uid") String uid) {
        Channel channel = deviceRegSession.isReg(uid);
        if (channel == null){
            return null;
        }
        //todo 文件下载地址 需要有文件下载的接口能力 http/https


        String channelId = channel.id().asLongText();
        DeviceInfo.OTAReq.Builder builder = DeviceInfo.OTAReq
                .newBuilder().setSeq(1).setESPFileSHA256("***").setESPFileSize(1231).setESPFileUrl("http://**").setEspsfver("2.0")
                .setRadarFileSHA256("***").setRadarFileSize(1231).setRadarFileUrl("http://**").setRadarsfver("2.0");
        int type = 16;
        String channelKey = type + channelId;
        ByteBuf buffer = channel.alloc().buffer();
        System.out.println("before write and flush, buf.refCnt(): " + buffer.refCnt());
        buffer.writeByte(type);
        buffer.writeBytes(builder.build().toByteArray());
        deviceRegSession.initReceiveMsg(channelKey);
        ChannelFuture channelFuture = channel.writeAndFlush(buffer);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                // 发送数据成功以后，再看看引用计数
                System.out.println("after write and flush completed, buf.refCnt(): " + buffer.refCnt());
            }
        });
        String s = deviceRegSession.waitReceiveMsg(channelKey);
        return s;
    }
}

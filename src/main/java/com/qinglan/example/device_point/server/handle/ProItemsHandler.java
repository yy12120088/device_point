package com.qinglan.example.device_point.server.handle;


import com.alibaba.fastjson2.JSONObject;
import com.qinglan.example.device_point.server.msg.ServerLBSInfo;
import com.qinglan.example.device_point.server.session.DeviceRegSession;
import com.qinglan.example.device_point.server.util.SpringUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ChannelHandler.Sharable
@Slf4j
public class ProItemsHandler extends SimpleChannelInboundHandler<ServerLBSInfo.ProPertyItems> {

    DeviceRegSession deviceRegSession = SpringUtils.getBean(DeviceRegSession.class);

    /**
     * 设备属性
     * Device Properties
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerLBSInfo.ProPertyItems msg) throws Exception {
        try {
            int type = 11;
            String channelId = ctx.channel().id().asLongText();
            String key = type + String.valueOf(channelId);
            JSONObject jsonObject = new JSONObject();
            List<ServerLBSInfo.ProPertyItem> propertiesList = msg.getPropertiesList();
            for (ServerLBSInfo.ProPertyItem proPertyItem : propertiesList) {
                jsonObject.put(proPertyItem.getKey(), proPertyItem.getValue());
            }
            log.error("====================channelRead0====key:{}===========", key);
            deviceRegSession.setReceiveMsg(key, jsonObject.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}

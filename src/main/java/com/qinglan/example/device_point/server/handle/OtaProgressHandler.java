package com.qinglan.example.device_point.server.handle;

import com.alibaba.fastjson2.JSONObject;
import com.qinglan.example.device_point.server.msg.DeviceInfo;
import com.qinglan.example.device_point.server.session.DeviceRegSession;
import com.qinglan.example.device_point.server.util.SpringUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class OtaProgressHandler extends SimpleChannelInboundHandler<DeviceInfo.OTAProgress> {

    DeviceRegSession deviceRegSession = SpringUtils.getBean(DeviceRegSession.class);

    /**
     * ota结果响应
     * Set Property Return
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DeviceInfo.OTAProgress msg) throws Exception {
        try {
            log.info("-------------ota progress:{}--msg:{}--------", msg.getProgress(), msg.getErrMsg());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}

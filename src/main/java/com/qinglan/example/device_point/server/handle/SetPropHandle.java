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

@ChannelHandler.Sharable
@Slf4j
public class SetPropHandle extends SimpleChannelInboundHandler<ServerLBSInfo.SetPropResponse> {

    DeviceRegSession deviceRegSession = SpringUtils.getBean(DeviceRegSession.class);

    /**
     * 获取服务器地址
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerLBSInfo.SetPropResponse msg) throws Exception {
        try {
            int type = 9;
            String channelId = ctx.channel().id().asLongText();
            String key = type + String.valueOf(channelId);
            JSONObject res = new JSONObject();
            res.put("result", msg.getResult());
            res.put("errmsg", msg.getErrmsg());
            res.put("seq", msg.getSeq());
            deviceRegSession.setReceiveMsg(key, res.toJSONString());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}

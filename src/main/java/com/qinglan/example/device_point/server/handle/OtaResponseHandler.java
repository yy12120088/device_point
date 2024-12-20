package com.qinglan.example.device_point.server.handle;

import com.alibaba.fastjson2.JSONObject;
import com.qinglan.example.device_point.server.msg.DeviceInfo;
import com.qinglan.example.device_point.server.session.DeviceRegSession;
import com.qinglan.example.device_point.server.util.SpringUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

@ChannelHandler.Sharable
public class OtaResponseHandler extends SimpleChannelInboundHandler<DeviceInfo.OtaResponse> {

    DeviceRegSession deviceRegSession = SpringUtils.getBean(DeviceRegSession.class);

    /**
     * ota结果响应
     * Set Property Return
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DeviceInfo.OtaResponse msg) throws Exception {
        try {
            int type = 16;
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

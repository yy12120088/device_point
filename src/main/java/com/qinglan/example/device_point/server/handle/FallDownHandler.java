package com.qinglan.example.device_point.server.handle;

import com.google.protobuf.ByteString;
import com.qinglan.example.device_point.server.msg.ServerLBSInfo;
import com.qinglan.example.device_point.server.session.DeviceRegSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class FallDownHandler extends SimpleChannelInboundHandler<ServerLBSInfo.ObjectFallDown> {

    /**
     * 跌倒
     * Trajectory event
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerLBSInfo.ObjectFallDown msg) throws Exception {
        try {
            String uid = DeviceRegSession.getUidByChannelId(ctx.channel().id());
            ByteString falls = msg.getFalls();
            log.info("-------uid:{}------fall-status:{}----------", uid, falls);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

}

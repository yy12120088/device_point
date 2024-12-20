package com.qinglan.example.device_point.server.handle;

import com.qinglan.example.device_point.server.msg.ServerLBSInfo;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class PositionStatisticHandler extends SimpleChannelInboundHandler<ServerLBSInfo.PositionStatisticReport> {

    /**
     * 轨迹事件
     * Trajectory event
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerLBSInfo.PositionStatisticReport msg) throws Exception {
        try {
            log.info("-------------statistic:{}----------", msg.getData());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

}

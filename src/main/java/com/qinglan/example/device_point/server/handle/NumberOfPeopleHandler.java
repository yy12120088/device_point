package com.qinglan.example.device_point.server.handle;

import com.google.protobuf.ByteString;
import com.qinglan.example.device_point.server.msg.ServerLBSInfo;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class NumberOfPeopleHandler extends SimpleChannelInboundHandler<ServerLBSInfo.NumberOfPeopleData> {

    /**
     * 跌倒
     * Trajectory event
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerLBSInfo.NumberOfPeopleData msg) throws Exception {
        try {
            ByteString number = msg.getData();
            byte[] bytes = number.toByteArray();
            log.info("-------------number-people:{}----------", bytes[0]);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

}

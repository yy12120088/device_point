package com.qinglan.example.device_point.server.handle;


import com.google.protobuf.ByteString;
import com.qinglan.example.device_point.server.msg.ServerLBSInfo;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@ChannelHandler.Sharable
@Slf4j
public class PositionDateHandler extends SimpleChannelInboundHandler<ServerLBSInfo.PositionData> {


    /**
     * position
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerLBSInfo.PositionData msg) {

        try {
            ByteString data = msg.getData();
            byte[] r = data.toByteArray();

            log.info("收到雷达轨迹数据------>{}", Arrays.toString(r));
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}

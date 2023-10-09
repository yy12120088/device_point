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
public class BreathDateHandler extends SimpleChannelInboundHandler<ServerLBSInfo.ObjectData> {

    /**
     * 呼吸心率
     * Respiratory heart rate
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerLBSInfo.ObjectData msg) {

        try {
            ByteString data = msg.getData();
            byte[] r = data.toByteArray();

            log.info("收到雷达breath rate数据------>{}", Arrays.toString(r));
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


}

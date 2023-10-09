package com.qinglan.example.device_point.server.handle;


import com.qinglan.example.device_point.server.msg.ServerLBSInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class GetServerHandler extends SimpleChannelInboundHandler<ServerLBSInfo.GetServerReq> {


    /**
     * 获取服务器地址
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerLBSInfo.GetServerReq msg) throws Exception {
        try {
            ServerLBSInfo.GetServerResponse.Builder serverInfo = ServerLBSInfo.GetServerResponse.newBuilder();
            serverInfo.setSeq(2);
            serverInfo.setResult(0);
            serverInfo.setServer("192.168.1.88");
            serverInfo.setPort(1060);

            ByteBuf buffer = ctx.alloc().buffer();
            byte[] data = serverInfo.build().toByteArray();
            //type = 2
            buffer.writeByte(2);
            buffer.writeBytes(data);
            ctx.writeAndFlush(buffer);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}

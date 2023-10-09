package com.qinglan.example.device_point.server.handle;


import com.qinglan.example.device_point.server.msg.DeviceInfo;
import com.qinglan.example.device_point.server.session.DeviceRegSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ChannelHandler.Sharable
public class RegistRequestHandler extends SimpleChannelInboundHandler<DeviceInfo.RegisterReq> {

    private static List<String> uids = new ArrayList<>();

//    public static RegistRequestHandler regRequestHandler;

    static {
        uids.add("F59D3E873F5B");
        uids.add("F59D3E873F51");
        uids.add("F59D3E873F52");
        uids.add("F59D3E873F53");
        uids.add("F59D3E873F54");
        uids.add("F59D3E873F55");
        uids.add("F59D3E873F56");
        uids.add("F59D3E873F57");
        uids.add("F59D3E873F58");
        uids.add("F59D3E873F59");
        uids.add("F59D3E873F50");
    }

    /*public RegistRequestHandler(){}

    @PostConstruct
    public void init() {
        regRequestHandler = this;
        deviceRegSession = this.deviceRegSession;
    }*/

/*    @Autowired
    private DeviceRegSession deviceRegSession;*/



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DeviceInfo.RegisterReq msg) throws Exception {
        //存在则注册成功 可以改数据库查询
        try {
            DeviceInfo.RegisterResponse.Builder response = DeviceInfo.RegisterResponse.newBuilder();
            boolean regFlag = false;
            if (uids.contains(msg.getUid())) {
                response.setSeq(4);
                // 0-成功 其它失败
                response.setResult(0);
                regFlag = true;
                DeviceRegSession.connect(ctx.channel(), msg.getUid());
            }else {
                response.setResult(1);
            }

            ByteBuf buffer = ctx.alloc().buffer();
            byte[] data = response.build().toByteArray();
            //type = 2
            buffer.writeByte(4);
            buffer.writeBytes(data);
            ctx.writeAndFlush(buffer);

            if (!regFlag){
                ctx.channel().close();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        DeviceRegSession.disconnect(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }



}

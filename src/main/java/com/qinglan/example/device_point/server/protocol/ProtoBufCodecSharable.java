package com.qinglan.example.device_point.server.protocol;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.qinglan.example.device_point.server.msg.ServerLBSInfo;
import com.qinglan.example.device_point.server.msg.DeviceInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ChannelHandler.Sharable
@Slf4j
public class ProtoBufCodecSharable extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    private static final boolean HAS_PARSER;

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf bytes, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
//        out.order(ByteOrder.LITTLE_ENDIAN);
        // 1 字节的指令类型
        // 获取内容的字节数组
        // 2. 长度
//        out.writeBytes(j2c_shortToByte(bytes.length));
        out.writeShortLE(bytes.readableBytes() - 1);
        // 3. 写入内容
        out.writeBytes(bytes);

        outList.add(out);
    }

    private final MessageLite getSerPrototype = ServerLBSInfo.GetServerReq.getDefaultInstance();

    private final MessageLite registerReqPrototype = DeviceInfo.RegisterReq.getDefaultInstance();

    private final MessageLite fallDownPrototype = ServerLBSInfo.ObjectFallDown.getDefaultInstance();

    private final MessageLite objectDataPrototype = ServerLBSInfo.ObjectData.getDefaultInstance();

    private final MessageLite positionDataPrototype = ServerLBSInfo.PositionData.getDefaultInstance();

    private final MessageLite heartDataPrototype = ServerLBSInfo.CommonMessage.getDefaultInstance();

    private final MessageLite commonResponsePrototype = ServerLBSInfo.CommonResponse.getDefaultInstance();

    private final MessageLite setPropResponsePrototype = ServerLBSInfo.SetPropResponse.getDefaultInstance();

    private final MessageLite positionStatusPrototype = ServerLBSInfo.PositionStatusEvent.getDefaultInstance();

    private final MessageLite proPertyItemsPrototype = ServerLBSInfo.ProPertyItems.getDefaultInstance();


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        MessageLite prototype = null;
        short i = msg.readShortLE();
        byte messageType = msg.readByte(); // 0,1,2...

        int length = msg.readableBytes();
        byte[] array;
        int offset;
        if (msg.hasArray()) {
            array = msg.array();
            offset = msg.arrayOffset() + msg.readerIndex();
        } else {
            array = ByteBufUtil.getBytes(msg, msg.readerIndex(), length, false);
            offset = 0;
        }

        if (messageType == 1) { //获取服务器
            prototype = getSerPrototype;
        } else if (messageType == 3) {  //注册
            prototype = registerReqPrototype;
        } else if (messageType == 5) {  //跌倒数据
            prototype = fallDownPrototype;
        } else if (messageType == 13) { //轨迹
            prototype = positionDataPrototype;
        } else if (messageType == 14) { //呼吸 心率
            prototype = objectDataPrototype;
        } else if (messageType == 7) { //事件
            prototype = heartDataPrototype;
        } else if (messageType == 15) {
            prototype = positionStatusPrototype;
        } else if (messageType == 27) {
            prototype = commonResponsePrototype;
        } else if (messageType == 12) {
            prototype = proPertyItemsPrototype;
        }
        else {
            log.error("-------------------------Unknown data---------messageType---{}---------------", messageType);
        }

        try {
            addExecutionChain(array, offset, length, out, prototype);
        } catch (InvalidProtocolBufferException e) {
            log.error("-------------------------decode-erro----------{}---------------", e.getMessage());;
            ctx.channel().close();
        }

//        out.add(prototype.getParserForType().parseFrom(array, offset, length));
    }

    private void addExecutionChain(byte[] array, int offset, int length, List<Object> out, MessageLite prototype) throws InvalidProtocolBufferException {
        if (HAS_PARSER) {
            out.add(prototype.getParserForType().parseFrom(array, offset, length));
        } else {
            out.add(prototype.newBuilderForType().mergeFrom(array, offset, length).build());
        }
    }


    static {
        boolean hasParser = false;

        try {
            MessageLite.class.getDeclaredMethod("getParserForType");
            hasParser = true;
        } catch (Throwable var2) {
        }

        HAS_PARSER = hasParser;
    }

}

package com.qinglan.example.device_point.server.protocol;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.qinglan.example.device_point.server.msg.DeviceInfo;
import com.qinglan.example.device_point.server.msg.ServerLBSInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

    private final PrototypeHandle prototypeHandle = new PrototypeHandle();

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
        MessageLite handle = prototypeHandle.handle(messageType);
        if (handle != null) {
            prototype = handle;
        } else {
            log.error("-------------------------Unknown data---------messageType---{}---------------", messageType);
            return;
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
class PrototypeHandle{
    private Map<String, Function<String, MessageLite>> prototypeMap = new HashMap<>();

    public PrototypeHandle(Map<String, Function<String, MessageLite>> prototypeMap) {
        this.prototypeMap = prototypeMap;
    }

    public PrototypeHandle() {
        this.dispatcherInit();
    }

    public MessageLite handle(byte type) {
        //根据类型operateType去查询执行方法
        String sType = String.valueOf(type);
        Function<String, MessageLite> handle = prototypeMap.get(sType);
        if (handle != null) {
            return handle.apply(sType);
        }
        return null;
    }

    public void dispatcherInit() {
        prototypeMap.put("1", this::handleGetServer);
        prototypeMap.put("3", this::handleRegister);
        prototypeMap.put("5", this::handleFallDown);
        prototypeMap.put("7", this::handleHeartData);
        prototypeMap.put("12", this::handleProPertyItems);
        prototypeMap.put("10", this::handleSetProp);
        prototypeMap.put("13", this::handlePositionData);
        prototypeMap.put("14", this::handleObjectData);
        prototypeMap.put("15", this::handlePositionStatus);
        prototypeMap.put("16", this::handleNumberOfPeople);
        prototypeMap.put("17", this::handleOtaResponse);
        prototypeMap.put("18", this::handleOtaProgress);
        prototypeMap.put("19", this::handlePositionStatistic);
        prototypeMap.put("27", this::handleCommonResponse);
        prototypeMap.put("35", this::handleNotifyMessage);
        prototypeMap.put("51", this::handleStartVoipResponse);
        prototypeMap.put("53", this::handleStopVoipResponse);
    }

    private MessageLite handleStartVoipResponse(String type) {
        return DeviceInfo.StartVoipResponse.getDefaultInstance();
    }

    private MessageLite handleStopVoipResponse(String type) {
        return DeviceInfo.StopVoipResponse.getDefaultInstance();
    }

    private MessageLite handleOtaProgress(String type) {
        return DeviceInfo.OTAProgress.getDefaultInstance();
    }

    private MessageLite handleOtaResponse(String type) {
        return DeviceInfo.OtaResponse.getDefaultInstance();
    }

    private MessageLite handleSetProp(String type) {
        return ServerLBSInfo.SetPropResponse.getDefaultInstance();
    }

    private MessageLite handleNotifyMessage(String type) {
        return ServerLBSInfo.NotifyMessage.getDefaultInstance();
    }

    private MessageLite handleNumberOfPeople(String s) {
        return ServerLBSInfo.NumberOfPeopleData.getDefaultInstance();
    }

    private MessageLite handlePositionStatistic(String type) {
        return ServerLBSInfo.PositionStatisticReport.getDefaultInstance();
    }

    private MessageLite handleCommonResponse(String type) {
        return ServerLBSInfo.CommonResponse.getDefaultInstance();
    }

    private MessageLite handlePositionStatus(String type) {
        return ServerLBSInfo.PositionStatusEvent.getDefaultInstance();
    }

    private MessageLite handleObjectData(String type) {
        return ServerLBSInfo.ObjectData.getDefaultInstance();
    }

    private MessageLite handlePositionData(String type) {
        return ServerLBSInfo.PositionData.getDefaultInstance();
    }

    private MessageLite handleProPertyItems(String type) {
        return ServerLBSInfo.ProPertyItems.getDefaultInstance();
    }

    private MessageLite handleHeartData(String type) {
        return ServerLBSInfo.CommonMessage.getDefaultInstance();
    }

    private MessageLite handleFallDown(String type) {
        return ServerLBSInfo.ObjectFallDown.getDefaultInstance();
    }

    private MessageLite handleGetServer(String type) {
        return ServerLBSInfo.GetServerReq.getDefaultInstance();
    }

    private MessageLite handleRegister(String type) {
        return DeviceInfo.RegisterReq.getDefaultInstance();
    }
}

package com.qinglan.example.device_point.server;

import com.qinglan.example.device_point.server.handle.*;
import com.qinglan.example.device_point.server.protocol.ProcotolFrameDecoder;
import com.qinglan.example.device_point.server.protocol.ProtoBufCodecSharable;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QlIotServer {
    public void startQLServer(int inetPort){
        NioEventLoopGroup boss = new NioEventLoopGroup(2);
        NioEventLoopGroup worker = new NioEventLoopGroup(16);
        ProtoBufCodecSharable MESSAGE_CODEC = new ProtoBufCodecSharable();
        //报文解析器
        GetServerHandler GET_SERVER_REC = new GetServerHandler();
        RegistResponseHandler REGIST_REC = new RegistResponseHandler();
        SetPropHandler SET_PROP_REC = new SetPropHandler();
        ProItemsHandler GET_PROP_REC = new ProItemsHandler();
        CommonResHandle COMMON_REC = new CommonResHandle();
        BreathDateHandler BREATH_MESSAGE_REC = new BreathDateHandler();
        PositionDateHandler POSITION_MESSAGE_REC = new PositionDateHandler();
        PositionEventHandler POSITION_EVENT_REC = new PositionEventHandler();
        PositionStatisticHandler POSITION_STATISTIC_REC = new PositionStatisticHandler();
        FallDownHandler FALL_DOWN_REC = new FallDownHandler();
        NumberOfPeopleHandler NUMBER_PEOPLE_REC = new NumberOfPeopleHandler();
        OtaResponseHandler OTA_RESPONSE_REC = new OtaResponseHandler();
        OtaProgressHandler OTA_PROGRESS_REC = new OtaProgressHandler();
        StartVoipHandler VOIP_START_REC = new StartVoipHandler();
        StopVoipHandler VOIP_STOP_REC = new StopVoipHandler();
        NotifyMessageHandler NOTIFY_MSG_REC = new NotifyMessageHandler();
        HeartMsgHandler HEART_REC = new HeartMsgHandler();
        //日志
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
                    // 5s 内如果没有收到 channel 的数据，会触发一个 IdleState#READER_IDLE 事件
                    ch.pipeline().addLast(new IdleStateHandler(60, 0, 0));
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(GET_SERVER_REC);
                    ch.pipeline().addLast(REGIST_REC);
                    // ChannelDuplexHandler 可以同时作为入站和出站处理器
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        // 用来触发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // 触发了读空闲事件
                            if (event.state() == IdleState.READER_IDLE) {
                                log.info("No data has been read for 60 seconds");
                                ctx.channel().close();
                            }
                        }
                    });
                    ch.pipeline().addLast(HEART_REC);
                    ch.pipeline().addLast(BREATH_MESSAGE_REC);
                    ch.pipeline().addLast(POSITION_MESSAGE_REC);
                    ch.pipeline().addLast(POSITION_EVENT_REC);
                    ch.pipeline().addLast(POSITION_STATISTIC_REC);
                    ch.pipeline().addLast(FALL_DOWN_REC);
                    ch.pipeline().addLast(SET_PROP_REC);
                    ch.pipeline().addLast(GET_PROP_REC);
                    ch.pipeline().addLast(NUMBER_PEOPLE_REC);
                    ch.pipeline().addLast(NOTIFY_MSG_REC);
                    ch.pipeline().addLast(OTA_RESPONSE_REC);
                    ch.pipeline().addLast(OTA_PROGRESS_REC);
                    ch.pipeline().addLast(VOIP_START_REC);
                    ch.pipeline().addLast(VOIP_STOP_REC);
                    ch.pipeline().addLast(COMMON_REC);
                }
            });
            Channel channel = serverBootstrap.bind(inetPort).sync().channel();
            log.info("----------------start----qlServer----port:{}--------", inetPort);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}

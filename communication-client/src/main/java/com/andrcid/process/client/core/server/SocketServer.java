package com.andrcid.process.client.core.server;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.andrcid.process.client.core.config.SocketClientConfig;
import com.andrcid.process.client.core.context.Global;
import com.andrcid.process.client.core.message.IMessageProcessor;
import com.tigerjoys.communication.protocol.ProtocolConst;
import com.tigerjoys.communication.protocol.enums.MessageFromType;
import com.tigerjoys.communication.protocol.message.MessageDecoder;
import com.tigerjoys.communication.protocol.message.MessageEncoder;
import com.tigerjoys.communication.protocol.utility.ProtocolHelper;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SocketServer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);
	
	private EventLoopGroup eventLoopGroup;
	
	private SocketClientConfig config;
	
	private Bootstrap bootstrap;
	
	private volatile boolean reConnection = false;
	
	public SocketServer() {
		this.eventLoopGroup = new NioEventLoopGroup();
		this.config = Global.getInstance().getClientConfig();
	}
	
	public void start() throws InterruptedException {
		LOGGER.info("----------------start socket server--------------------");
//		Log.d("netty msg:", "----------------start socket server--------------------");
		initServer();
		LOGGER.info("----------------socket server start finish-------------");
//		Log.d("netty msg:", "----------------socket server start finish-------------");
	}
	
	private void initServer() throws InterruptedException {
		bootstrap = new Bootstrap(); // ??????????????????
        bootstrap.group(this.eventLoopGroup);// ???????????????
        bootstrap.channel(NioSocketChannel.class);// ?????????????????????NioServerSocketChannel????????????????????????OIO???????????????OioServerSocketChannel
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);//??????Nagle??????
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);//????????????
        bootstrap.option(ChannelOption.SO_TIMEOUT, 5000);
        bootstrap.remoteAddress(new InetSocketAddress(config.getServerHost(), config.getServerPort()));// ??????????????????
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
            	ChannelPipeline pipeline = ch.pipeline();
            	
            	IMessageProcessor messageProcessor = new SocketMessageProcessor();
            	
            	pipeline.addLast(new DelimiterBasedFrameDecoder(ProtocolConst.MAX_FRAME_LENGTH, true , Unpooled.copiedBuffer(ProtocolConst.P_END_TAG)));
            	pipeline.addLast(new MessageDecoder());
            	pipeline.addLast(new MessageEncoder());
            	pipeline.addLast(new ConnectionWatchdog(SocketServer.this));
            	pipeline.addLast(new IdleStateHandler(0, config.getWriterIdleTime(), 0, TimeUnit.SECONDS));//??????30s?????????????????????userEventTriggered????????????????????????IdleState???????????????WRITER_IDLE
            	pipeline.addLast(new ClientHeartBeatHandler());//??????userEventTriggered???????????????state???WRITER_IDLE?????????????????????????????????sever????????????server???????????????
            	pipeline.addLast(new ClientMessageHandler(messageProcessor));
            }
        });
        
        ChannelFuture f = bootstrap.connect();
        f.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				LOGGER.info("connection server status : " + future.isDone()+",success:" + future.isSuccess());
//				Log.d("netty msg:", "connection server status : " + future.isDone()+",success:" + future.isSuccess());
				reConnection = false;
				if (future.isDone() && future.isSuccess()) {
					LOGGER.info("---- connection success-------");
//					Log.d("netty msg:", "---- connection success-------");
					//??????????????????????????????????????????????????????
					future.channel().writeAndFlush(ProtocolHelper.createHeartBeatMessage(MessageFromType.CLIENT , config.getDeviceId() , true));
				} else {
					//???????????????try????????????????????????????????????
					try {
						future.cause().printStackTrace();
					}catch (Exception e){
						Log.d("netty msg:", "future.cause.printStackTrace:"+e);
					}
					try {
						f.channel().close();
					} catch (Exception e) {
						LOGGER.info("=================");
//						Log.d("netty msg:", "================="+e);
					}
					LOGGER.info("---- connection fail," + config.getReconnectTime() + " second later reconnection ---------host="+config.getServerHost()+",port=" + config.getServerPort());
//					Log.d("netty msg:", "---- connection fail," + config.getReconnectTime() + " second later reconnection ---------host="+config.getServerHost()+",port=" + config.getServerPort());
					reconnection();
				}
			}
        	
        });
	}
	
	/**
	 * ??????????????????????????????
	 * @param  - ??????
	 * @throws InterruptedException
	 */
	public synchronized void reconnection() throws InterruptedException {
		if(reConnection) {
			return;
		}
		reConnection = true;
		this.eventLoopGroup.schedule(new Runnable() {
            @Override
            public void run() {
            	try {
            		initServer();
				} catch (InterruptedException e) {
					LOGGER.error(e.getMessage() , e);
//					Log.d("netty msg:", "error"+e.getMessage());
				}
            }

        }, config.getReconnectTime(), TimeUnit.SECONDS);
	}

}

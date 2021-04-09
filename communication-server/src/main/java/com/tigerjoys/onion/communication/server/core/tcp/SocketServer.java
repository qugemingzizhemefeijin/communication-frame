package com.tigerjoys.onion.communication.server.core.tcp;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tigerjoys.communication.protocol.ProtocolConst;
import com.tigerjoys.communication.protocol.message.MessageDecoder;
import com.tigerjoys.communication.protocol.message.MessageEncoder;
import com.tigerjoys.onion.communication.server.core.config.ServerConstant;
import com.tigerjoys.onion.communication.server.core.context.Global;
import com.tigerjoys.onion.communication.server.core.context.ServerType;
import com.tigerjoys.onion.communication.server.core.server.IMessageProcessor;
import com.tigerjoys.onion.communication.server.core.server.IServer;
import com.tigerjoys.onion.communication.server.core.session.SocketMessageProcessor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.ImmediateEventExecutor;

/**
 * 服务端
 * @author chengang
 *
 */
public class SocketServer implements IServer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);
	
	static final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
	
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	
	public SocketServer() {
		this.bossGroup = new NioEventLoopGroup();
		this.workerGroup = new NioEventLoopGroup();
	}

	@Override
	public void start() throws Exception {
		LOGGER.info("----------------start socket server--------------------");
		initServer();
		LOGGER.info("----------------socket server start finish-------------");
	}
	
	/**
	 * 初始化服务器
	 */
	private void initServer() {
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)//指定NIO的模式
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							IMessageProcessor messageProcessor = new SocketMessageProcessor();
							
							ChannelPipeline pipeline = ch.pipeline();
							
							//处理心跳
							pipeline.addLast(new IdleStateHandler(Global.getInstance().getServiceConfig().getReaderIdleTime(), 0, 0, TimeUnit.SECONDS));
							pipeline.addLast(new DelimiterBasedFrameDecoder(ProtocolConst.MAX_FRAME_LENGTH, true , Unpooled.copiedBuffer(ProtocolConst.P_END_TAG)));
							pipeline.addLast(new MessageDecoder());
							pipeline.addLast(new MessageEncoder());
							pipeline.addLast(new ServerHeartBeatHandler(messageProcessor));
							pipeline.addLast(new SocketServerHandler(messageProcessor));
						}
						
					})
					.option(ChannelOption.SO_BACKLOG, ServerConstant.DEFAULT_SOCKET_BACKLOG)
					.childOption(ChannelOption.TCP_NODELAY, ServerConstant.DEFAULT_SOCKET_NODELAY)//禁用Nagle算法
					.childOption(ChannelOption.SO_RCVBUF, ServerConstant.DEFAULT_SOCKET_RCVBUF)//socket server receive buffer size:1024 * 64
					.childOption(ChannelOption.SO_SNDBUF, ServerConstant.DEFAULT_SOCKET_SNDBUF)//socket server send buffer size:1024 * 64
					.childOption(ChannelOption.SO_KEEPALIVE, ServerConstant.DEFAULT_SOCKET_KEEPALIVE);

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(Global.getInstance().getServiceConfig().getPort()).syncUninterruptibly();
			
			Channel channel = f.channel();
			channelGroup.add(channel);
			
			// Wait until the server socket is closed.
			//channel.closeFuture().sync();
		} catch (Exception e) {
			LOGGER.error(e.getMessage() , e);
		}/* finally {
			try {
				stop();
			} catch (Exception e) {
				LOGGER.error(e.getMessage() , e);
			}
		}*/
	}

	@Override
	public void stop() throws Exception {
		//停止工作线程
		AsyncMessageInvoker.getInstance().stop();
		
		//停止所有通道
		LOGGER.info("----------------------------------------------------");
		LOGGER.info("-- socket server closing...");
		LOGGER.info("-- channels count : " + channelGroup.size());
		
		ChannelGroupFuture future = channelGroup.close();
		LOGGER.info("-- closing all channels...");
		future.awaitUninterruptibly();
		LOGGER.info("-- closed all channels...");
		
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		LOGGER.info("SOCKET SERVER is closed.");
	}

	@Override
	public ServerType getServerType() {
		return ServerType.TCP;
	}

}

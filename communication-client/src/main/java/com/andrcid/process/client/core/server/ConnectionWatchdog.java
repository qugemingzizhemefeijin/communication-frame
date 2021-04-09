package com.andrcid.process.client.core.server;

import java.util.List;
import java.util.concurrent.TimeUnit;


import com.andrcid.process.client.core.context.CommunicationListener;
import com.andrcid.process.client.core.context.Global;
import com.andrcid.process.client.core.message.MessageWaitProcessor;
import com.andrcid.process.client.core.session.ISession;
import com.andrcid.process.client.core.session.ISessionConnectionListener;
import com.andrcid.process.client.core.session.SessionConnectionEvent;
import com.andrcid.process.client.core.session.SessionFactory;
import com.tigerjoys.communication.protocol.Protocol;
import com.tigerjoys.communication.protocol.enums.MessageType;
import com.tigerjoys.communication.protocol.message.HeartBeatMessage;
import com.tigerjoys.communication.protocol.message.IMessage;
import com.tigerjoys.communication.protocol.utility.FastJsonHelper;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;
import android.util.Log;

@ChannelHandler.Sharable
public class ConnectionWatchdog extends ChannelInboundHandlerAdapter {

//	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionWatchdog.class);

	private SocketServer server;

	private volatile long refreshTime = 0L;
	private long idleHeartTimeout;
	private ScheduledFuture<?> scheduledFuture = null;
	
	public ConnectionWatchdog(SocketServer server) {
		this.server = server;
		this.idleHeartTimeout = Global.getInstance().getClientConfig().getIdleHeartTimeout()*1000L;
	}

	/**
	 * 链路链接之后执行
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		//创建Session
		ISession session = SessionFactory.buildSession(ctx , Global.getInstance().getClientConfig().getDeviceId());
		refreshTime = System.currentTimeMillis();
		//检查心跳连接
		scheduledFuture = ctx.channel().eventLoop().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (System.currentTimeMillis() - refreshTime > idleHeartTimeout) {
					session.close();
					//取消定时器
					scheduledFuture.cancel(false);
					//重新连接服务器
					try {
						server.reconnection();
					} catch (InterruptedException e) {
//						LOGGER.error(e.getMessage() , e);
						Log.d("netty msg:", "error"+e.getMessage());

					}
//					LOGGER.info("Heartbeat Failure , close channel and reconnection ---------");
					Log.d("netty msg:", "Heartbeat Failure , close channel and reconnection ---------");
				}
			}
		}, 5L, 5L, TimeUnit.SECONDS);
		
//		LOGGER.info("Connects with {}.", ctx.channel());
		Log.d("netty msg:", "Connects with {"+ ctx.channel()+"}.");
		ctx.fireChannelActive();
	}

	/**
	 * 因为链路断掉之后，会触发channelInActive方法，进行重连。
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		LOGGER.warn("Disconnects with {}", ctx.channel());
		Log.d("netty msg:", "Disconnects with  "+ ctx.channel()+".");
		//如果当前Session没有关闭，则调用关闭功能
		ISession session = SessionFactory.getSession();
		if(session != null && !session.isClosed()) {
			session.close();
		}
		//取消定时器
		if(scheduledFuture != null) {
			try {
				scheduledFuture.cancel(false);
			} catch (Exception e) {
//				LOGGER.error(e.getMessage() , e);
				Log.d("netty msg:", "error"+e.getMessage());
			}
		}
		
		//重新连接服务器
		server.reconnection();
	}

	/**
	 * 心跳检测 ，将服务端返回的数据进行读取
	 * @param ctx - ChannelHandlerContext
	 * @param msg - 消息
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg != null) {
			if(msg instanceof Protocol) {
				Protocol p = (Protocol)msg;
				IMessage message = (IMessage)p.getEntity();
				
				if(message.messageType() == MessageType.HeartBeat) {
					refreshTime = System.currentTimeMillis();
					
					HeartBeatMessage m = (HeartBeatMessage)message;
					if(m.getFstConn() == 1) {//如果是第一次心跳连接，则需要执行业务层的监听器
//						LOGGER.info("current session first connection server!");
						Log.d("netty msg:", "current session first connection server!");

						List<ISessionConnectionListener> connectionListenerList = CommunicationListener.getSessionConnectionListener();
						if(connectionListenerList != null && !connectionListenerList.isEmpty()) {
							//创建Event对象
							SessionConnectionEvent event = new SessionConnectionEvent(message , SessionFactory.getSession());
							//异步执行Listener
							ctx.channel().eventLoop().execute(new Runnable() {
								
								@Override
								public void run() {
									for(ISessionConnectionListener listener : connectionListenerList) {
										if(!listener.onConnection(event)) {
											break;
										}
									}
								}
							});
						}
					}
					
//					LOGGER.info("receive heartBeat response from server {},time is {} , wait is {}",FastJsonHelper.toJson(message),refreshTime , MessageWaitProcessor.getWindowDataSize());
					Log.d("netty msg:", "receive heartBeat response from server "+FastJsonHelper.toJson(message)+",time is "+refreshTime+" , wait is "+MessageWaitProcessor.getWindowDataSize()+"");
					return;
				}
				
				ctx.fireChannelRead(p);
			} else {
//				LOGGER.warn("receive server message unknown type : " + msg.getClass().getName());
				Log.d("netty msg:", "receive server message unknown type :"+msg.getClass().getName()+"");

			}
		}
	}

}

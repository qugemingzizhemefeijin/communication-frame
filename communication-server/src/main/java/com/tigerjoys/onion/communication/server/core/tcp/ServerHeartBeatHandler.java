package com.tigerjoys.onion.communication.server.core.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tigerjoys.communication.protocol.Protocol;
import com.tigerjoys.communication.protocol.enums.MessageType;
import com.tigerjoys.communication.protocol.message.HeartBeatMessage;
import com.tigerjoys.communication.protocol.message.IMessage;
import com.tigerjoys.communication.protocol.utility.FastJsonHelper;
import com.tigerjoys.onion.communication.server.core.context.BeatContext;
import com.tigerjoys.onion.communication.server.core.context.Global;
import com.tigerjoys.onion.communication.server.core.context.ServerType;
import com.tigerjoys.onion.communication.server.core.filter.IFilter;
import com.tigerjoys.onion.communication.server.core.server.IMessageProcessor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHeartBeatHandler extends ChannelInboundHandlerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(ServerHeartBeatHandler.class);
	
	private IMessageProcessor messageProcessor;
	
	public ServerHeartBeatHandler(IMessageProcessor messageProcessor) {
		this.messageProcessor = messageProcessor;
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleState state = ((IdleStateEvent) evt).state();
			if (state == IdleState.READER_IDLE) {
				LOGGER.warn("Client is idle, close it.");
				messageProcessor.processReceiveDisconnect(ctx);
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("RemoteAddress : " + ctx.channel().remoteAddress() + " active !");
		
		BeatContext context = BeatContext.wrapNoSessionContext(ctx, ServerType.TCP);
		//连接进来后，查看是否在禁用IP中
        for(IFilter f : Global.getInstance().getConnectionFilterList()) {
        	f.filter(context);
        }
		super.channelActive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg != null) {
			Protocol p = (Protocol)msg;
			if(p.getMessageType() == MessageType.HeartBeat) {
				IMessage message = (IMessage)p.getEntity();
				
				LOGGER.info("read heartBeat from client {}", FastJsonHelper.toJson(message));
				
				//接收到心跳消息的处理逻辑
				messageProcessor.processReceiveHeartMessage(ctx, (HeartBeatMessage)message);
				return;
			}
			ctx.fireChannelRead(p);
		}
	}

}

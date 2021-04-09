package com.andrcid.process.client.core.session;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andrcid.process.client.core.context.Global;
import com.andrcid.process.client.core.hotkey.IAsyncCallBackListener;
import com.andrcid.process.client.core.hotkey.IReturnHandlerListener;
import com.andrcid.process.client.core.message.AsyncMessageProcessor;
import com.andrcid.process.client.core.message.AutoReceiveEvent;
import com.andrcid.process.client.core.message.MessageIdFactory;
import com.andrcid.process.client.core.message.MessageWaitProcessor;
import com.andrcid.process.client.core.message.WindowData;
import com.tigerjoys.communication.protocol.Protocol;
import com.tigerjoys.communication.protocol.enums.DeviceStatus;
import com.tigerjoys.communication.protocol.enums.MessageFromType;
import com.tigerjoys.communication.protocol.exception.TimeoutException;
import com.tigerjoys.communication.protocol.message.IMessage;
import com.tigerjoys.communication.protocol.utility.ProtocolHelper;

import io.netty.channel.ChannelHandlerContext;

public class DefaultSession implements ISession {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSession.class);

	private volatile boolean closed = false;
	
	private ChannelHandlerContext ctx;
	
	private String deviceId;
	
	/**
	 * 设备状态
	 */
	private DeviceStatus status;
	
	private long requestTimeout;
	
	public DefaultSession(ChannelHandlerContext ctx , String deviceId) {
		this.ctx = ctx;
		this.deviceId = deviceId;
		this.requestTimeout = Global.getInstance().getClientConfig().getRequestTimeout();
		this.status = DeviceStatus.IDLE;//默认空闲状态
	}

	@Override
	public IMessage request(Protocol p) throws IOException, TimeoutException, Exception {
		int messageId = p.getMessageId();
		if(messageId <= 0) {
			messageId = MessageIdFactory.createMessageId();
			p.setMessageId(messageId);
		}
		
		try {
			MessageWaitProcessor.registerEvent(messageId);
			ctx.writeAndFlush(p);
		} catch (Throwable ex) {
			LOGGER.error("client get socket Exception", ex);
			//抛出异常了则移除定时器
			MessageWaitProcessor.unregisterEvent(messageId);
			throw ex;
		}
		
		WindowData wd = MessageWaitProcessor.getWindowData(messageId);
		if(wd != null) {
			try {
				AutoReceiveEvent event = wd.getEvent();
				if (!event.waitOne(this.requestTimeout)) {
					StringBuilder buf = new StringBuilder();
					buf.append("ServerIP:[").append(Global.getInstance().getClientConfig().getServerHost());
					buf.append("],ServerPort:[").append(Global.getInstance().getClientConfig().getServerHost());
					buf.append("],Receive data timeout or error!timeout:").append(this.requestTimeout).append("s");
					
					throw new TimeoutException(buf.toString());
				}
				return (IMessage)wd.getProtocol().getEntity();
			} finally {//移除定时
				MessageWaitProcessor.unregisterEvent(messageId);
			}
		}
		
		return null;
	}
	
	@Override
	public void requestAsync(Protocol p , IReturnHandlerListener returnHandler , IAsyncCallBackListener[] callBacks , boolean future) throws IOException, TimeoutException, Exception {
		int messageId = p.getMessageId();
		if(messageId <= 0) {
			messageId = MessageIdFactory.createMessageId();
			p.setMessageId(messageId);
		}
		
		try {
			WindowData wd = new WindowData(messageId, p , returnHandler , callBacks , future);
			MessageWaitProcessor.registerEvent(messageId , wd);
			//添加到异步回调执行队列中
			AsyncMessageProcessor.offerSendQueue(wd);
		} catch (Throwable ex) {
			LOGGER.error("client get socket Exception", ex);
			//抛出异常了则移除定时器
			MessageWaitProcessor.unregisterEvent(messageId);
			throw ex;
		}
	}

	@Override
	public void receive(Protocol p) throws IOException, TimeoutException, Exception {
		// TODO Auto-generated method stub
	}
	
	@Override
	public DeviceStatus getDeviceStatus() {
		return status;
	}
	
	@Override
	public void setDeviceStatus(DeviceStatus status) {
		this.status = status;
	}
	
	@Override
	public void setDeviceStatusNotifyServer(DeviceStatus status) throws InterruptedException , SessionException {
		if (isClosed()) {
			throw new SessionException("session is null or closed!");
		}
		
		LOGGER.info("notify server status message - deviceId: " +deviceId+ ",current status "+ this.status.name() +",change status : " + status.name());
		
		//此处需要发送状态变更到客户端
		ctx.writeAndFlush(ProtocolHelper.createStatusMessage(MessageFromType.CLIENT, deviceId , status)).sync();
		//修改设备状态
		this.status = status;
	}

	@Override
	public void close() {
		this.closed = true;
		this.status = DeviceStatus.DOWN;
		try {
			ctx.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage() , e);
		} finally {
			//从session工厂中移除
			SessionFactory.removeSession();
		}
		
	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public ChannelHandlerContext getCtx() {
		return ctx;
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			if (this.ctx != null && this.ctx.channel().isOpen()) {
				close();
			}
		} catch (Throwable t) {
			LOGGER.error("Device " + deviceId + " Session Release Error!:", t);
		} finally {
			super.finalize();
		}
	}

}

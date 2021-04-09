package com.andrcid.process.client.core.server;

import android.util.Log;

import com.andrcid.process.client.core.message.AsyncMessageProcessor;
import com.andrcid.process.client.core.message.IMessageProcessor;
import com.andrcid.process.client.core.message.MessageWaitProcessor;
import com.andrcid.process.client.core.message.WindowData;
import com.andrcid.process.client.core.session.ISession;
import com.andrcid.process.client.core.session.SessionFactory;
import com.tigerjoys.communication.protocol.Protocol;
import com.tigerjoys.communication.protocol.enums.DeviceStatus;
import com.tigerjoys.communication.protocol.message.StatusMessage;
import com.tigerjoys.communication.protocol.utility.FastJsonHelper;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息处理中心
 * @author chengang
 *
 */
public class SocketMessageProcessor implements IMessageProcessor {
	
//	private static final Logger LOGGER = LoggerFactory.getLogger(SocketMessageProcessor.class);

	@Override
	public void processReceiveRequestMessage(ChannelHandlerContext ctx, Protocol p) {
//		LOGGER.info("Send Client to Server RequestMessage : " + FastJsonHelper.toJson(p.getEntity()));
		Log.d("netty msg:", "Send Client to Server RequestMessage : " + FastJsonHelper.toJson(p.getEntity()));
	}

	@Override
	public void processReceiveResponseMessage(ChannelHandlerContext ctx, Protocol p) {
		//解锁线程
		WindowData wd = MessageWaitProcessor.getWindowData(p.getMessageId());
		if(wd != null) {
//			LOGGER.info("Receive from Server ResponseMessage : " + FastJsonHelper.toJson(p.getEntity()));
			Log.d("netty msg:", "Receive from Server ResponseMessage : " + FastJsonHelper.toJson(p.getEntity()));
			if(wd.isAsync()) {
				wd.setProtocol(p);
				//执行异步回调逻辑
				AsyncMessageProcessor.callback(wd);
			} else {
				wd.setProtocol(p);
				wd.getEvent().set();
			}
		} else {
//			LOGGER.warn("Receive from Server ResponseMessage not in WAIT_WINDOWS : " + FastJsonHelper.toJson(p.getEntity()));
			Log.d("netty msg:", "Receive from Server ResponseMessage not in WAIT_WINDOWS : " + FastJsonHelper.toJson(p.getEntity()));
		}
	}

	@Override
	public void processReceiveExceptionMessage(ChannelHandlerContext ctx, Protocol p) {
		//解锁线程
		WindowData wd = MessageWaitProcessor.getWindowData(p.getMessageId());
		if(wd != null) {
//			LOGGER.info("Receive from Server ExceptionMessage : " + FastJsonHelper.toJson(p.getEntity()));
			Log.d("netty msg:", "Receive from Server ExceptionMessage : " + FastJsonHelper.toJson(p.getEntity()));
			if(wd.isAsync()) {
				wd.setProtocol(p);
				//执行异步回调逻辑
				AsyncMessageProcessor.callback(wd);
			} else {
				wd.setProtocol(p);
				wd.getEvent().set();
			}
		} else {
//			LOGGER.info("Receive from Server ExceptionMessage not in WAIT_WINDOWS : " + FastJsonHelper.toJson(p.getEntity()));
			Log.d("netty msg:", "Receive from Server ExceptionMessage not in WAIT_WINDOWS : " + FastJsonHelper.toJson(p.getEntity()));
		}
	}
	
	@Override
	public void processReceiveStatusMessage(ChannelHandlerContext ctx, StatusMessage message) {
		ISession session = SessionFactory.getSession();
		
		if (session == null || session.isClosed()) {
//			LOGGER.warn("status message's session is null or closed!");
			Log.d("netty msg:", "status message's session is null or closed!");
			return;
		}
		
		DeviceStatus status = DeviceStatus.getDeviceState(message.getStatus());
		
//		LOGGER.info("from server message - deviceId: " +message.getDeviceId()+ ",current status "+ session.getDeviceStatus().name() +",change status : " + status.name());
		Log.d("netty msg:", "from server message - deviceId: " +message.getDeviceId()+ ",current status "+ session.getDeviceStatus().name() +",change status : " + status.name());

		//修改设备状态
		session.setDeviceStatus(status);
	}

}

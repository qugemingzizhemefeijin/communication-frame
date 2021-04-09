package com.tigerjoys.onion.communication.server.core.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tigerjoys.communication.protocol.enums.DeviceStatus;
import com.tigerjoys.communication.protocol.enums.MessageFromType;
import com.tigerjoys.communication.protocol.utility.ProtocolHelper;

import io.netty.channel.ChannelHandlerContext;

/**
 * session
 * @author chengang
 *
 */
public class DefaultSession implements Session {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSession.class);
	
	/**
	 * Channel通道
	 */
	private final ChannelHandlerContext ctx;
	
	/**
	 * 设备ID
	 */
	private final String deviceId;
	
	/**
	 * 设备状态
	 */
	private DeviceStatus status;
	
	/**
	 * session是否关闭
	 */
	private volatile boolean closed = false;

	public DefaultSession(ChannelHandlerContext ctx , String deviceId) {
		this.ctx = ctx;
		this.deviceId = deviceId;
		this.status = DeviceStatus.IDLE;//默认空闲状态
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public String getDeviceId() {
		return deviceId;
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
	public void setDeviceStatusNotifyClient(DeviceStatus status) throws InterruptedException , SessionException {
		if(isClosed()) {
			throw new SessionException("deviceId:"+deviceId+" is closed!");
		}
		LOGGER.info("notify client status message -  - deviceId: " +this.deviceId+ ",current status "+ this.status.name() +",change status : " + status.name());
		
		//此处需要发送状态变更到客户端
		ctx.writeAndFlush(ProtocolHelper.createStatusMessage(MessageFromType.SERVER, this.deviceId , status)).sync();
		//修改设备状态
		this.status = status;
	}

	public void close() {
		closed = true;
		this.status = DeviceStatus.DOWN;
		try {
			ctx.close();
		} finally {
			SessionManager.getInstance().removeSession(this);
		}
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

}

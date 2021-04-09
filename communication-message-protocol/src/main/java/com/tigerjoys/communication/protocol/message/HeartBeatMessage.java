package com.tigerjoys.communication.protocol.message;

import com.tigerjoys.communication.protocol.enums.MessageType;

public class HeartBeatMessage extends DefaultMessage {
	
	/**
	 * 是否第一次请求,1是，0否
	 */
	private int fstConn;
	
	public HeartBeatMessage() {
		
	}
	
	public HeartBeatMessage(String deviceId) {
		this.deviceId = deviceId;
	}
	
	@Override
	public MessageType messageType() {
		return MessageType.HeartBeat;
	}

	public int getFstConn() {
		return fstConn;
	}

	public void setFstConn(int fstConn) {
		this.fstConn = fstConn;
	}

}

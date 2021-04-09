package com.tigerjoys.communication.protocol.message;

import com.tigerjoys.communication.protocol.enums.MessageType;

public interface IMessage {
	
	/**
	 * 消息的时间
	 */
	public long getMessageTime();
	
	/**
	 * 获得设备ID
	 * @return String
	 */
	public String getDeviceId();
	
	/**
	 * 消息类型
	 * @return MessageType
	 */
	public MessageType messageType();

}

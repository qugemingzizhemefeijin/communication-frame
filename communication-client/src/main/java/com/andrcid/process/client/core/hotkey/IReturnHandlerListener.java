package com.andrcid.process.client.core.hotkey;

import com.tigerjoys.communication.protocol.message.IMessage;

/**
 * 返回消息处理
 * @author chengang
 *
 */
public interface IReturnHandlerListener {
	
	public Object handler(IMessage message) throws Exception;

}

package com.tigerjoys.onion.communication.server.core.hotkey;

import com.tigerjoys.communication.protocol.exception.ServiceFrameException;
import com.tigerjoys.onion.communication.server.core.context.BeatContext;

/**
 * 代理实现类
 * @author chengang
 *
 */
public interface IProxyStub {
	
	/**
	 * 调用具体的代理方法
	 * @param context - BeatContext
	 * @throws ServiceFrameException
	 */
	public void invoke(BeatContext context) throws ServiceFrameException;

}

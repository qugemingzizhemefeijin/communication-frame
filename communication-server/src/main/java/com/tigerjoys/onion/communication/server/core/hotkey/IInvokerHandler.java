package com.tigerjoys.onion.communication.server.core.hotkey;

import com.tigerjoys.onion.communication.server.core.context.BeatContext;

public interface IInvokerHandler {
	
	/**
	 * 调用方法
	 * @param context - BeatContext
	 * @throws Exception
	 */
	public void invoke(BeatContext context) throws Exception;

}

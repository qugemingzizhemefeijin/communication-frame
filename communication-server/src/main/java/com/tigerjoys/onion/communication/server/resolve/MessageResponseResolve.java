package com.tigerjoys.onion.communication.server.resolve;

import com.tigerjoys.communication.protocol.message.ResponseMessage;
import com.tigerjoys.onion.communication.server.core.context.BeatContext;

public class MessageResponseResolve implements IResolve<ResponseMessage> {
	
	@Override
	public Class<ResponseMessage> getResolveClass() {
		return ResponseMessage.class;
	}

	@Override
	public ResponseMessage resolve(BeatContext context , String paramName) {
		return context.getResponse();
	}

}

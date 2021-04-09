package com.tigerjoys.onion.communication.server.resolve;

import com.tigerjoys.communication.protocol.message.RequestMessage;
import com.tigerjoys.onion.communication.server.core.context.BeatContext;

public class MessageRequestResolve implements IResolve<RequestMessage> {
	
	@Override
	public Class<RequestMessage> getResolveClass() {
		return RequestMessage.class;
	}

	@Override
	public RequestMessage resolve(BeatContext context , String paramName) {
		return context.getRequest();
	}

}

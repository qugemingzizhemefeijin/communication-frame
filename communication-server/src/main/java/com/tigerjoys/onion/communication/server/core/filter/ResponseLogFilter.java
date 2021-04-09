package com.tigerjoys.onion.communication.server.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tigerjoys.communication.protocol.message.RequestMessage;
import com.tigerjoys.communication.protocol.message.ResponseMessage;
import com.tigerjoys.onion.communication.server.core.context.BeatContext;

public class ResponseLogFilter implements IFilter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseLogFilter.class);

	@Override
	public void filter(BeatContext context) throws Exception {
		if(!context.isResponseFilter()) {
			return;
		}
		
		RequestMessage request = context.getRequest();
		ResponseMessage response = context.getResponse();
		
		StringBuilder buf = new StringBuilder();
		buf.append("response ---- ").append(",");
		buf.append("remoteIP:").append(context.getRemoteIP()).append(",");
		buf.append("localIP:").append(context.getLocalIP()).append(",");
		buf.append("Server.mapping:" + context.getMapping()).append(",");
		buf.append("Server.DeviceId:" + context.getDeviceId()).append(",");
		
		if(request != null) {
			buf.append("Server.Body:").append(request.getBody()).append(",");
		}
		if(response != null) {
			buf.append("Server.Response:").append(response.getBody()).append(",");
		}
		
		LOGGER.info(buf.toString());
	}
	
	@Override
	public FilterType getFilterType() {
		return FilterType.Response;
	}

}

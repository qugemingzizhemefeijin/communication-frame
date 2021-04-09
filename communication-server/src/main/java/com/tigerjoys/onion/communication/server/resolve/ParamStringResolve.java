package com.tigerjoys.onion.communication.server.resolve;

import com.tigerjoys.onion.communication.server.core.context.BeatContext;

public class ParamStringResolve implements IResolve<String> {

	@Override
	public Class<String> getResolveClass() {
		return String.class;
	}

	@Override
	public String resolve(BeatContext context , String paramName) {
		return context.getUrlParams().get(paramName);
	}

}

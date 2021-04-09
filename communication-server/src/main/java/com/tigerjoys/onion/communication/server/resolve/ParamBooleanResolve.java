package com.tigerjoys.onion.communication.server.resolve;

import com.tigerjoys.onion.communication.server.core.context.BeatContext;

public class ParamBooleanResolve implements IResolve<Boolean> {

	@Override
	public Class<Boolean> getResolveClass() {
		return Boolean.class;
	}

	@Override
	public Boolean resolve(BeatContext context, String paramName) {
		return Boolean.valueOf(Boolean.parseBoolean(context.getUrlParams().get(paramName)));
	}

}

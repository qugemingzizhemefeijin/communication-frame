package com.tigerjoys.onion.communication.server.resolve;

import com.tigerjoys.onion.communication.server.core.context.BeatContext;

public class ParamShortResolve implements IResolve<Short> {

	@Override
	public Class<Short> getResolveClass() {
		return Short.class;
	}

	@Override
	public Short resolve(BeatContext context, String paramName) {
		return Short.valueOf(Short.parseShort(context.getUrlParams().get(paramName)));
	}

}

package com.tigerjoys.onion.communication.server.resolve;

import com.tigerjoys.onion.communication.server.core.context.BeatContext;

public class ParamIntegerResolve implements IResolve<Integer> {

	@Override
	public Class<Integer> getResolveClass() {
		return Integer.class;
	}

	@Override
	public Integer resolve(BeatContext context, String paramName) {
		return Integer.valueOf(Integer.parseInt(context.getUrlParams().get(paramName)));
	}

}

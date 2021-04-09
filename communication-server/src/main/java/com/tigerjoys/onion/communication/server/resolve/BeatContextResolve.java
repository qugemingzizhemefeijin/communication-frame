package com.tigerjoys.onion.communication.server.resolve;

import com.tigerjoys.onion.communication.server.core.context.BeatContext;

public class BeatContextResolve implements IResolve<BeatContext> {

	@Override
	public Class<BeatContext> getResolveClass() {
		return BeatContext.class;
	}

	@Override
	public BeatContext resolve(BeatContext context , String paramName) {
		return context;
	}

}

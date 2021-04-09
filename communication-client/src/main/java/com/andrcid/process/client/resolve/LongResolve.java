package com.andrcid.process.client.resolve;

public class LongResolve implements IResolve<Long> {

	@Override
	public Class<Long> getResolveClass() {
		return Long.class;
	}

	@Override
	public Long resolve(String res) {
		return Long.valueOf(res);
	}

}

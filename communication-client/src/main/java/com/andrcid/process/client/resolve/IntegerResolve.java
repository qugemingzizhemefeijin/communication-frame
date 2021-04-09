package com.andrcid.process.client.resolve;

public class IntegerResolve implements IResolve<Integer> {

	@Override
	public Class<Integer> getResolveClass() {
		return Integer.class;
	}

	@Override
	public Integer resolve(String res) {
		return Integer.valueOf(res);
	}

}

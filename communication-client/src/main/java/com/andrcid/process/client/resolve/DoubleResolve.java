package com.andrcid.process.client.resolve;

public class DoubleResolve implements IResolve<Double> {

	@Override
	public Class<Double> getResolveClass() {
		return Double.class;
	}

	@Override
	public Double resolve(String res) {
		return Double.valueOf(res);
	}

}

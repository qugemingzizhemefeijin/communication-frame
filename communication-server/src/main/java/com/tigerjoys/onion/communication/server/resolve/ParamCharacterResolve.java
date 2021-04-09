package com.tigerjoys.onion.communication.server.resolve;

import com.tigerjoys.onion.communication.server.core.context.BeatContext;

public class ParamCharacterResolve implements IResolve<Character> {

	@Override
	public Class<Character> getResolveClass() {
		return Character.class;
	}

	@Override
	public Character resolve(BeatContext context, String paramName) {
		String s = context.getUrlParams().get(paramName);
		if (s == null || s.length() == 0) {
			throw new IllegalArgumentException("Cannot convert empty string to char.");
		}
		
		return Character.valueOf(s.charAt(0));
	}

}

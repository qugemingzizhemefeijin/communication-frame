package com.andrcid.process.client.resolve;

public class CharacterResolve implements IResolve<Character> {

	@Override
	public Class<Character> getResolveClass() {
		return Character.class;
	}

	@Override
	public Character resolve(String res) {
		if (res == null || res.length() == 0) {
			throw new IllegalArgumentException("Cannot convert empty string to char.");
		}
		
		return Character.valueOf(res.charAt(0));
	}

}

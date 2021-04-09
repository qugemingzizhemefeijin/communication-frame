package com.tigerjoys.onion.communication.server.core.hotkey;

public class DefaultObjectBeanFactory implements IObjectBeanFactory {

	@Override
	public Object createBean(Class<?> clazz) throws Exception {
		return clazz.newInstance();
	}

}

package com.tigerjoys.onion.communication.server.core.hotkey;

public interface IObjectBeanFactory {
	
	/**
	 * 注册指定的Class
	 * @param clazz - Class
	 * @return Object
	 * @throws Exception
	 */
	public Object createBean(Class<?> clazz) throws Exception;

}

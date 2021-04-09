package com.tigerjoys.onion.communication.server.spring.hotkey;

import org.springframework.context.ApplicationContext;

import com.tigerjoys.onion.communication.server.core.hotkey.AbstractProxyFactory;
import com.tigerjoys.onion.communication.server.core.hotkey.IObjectBeanFactory;

public class SpringProxyFactory extends AbstractProxyFactory {
	
	private IObjectBeanFactory beanFactory;
	
	public SpringProxyFactory(ApplicationContext context) {
		this.beanFactory = new SpringObjectBeanFactory(context);
	}

	@Override
	public IObjectBeanFactory getBeanFactory() {
		return beanFactory;
	}

}

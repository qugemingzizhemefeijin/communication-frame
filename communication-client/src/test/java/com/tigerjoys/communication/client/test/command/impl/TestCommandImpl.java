package com.tigerjoys.communication.client.test.command.impl;

import com.andrcid.process.client.core.hotkey.ProxyModule;
import com.google.inject.Inject;
import com.tigerjoys.communication.client.test.command.IServiceRemote;
import com.tigerjoys.communication.client.test.command.ITestCommand;

@ProxyModule(from=ITestCommand.class)
public class TestCommandImpl implements ITestCommand {
	
	@Inject
	private IServiceRemote serviceRemote;
	@Override
	public void sayHello() {
		System.out.println(serviceRemote.sayHello("abc" , 101));
	}

	@Override
	public String getTask(String json) {
		return serviceRemote.getTask(json);
	}

}

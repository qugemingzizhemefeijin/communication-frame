package com.tigerjoys.communication.server.test;

import com.tigerjoys.onion.communication.server.core.NettyBootstrap;
import com.tigerjoys.onion.communication.server.core.config.SocketServerConfig;
import com.tigerjoys.onion.communication.server.core.hotkey.DefaultProxyFactory;

public class ServerClient {

	public static void main(String[] args) throws InterruptedException {
		//初始化配置信息
		SocketServerConfig serviceConfig = new SocketServerConfig();
		serviceConfig.setBasePackages("com.tigerjoys.onion.communication.server.command");
		serviceConfig.setPort(9527);
		serviceConfig.setProxyFactory(new DefaultProxyFactory());
		
		NettyBootstrap boostrap = new NettyBootstrap(serviceConfig);
		boostrap.start();
	}

}

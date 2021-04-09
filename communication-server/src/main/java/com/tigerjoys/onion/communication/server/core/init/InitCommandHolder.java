package com.tigerjoys.onion.communication.server.core.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tigerjoys.onion.communication.server.core.config.SocketServerConfig;
import com.tigerjoys.onion.communication.server.core.context.Global;
import com.tigerjoys.onion.communication.server.core.hotkey.CommandHolder;

public class InitCommandHolder implements IInit {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InitCommandHolder.class);

	@Override
	public void init() {
		SocketServerConfig serviceConfig = Global.getInstance().getServiceConfig();
		
		LOGGER.info("----------------init command holder--------------------");
		CommandHolder.init(serviceConfig.getBasePackages() , serviceConfig.getProxyFactory());
		LOGGER.info("----------------init command holder finish--------------");
	}

}

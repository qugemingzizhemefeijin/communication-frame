package com.andrcid.process.client.core;

import android.util.Log;
import com.andrcid.process.client.core.config.SocketClientConfig;
import com.andrcid.process.client.core.context.CommunicationListener;
import com.andrcid.process.client.core.context.Global;
import com.andrcid.process.client.core.hotkey.GuiceModule;
import com.andrcid.process.client.core.server.SocketServer;
import com.andrcid.process.client.core.session.ISessionConnectionListener;
import com.google.inject.Guice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端启动类
 * @author chengang
 *
 */
public final class ClientBootstrap {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientBootstrap.class);
	
	public ClientBootstrap(SocketClientConfig clientConfig) {
		//检查配置信息
		checkConfig(clientConfig);
		//设置到全局变量中
		Global.getInstance().setClientConfig(clientConfig);
	}
	
	/**
	 * 添加Session连接监听器
	 * @param listener - ISessionConnectionListener
	 */
	public void addSessionConnectionListener(ISessionConnectionListener listener) {
		CommunicationListener.addSessionConnectionListener(listener);
	}
	
	/**
	 * 开启通信服务
	 * @throws InterruptedException 
	 */
	public void start() throws InterruptedException {
		LOGGER.info("----------------------start client netty server------------------");
//		Log.d("netty msg:", "----------------------start client netty server------------------");
		//加载guice注入服务
		loadGuice();
		
		//加载服务
		loadServer();
		
		LOGGER.info("----------------------start client netty server finish------------");
//		Log.d("netty msg:", "----------------------start client netty server finish------------");
	}
	
	/**
	 * 开启guice
	 */
	private void loadGuice() {
		Global.getInstance().setInjector(Guice.createInjector(new GuiceModule()));
	}
	
	/**
	 * 加载服务
	 * @throws InterruptedException 
	 */
	private void loadServer() throws InterruptedException {
		SocketServer server = new SocketServer();
		server.start();
	}
	
	/**
	 * 检查配置
	 * @param clientConfig - SocketClientConfig
	 */
	private void checkConfig(SocketClientConfig clientConfig) {
		if(clientConfig.getServerPort() <= 0) {
			throw new IllegalArgumentException("server port error");
		}
		if(clientConfig.getServerHost() == null || clientConfig.getServerHost().isEmpty()) {
			throw new IllegalArgumentException("server host can't be empty");
		}
		if(clientConfig.getDeviceId() == null || clientConfig.getDeviceId().isEmpty()) {
			throw new IllegalArgumentException("deviceId can't be empty");
		}
	}

}

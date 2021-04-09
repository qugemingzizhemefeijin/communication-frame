package com.tigerjoys.onion.communication.server.core.server;

import com.tigerjoys.onion.communication.server.core.context.ServerType;

/**
 * 服务器接口
 * @author chengang
 *
 */
public interface IServer {
	
	/**
	 * 开启服务器
	 * @throws Exception
	 */
	public abstract void start() throws Exception;
	
	/**
	 * 关闭服务器
	 * @throws Exception
	 */
	public abstract void stop() throws Exception;
	
	/**
	 * 服务类型
	 * @return ServerType
	 */
	public abstract ServerType getServerType();

}

package com.andrcid.process.client.core.context;

import java.util.ArrayList;
import java.util.List;

import com.andrcid.process.client.core.session.ISessionConnectionListener;

/**
 * 通信监听器工具类
 * @author chengang
 *
 */
public final class CommunicationListener {
	
	/**
	 * Session连接监听器
	 */
	private static final List<ISessionConnectionListener> SESSION_CONNECTION_LISTENER_LIST = new ArrayList<>();
	
	/**
	 * 添加一个Session连接监听器
	 * @param listener - ISessionConnectionListener
	 */
	public static void addSessionConnectionListener(ISessionConnectionListener listener) {
		SESSION_CONNECTION_LISTENER_LIST.add(listener);
	}
	
	/**
	 * 移除一个Session连接监听器
	 * @param listener - ISessionConnectionListener
	 */
	public static void removeSessionConnectionListener(ISessionConnectionListener listener) {
		SESSION_CONNECTION_LISTENER_LIST.remove(listener);
	}
	
	/**
	 * 清空Session连接监听器
	 */
	public static void clearSessionConnectionListener() {
		SESSION_CONNECTION_LISTENER_LIST.clear();
	}
	
	/**
	 * 获取所有的Session连接监听器
	 * @return List<ISessionConnectionListener>
	 */
	public static List<ISessionConnectionListener> getSessionConnectionListener() {
		return SESSION_CONNECTION_LISTENER_LIST;
	}

}

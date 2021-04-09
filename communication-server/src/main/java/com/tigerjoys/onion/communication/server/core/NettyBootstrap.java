package com.tigerjoys.onion.communication.server.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tigerjoys.onion.communication.server.core.config.SocketServerConfig;
import com.tigerjoys.onion.communication.server.core.context.Global;
import com.tigerjoys.onion.communication.server.core.filter.ExecuteTimeAlarmFilter;
import com.tigerjoys.onion.communication.server.core.filter.IPConnectionFilter;
import com.tigerjoys.onion.communication.server.core.filter.RequestLogFilter;
import com.tigerjoys.onion.communication.server.core.filter.ResponseLogFilter;
import com.tigerjoys.onion.communication.server.core.init.IInit;
import com.tigerjoys.onion.communication.server.core.init.InitCommandHolder;
import com.tigerjoys.onion.communication.server.core.server.IServer;
import com.tigerjoys.onion.communication.server.core.tcp.SocketServer;

/**
 * 启动类
 * @author chengang
 *
 */
public final class NettyBootstrap {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyBootstrap.class);
	
	public NettyBootstrap(SocketServerConfig serviceConfig) {
		//loadServiceConfig
		Global.getInstance().setServiceConfig(serviceConfig);
	}

	/**
	 * 开启服务，暂时所有的都是写死的，以后可以做成配置加载
	 * @param - SocketServerConfig
	 * @throws InterruptedException 
	 */
	public void start() throws InterruptedException {
		//初始化执行列表
		loadInit();
		
		//初始化过滤器
		loadRequestFilter();
		loadResponseFilter();
		loadConnectionFilter();
		
		//加载服务
		loadServer();
		
		//注册虚拟机关闭时候的监听事件
		try {
			registerShutdownEvent();
		} catch (Exception e) {
			LOGGER.error("registerExcetEven error", e);
			System.exit(0);
		}
		
		//阻塞当前线程
		LOGGER.info("+++++++++++++++++++++ server start success!!! +++++++++++++++++++++\n");
		while (true) Thread.sleep(3600000L);
	}
	
	/**
	 * 初始化Init
	 */
	private static void loadInit() {
		LOGGER.info("-----------------loading init beans------------------");
		Global.getInstance().addInit(new InitCommandHolder());
		
		for(IInit i : Global.getInstance().getInitList()) {
			i.init();
		}
		LOGGER.info("-------------------------end-------------------------\n");
	}
	
	/**
	 * 初始化请求过滤器
	 */
	private static void loadRequestFilter() {
		LOGGER.info("-----------loading global request filters------------");
		Global.getInstance().addRequestFilter(new RequestLogFilter());
		LOGGER.info("-------------------------end-------------------------\n");
	}
	
	/**
	 * 初始化应答过滤器
	 */
	private static void loadResponseFilter() {
		LOGGER.info("-----------loading global response filters-----------");
		Global.getInstance().addResponseFilter(new ExecuteTimeAlarmFilter());
		Global.getInstance().addResponseFilter(new ResponseLogFilter());
		LOGGER.info("-------------------------end-------------------------\n");
	}
	
	/**
	 * 初始化连接过滤器
	 */
	private static void loadConnectionFilter() {
		LOGGER.info("-----------loading connection filters-----------");
		Global.getInstance().addConnectionFilter(new IPConnectionFilter());
		LOGGER.info("-------------------------end-------------------------\n");
	}
	
	/**
	 * 加载Server服务
	 */
	private static void loadServer() {
		//初始化服务
		Global.getInstance().addServer(new SocketServer());
		
		LOGGER.info("------------------ starting servers -----------------");
		List<IServer> serverList = Global.getInstance().getServerList();
		if(serverList != null && !serverList.isEmpty()) {
			for(IServer server : serverList) {
				try {
					server.start();
				} catch (Exception e) {
					LOGGER.error(e.getMessage() , e);
				}
			}
		}
		LOGGER.info("-------------------------end-------------------------\n");
	}
	
	/**
	 * 注册虚拟机停止后执行的回收资源事件
	 */
	private static void registerShutdownEvent() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				for (IServer server : Global.getInstance().getServerList()) {
					try {
						server.stop();
					} catch (Exception e) {
						LOGGER.error("stop server error", e);
					}
				}
				try {
					super.finalize();
				} catch (Throwable e) {
					LOGGER.error("super.finalize() error when stop server", e);
				}
			}
		});
	}

}

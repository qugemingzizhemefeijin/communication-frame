package com.tigerjoys.onion.communication.server.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tigerjoys.onion.communication.server.core.config.SocketServerConfig;
import com.tigerjoys.onion.communication.server.core.context.BeatContext;
import com.tigerjoys.onion.communication.server.core.context.Global;

/**
 * 代码执行时间告警输出过滤器
 * @author chengang
 *
 */
public class ExecuteTimeAlarmFilter implements IFilter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteTimeAlarmFilter.class);

	@Override
	public void filter(BeatContext context) throws Exception {
		SocketServerConfig config = Global.getInstance().getServiceConfig();
		if(config.isSlowMethod()) {
			long cost = context.getInvokeEndTime() - context.getInvokeBeginTime();
			if (cost >= config.getSlowMethodMillis()) {//调用超过指定毫秒，打印日志
				String msg = "time:" + cost + "ms, remoteIP:" +context.getRemoteIP() + ", mapping:" + context.getMapping();
				
				LOGGER.warn(msg);
			}
		}
	}
	
	@Override
	public FilterType getFilterType() {
		return FilterType.Response;
	}

}

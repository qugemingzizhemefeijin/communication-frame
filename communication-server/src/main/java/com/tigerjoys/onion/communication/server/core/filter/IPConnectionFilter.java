package com.tigerjoys.onion.communication.server.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tigerjoys.onion.communication.server.core.context.BeatContext;
import com.tigerjoys.onion.communication.server.utils.IPTable;

/**
 * IP过滤过滤器
 * @author chengang
 *
 */
public class IPConnectionFilter implements IFilter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IPConnectionFilter.class);

	@Override
	public void filter(BeatContext context) throws Exception {
		String ip = context.getRemoteIP();
		
		if (IPTable.isAllow(ip)) {
			LOGGER.info("new channel conected:" + ip);
		} else {
			LOGGER.error("forbid ip not allow connect:" + ip);
			context.getSession().close();
		}
	}
	
	@Override
	public FilterType getFilterType() {
		return FilterType.CONNECTION;
	}

}

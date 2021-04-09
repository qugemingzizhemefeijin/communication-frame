package com.tigerjoys.onion.communication.server.command;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.tigerjoys.communication.protocol.message.RequestMessage;
import com.tigerjoys.communication.protocol.utility.FastJsonHelper;
import com.tigerjoys.onion.communication.server.core.hotkey.Command;
import com.tigerjoys.onion.communication.server.core.hotkey.CommandMapping;
import com.tigerjoys.onion.communication.server.vo.TestVO;

@Command
public class TestCommand {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestCommand.class);

	private final AtomicInteger increment = new AtomicInteger();
	
	@CommandMapping("/api/test/{abc}")
	public List<String> test(RequestMessage request , int abc , JSONObject json , TestVO vo){
		LOGGER.info("我进来了此方法！！！");
		LOGGER.info("server receive body : " + request.getBody());
		LOGGER.info("abc = " + abc);
		LOGGER.info("json = " + json.toJSONString());
		LOGGER.info("vo = " + FastJsonHelper.toJson(json));
		
		List<String> list = new ArrayList<>();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
		
		return list;
	}

	@CommandMapping("/api/getTask")
	public String getTask(JSONObject json) {
		return increment.incrementAndGet()+":"+json.toJSONString();
	}
	
}

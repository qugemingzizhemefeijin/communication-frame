package com.tigerjoys.onion.communication.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tigerjoys.communication.protocol.message.RequestMessage;
import com.tigerjoys.onion.communication.server.service.ITestService;

@Service
public class TestServiceImpl implements ITestService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestServiceImpl.class);

	@Override
	public List<String> test(RequestMessage request, int abc) {
		/*if(true) {
			throw new NullPointerException("我就是异常");
		}*/
		
		List<String> list = new ArrayList<>();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
		
		return list;
	}

}

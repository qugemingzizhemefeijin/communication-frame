package com.tigerjoys.onion.communication.server.service;

import java.util.List;

import com.tigerjoys.communication.protocol.message.RequestMessage;

public interface ITestService {
	
	public List<String> test(RequestMessage request , int abc);

}

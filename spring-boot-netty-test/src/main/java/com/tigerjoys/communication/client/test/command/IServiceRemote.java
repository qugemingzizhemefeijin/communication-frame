package com.tigerjoys.communication.client.test.command;

import java.util.List;

import com.andrcid.process.client.core.hotkey.Controller;
import com.andrcid.process.client.core.hotkey.Future;
import com.andrcid.process.client.core.hotkey.IAsyncCallBackListener;
import com.andrcid.process.client.core.hotkey.Param;
import com.andrcid.process.client.core.hotkey.ProxyModule;
import com.andrcid.process.client.core.hotkey.RequestMapping;
import com.andrcid.process.client.core.hotkey.RequestMethod;
import com.tigerjoys.communication.protocol.message.ResponseMessage;

@ProxyModule
@Controller
public interface IServiceRemote {
	
	@RequestMapping("/api/test/123")
	public List<String> sayHello(@Param("aa") String x , @Param("b") int b);

	@RequestMapping("/api/test/345")
	public List<String> sayWorld(String json);
	
	@RequestMapping("/api/test/567")
	public void sayVoid(@Param("aa") String x , @Param("b") int b);

	@RequestMapping("/api/test/789")
	public ResponseMessage sayMessage(@Param("aa") String x , @Param("b") int b);
	
	@RequestMapping(value="/api/test/911" , method = RequestMethod.ASYNC)
	public List<String> sayAsyncHello(IAsyncCallBackListener callback , @Param("aa") String x , @Param("b") int b , IAsyncCallBackListener callback2);
	
	@RequestMapping(value="/api/test/888")
	public Future sayFuture(@Param("aa") String x , @Param("b") int b);

}

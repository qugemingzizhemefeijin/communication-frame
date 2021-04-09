package com.tigerjoys.communication.protocol.message;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tigerjoys.communication.protocol.Protocol;
import com.tigerjoys.communication.protocol.ProtocolConst;
import com.tigerjoys.communication.protocol.utility.ProtocolHelper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class MessageDecoder extends ByteToMessageDecoder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageDecoder.class);
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		LOGGER.debug("message receive");
		int totalLength = in.readableBytes();
		LOGGER.debug("reciveByte.length:" + totalLength);
		
		byte[] headDelimiter = new byte[ProtocolConst.P_START_TAG.length];
		in.readBytes(headDelimiter);
		
		if(ProtocolHelper.checkHeadDelimiter(headDelimiter)) {
			//读取所有信息
			byte[] requestBuffer = new byte[totalLength - ProtocolConst.P_START_TAG.length];
			in.readBytes(requestBuffer);
			
			Protocol p = new Protocol();
			p.fromBytes(requestBuffer);
			
			out.add(p);
		} else {
			LOGGER.error("protocol error: protocol head not match");
		}
	}
	
}
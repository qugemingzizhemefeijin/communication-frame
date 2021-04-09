package com.tigerjoys.communication.protocol.enums;

import com.tigerjoys.communication.protocol.serialize.ISerialize;
import com.tigerjoys.communication.protocol.serialize.JSONSerialize;

/**
 * 序列化枚举
 * 
 * @author chengang
 *
 */
public enum SerializeType {

	/**
	 * json
	 */
	JSON(1 , new JSONSerialize()),
	
	/**
	 * java
	 */
	JAVABinary(2 , null),
	
	/**
	 * xml
	 */
	XML(3 , null);

	/**
	 * 序列化编码
	 */
	private final int code;
	
	/**
	 * 序列化对应的对象实体
	 */
	private final ISerialize serialize;
	
	private SerializeType(int code , ISerialize serialize) {
		this.code = code;
		this.serialize = serialize;
	}

	public int getCode() {
		return this.code;
	}

	public ISerialize getSerialize() {
		return serialize;
	}

	public static SerializeType getSerializeType(int code) {
		for (SerializeType type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		return null;
	}

}

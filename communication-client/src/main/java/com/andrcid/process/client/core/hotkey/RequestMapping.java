package com.andrcid.process.client.core.hotkey;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口映射注解
 * 
 * @author chengang
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

	/**
	 * 通信接口的URL地址
	 * @return String
	 */
	String value() default "";
	
	/**
	 * 通信接口的请求方式
	 * @return RequestMethod
	 */
	RequestMethod method() default RequestMethod.SYNC;
}

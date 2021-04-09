package com.andrcid.process.client.core.hotkey;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注方法参数名称
 * @author chengang
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {
	
	/**
	 * 映射名称
	 * @return String
	 */
	String value();

}

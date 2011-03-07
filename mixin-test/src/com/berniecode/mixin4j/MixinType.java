package com.berniecode.mixin4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Used to label an interface as a mixin type.
 * 
 * @author Bernard Sumption
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MixinType {
	/**
	 * <p>Controls how to look up the mixin implementation for this mixin type.
	 * 
	 * <p>The class provided must either be a class that implements this mixin type,
	 * or an {@link ImplementationSource} class that can be used to look up an appropriate
	 * mixin implementation class
	 */
	public Class<?> implementation();
}
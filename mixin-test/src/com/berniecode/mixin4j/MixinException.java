package com.berniecode.mixin4j;

/**
 * <p>Used to signal problems with the mixing process
 * 
 * @author Bernard Sumption
 */
public class MixinException extends RuntimeException {

	public MixinException(String message) {
		super(message);
	}

	public MixinException(String message, Exception e) {
		super(message, e);
	}
}

package com.berniecode.mixin4j.test.hello;

import com.berniecode.mixin4j.GenericParameterImplementationSource;
import com.berniecode.mixin4j.MixinType;

/**
 * <p>Demo mixin type.
 * 
 * @author Bernard Sumption
 *
 * @param <T> used by the mixin base class to specify the implementation. If the mixin base class
 * is defined like so: {@code @MixinBase MyMixinBase implements GreetingMixin<GreetingMixinImpl>} then
 * the class <code>GreetingMixinImpl</code> will be used as an a mixin implementation class
 */
@MixinType(implementation = GenericParameterImplementationSource.class)
public interface GreetingMixin<IMPL> {
	String getGreeting();
}

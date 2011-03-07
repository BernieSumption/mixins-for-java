package com.berniecode.mixin4j.test.hello;

import com.berniecode.mixin4j.MixinBase;
import com.berniecode.mixin4j.MixinSupport;

/**
 * <p>This object implements the GreetingMixin interface, and specifies {@link PoshGreeting}
 * as an implementation. When an instance is created by {@link MixinSupport}, the HelloGreeting
 * implementation will be mixed in to this object
 * 
 * @author Bernard Sumption
 */
@MixinBase
public abstract class StaticallyMixedObject implements GreetingMixin<PoshGreeting> {
	
	public void doGreeting() {
		System.out.println(getGreeting());
	}
	
	public static StaticallyMixedObject getInstance() {
		return MixinSupport.getSingleton().newInstanceOf(StaticallyMixedObject.class);
	}

}

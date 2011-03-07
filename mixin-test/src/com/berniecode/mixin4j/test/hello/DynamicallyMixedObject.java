package com.berniecode.mixin4j.test.hello;


import com.berniecode.mixin4j.MixinBase;
import com.berniecode.mixin4j.MixinSupport;
import com.berniecode.mixin4j.GenericParameterImplementationSource.Dynamic;

/**
 * <p>This object implements the GreetingMixin interface, but instead of specifying an implementation
 * in the type parameter (like {@link StaticallyMixedObject}), leaves the class generic. This means that
 * the implementation must be provided at runtime.
 * 
 * @author Bernard Sumption
 */
@MixinBase
public abstract class DynamicallyMixedObject implements GreetingMixin<Dynamic> {
	
	public void doGreeting() {
		System.out.println(getGreeting());
	}
	
	public static DynamicallyMixedObject getInstance() {
		return MixinSupport.getSingleton().newInstanceOf(DynamicallyMixedObject.class);
	}

}

package com.berniecode.mixin4j.test.hello;


import com.berniecode.mixin4j.*;

/**
 * <p>Simple test of static and dynamic mixins
 * 
 * @author Bernard Sumption
 */
public class HelloTestRunner {

	public static void main(String[] args) throws Exception {
		
		System.out.print("Dynamically mixed northern greeting: ");
		
		// Specify the mixin: apply the GreetingMixin mixin, using NorthernGreeting an an implementation
		// to the DynamicallyMixedObject base class
		Mixin<DynamicallyMixedObject> mixin = new Mixin<DynamicallyMixedObject>(
				GreetingMixin.class, NorthernGreeting.class, DynamicallyMixedObject.class);
		
		// create a mixer and apply the mixin
		Factory<? extends DynamicallyMixedObject> factory =
			new DynamicProxyMixer().getFactory(DynamicallyMixedObject.class, mixin);
		
		// test it - this should print a northern greeting
		DynamicallyMixedObject dmo = factory.newInstance();
		dmo.doGreeting();
		

		
		System.out.print("Statically mixed posh greeting: ");

		StaticallyMixedObject smo = MixinSupport.getSingleton().newInstanceOf(StaticallyMixedObject.class);
		smo.doGreeting();	
	}

}

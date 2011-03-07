package com.berniecode.mixin4j.test.hello;

/**
 * <p>Greet the user in a toffee-nosed fashion
 * 
 * @author Bernard Sumption
 */
public class PoshGreeting implements GreetingMixin<PoshGreeting> {

	public String getGreeting() {
		return "Good day, dear Sir";
	}

}

package com.berniecode.mixin4j.test.hello;

import com.berniecode.mixin4j.GenericParameterImplementationSource.Dynamic;

/**
 * 
 * @author Bernard Sumption
 *
 */
public class NorthernGreeting implements GreetingMixin<Dynamic> {

	public String getGreeting() {
		return "Yer right, duck?";
	}

}

package com.berniecode.mixin4j;

import java.util.List;

/**
 * <p>A mixer takes a ClassWithMixins instance and produces a subclass of mixin base class
 * that delegates methods not implemented in the base class to an instance of the
 * appropriate mixin implementation
 * 
 * <p>For anyone familiar with AOP terminology, this is the equivalent of a weaver.
 * 
 * @author Bernard Sumption
 */
public interface Mixer {
	
	/**
	 * Return a Factory that can make new instances of a mixed class
	 * 
	 * @param mixinBase the mixin base class
	 * @param mixins a list of mixins to apply to the base class
	 */
	public <T> Factory<? extends T> getFactory(Class<T> mixinBase, List<Mixin<T>> mixins);

	/**
	 * Return a Factory that can make new instances of a mixed class
	 * 
	 * @param mixinBase the mixin base class
	 * @param mixin a single mixin to apply to the base class
	 */
	public <T> Factory<? extends T> getFactory(Class<T> mixinBase, Mixin<T> mixin);
}
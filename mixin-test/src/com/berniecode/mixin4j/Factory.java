package com.berniecode.mixin4j;

/**
 * <p>A factory for creating instances of a type.
 * 
 * <p>Factory instances are returned by {@link Mixer} instances to create instances of
 * the mixed classes that they have generated
 * 
 * @param <T> the type that will be created
 * 
 * @author Bernard Sumption
 */
public interface Factory<T> {
	
	/**
	 * Create a new object using the no-arg constructor
	 */
	public T newInstance();
	
	/**
	 * Create a new object using specific constructor arguments
	 * @param constructorArgTypes The types of the arguments
	 * @param constructorArgs The value of the arguments
	 */
	public T newInstance(Class<?>[] constructorArgTypes, Object[] constructorArgs);
}

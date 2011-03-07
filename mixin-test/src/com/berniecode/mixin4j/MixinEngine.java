package com.berniecode.mixin4j;

/**
 * <p>Represents a mixin system. The default mixing engine, {@link MixinSupport}, is
 * extremely configurable and is designed allow dramatic extension by swapping implementations
 * of the {@link ImplementationSource} and {@link Mixer} interfaces.
 * 
 * <p>However, it still has limitations. For example, it enforces the constraint that all
 * mixins are defined as an interface (the mixin type) and that mixin base classes implement
 * this interface in order to indicate that they want the mixin. If this constraint is unsuitable,
 * you could implement a new MixinEngine.
 * 
 * <p>For example, perhaps you want a central XML file that lists the mixins that should be applied
 * to various classes. Go ahead and implement that as a MixinEngine, you weirdo.
 * 
 * @author Bernard Sumption
 */
public interface MixinEngine {

	/**
	 * Create a new instance of a mixed class using the default no-arg constructor
	 */
	public <T> T newInstanceOf(Class<T> mixinBase);

	/**
	 * Return a new instance of a mixed class using a specific constructor.
	 * 
	 * @param mixinBase The class to create a new instance of
	 * @param constructorArgTypes argument types to look up the constructor with
	 * @param constructorArgs argument values to pass to the constructor
	 */
	public <T> T newInstanceOf(Class<T> mixinBase, Class<?>[] constructorArgTypes, Object[] constructorArgs);

	/**
	 * Return a new instance of a mixed class using a constructor with a given argument count.
	 * 
	 * This method requires the constructor to be unambiguous, i.e. there can't be more than one
	 * constructor that takes the same number of arguments as provided in constructorArgs
	 * 
	 * @param mixinBase The class to create a new instance of
	 * @param constructorArgs argument values to pass to the constructor
	 */
	public <T> T newInstanceOf(Class<T> mixinBase, Object[] constructorArgs);

}

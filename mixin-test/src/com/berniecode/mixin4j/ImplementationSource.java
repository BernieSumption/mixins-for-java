package com.berniecode.mixin4j;

/**
 * <p>A mixin consists of an interface (the mixin type) and a class that implements
 * that interface (the mixin implementation).
 * 
 * <p>The mixin type is specified directly by the programmer by implementing the 
 * interface on the class that she wants to be mixed (the mixin base).
 * 
 * <p>An ImplementationSource is used by the author of the mixin type to control how
 * the mixin implementation is chosen, by specifying an ImplementationSource in the @MixinType
 * annotation.
 * 
 * <p>Writing your own ImplementationSource is a very advanced degree of customisation:
 * the vast majority of mixin authors will find the provided sources adequate.
 * 
 * @author Bernard Sumption
 */
public interface ImplementationSource {
	
	/**
	 * <p>Source an implementation for a mixin type.
	 * 
	 * <p>For example, if the interface signature of the mixin base class is:
	 * 
	 * <pre>@MixinBase class Foo implements SomeMixin</pre>
	 * 
	 * <p>Then this method will be called as:
	 * 
	 * <pre>getImplementation(SomeMixin.class, Foo.class);</pre>
	 * 
	 * @return a class that implements the declared interface
	 */
	public Class<?> getImplementation(Class<?> declaredInteface, Class<?> declaringClass);

}

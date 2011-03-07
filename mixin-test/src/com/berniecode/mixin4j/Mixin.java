package com.berniecode.mixin4j;

import java.lang.reflect.Modifier;


/**
 * <p>Represents a bit of functionality that is added to a class at runtime. It consists of a
 * mixin type interface and associated concrete class that implements the type interface.
 * 
 * @param <B> the type of the mixin base class
 * 
 * @author Bernard Sumption
 */
public class Mixin<B> {
	
	private Class<?> mixinType;
	private Class<?> mixinImpl;

	public Mixin(Class<?> mixinType, Class<?> mixinImpl, Class<B> mixinBase) {
		requireConcreteImplementationOfType(mixinType, mixinImpl, "mixin implementation", false);
		requireConcreteImplementationOfType(mixinType, mixinBase, "mixin base", true);
		this.mixinType = mixinType;
		this.mixinImpl = mixinImpl;
	}

	private void requireConcreteImplementationOfType(Class<?> type, Class<?> impl, String implDesc, boolean isAbstract) {
		String failReason = null;
		if (!type.isAssignableFrom(impl)) {
			failReason = "does not implement the mixin type as an interface";
		} else if (impl.isInterface()) {
			failReason = "is an interface, not a class";
		} else if (Modifier.isAbstract(impl.getModifiers()) != isAbstract) {
			failReason = isAbstract ? "is not abstract" : "is abstract";
		}
		if (failReason != null) {
			throw new MixinException("Can't apply mixin type " + type.getCanonicalName()
					+ " because the " + implDesc + " class " + impl.getCanonicalName()
					+ " " + failReason + ".");
		}
	}
	
	/**
	 * Get the type interface for this Mixin
	 */
	public Class<?> getMixinType() {
		return this.mixinType;
	}

	/**
	 * Get the concrete class that implements the type interface
	 */
	public Class<?> getMixinImpl() {
		return this.mixinImpl;
	}


}
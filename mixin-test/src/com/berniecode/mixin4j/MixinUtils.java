package com.berniecode.mixin4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class MixinUtils {
	
	/**
	 * <p>Fetch an array of constructor argument types
	 * 
	 * @param klass the class that the constructor belongs to
	 * @param length the length of the argument list
	 * @throws MixinException if there is not exactly one constructor with the right argument list length
	 * @return an array of argument types
	 */
	public static Class<?>[] getConstructorArgTypes(Class<?> klass, int length) {
		Constructor<?> target = null;
		for (Constructor<?> test: klass.getDeclaredConstructors()) {
			if (test.getParameterTypes().length == length) {
				if (target != null) {
					throw new MixinException("Class " + klass.getCanonicalName()
							+ " has more then one constructor with " + length + " parameters");
				} else {
					target = test;
				}
			}
		}
		if (target != null) {
			return target.getParameterTypes();
		}
		throw new MixinException("Class " + klass.getCanonicalName()
				+ " has no constructors with exactly " + length + " parameters");
	}

	/**
	 * <p>part of the {@link Mixer} contract is to correctly handle the {@link MixinAware} interface.
	 * 
	 * <p>This method implements the necessary processing.
	 * 
	 * @param implementation the newly created instance of a mixin implementation class
	 * @param newMixedObject the instance of the mixin base class that the implementation is to be mixed into
	 * 
	 * @see MixinAware
	 */
	public static void handleMixinAwareness(Object implementation, Object newMixedObject, Class<?> mixinBase) {
		if (!(implementation instanceof MixinAware)) {
			return;
		}
		// get the mixin constraint type from the implementation, e.g. if the implementation
		// implements MixinAware<Iterable<String>> then the constraint type is Iterable<String>
		for (Type gInterface: implementation.getClass().getGenericInterfaces()) {
			if (gInterface instanceof ParameterizedType) {
				ParameterizedType pInterface = (ParameterizedType) gInterface;
				if (pInterface.getRawType().equals(MixinAware.class)) {
					Type t = pInterface.getActualTypeArguments()[0];
					if (!classAssignableToType(newMixedObject, t)) {
						throw new MixinException("Mixin-aware implementation class "
								+ implementation.getClass().getCanonicalName()
								+ " can't be mixed with base class "
								+ mixinBase.getCanonicalName()
								+ " because the base class does not match the type "
								+ t);
					}
				}
			}
		}
		
		MixinAware<Object> mixinAware = (MixinAware<Object>) implementation;
		mixinAware.setMixinBase(newMixedObject);
	}

	/**
	 * Check whether a class matches the type specified. For example, if the type is
	 * {@code Iterable<? extends InputStream>} and the class implements the interface
	 * {@code Iterable<? extends BufferedInputStream>} then method would return true.
	 * 
	 * @param testClass
	 * @param targetType
	 */
	public static boolean classAssignableToType(Object testClass, Type targetType) {
		//TODO implement this (currently it just returns true).
		// If a List<Integer> is passed into the setMixinBase method of a MixinAware<Iterable<String>>,
		// it will fail immediately with a ClassCastException since the outer types, List and Iterable,
		// are incompatible. However, if a List<Integer> is passed into the setMixinBase method of a
		// MixinAware<List<String>>, the initial call will succeed, and the program will fail later
		// when it tries to use what it thinks is a string, and turns out to be an integer.
		return true;
	}
	
	/**
	 * Check if a Class object represents a valid mixin base class.
	 * 
	 * @param mixinBase
	 */
	public static void validateMixinBase(Class<?> mixinBase) {
		if (mixinBase.isInterface()) {
			throw new MixinException("Can't apply mixins to an interface '"
					+ mixinBase.getCanonicalName() + "' - a class is required.");
		}
		int mods = mixinBase.getModifiers();
		if (!Modifier.isAbstract(mods)) {
			throw new MixinException("Can't apply mixins to non-abstract base class '"
					+ mixinBase.getCanonicalName() + "'.");
		}
		if (Modifier.isFinal(mods)) {
			throw new MixinException("Can't apply mixins to final base class '"
					+ mixinBase.getCanonicalName() + "'.");
		}
	}

}

package com.berniecode.mixin4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Provides static methods to apply mixins to mixin base classes.
 * 
 * @author Bernard Sumption
 *
 */
public class MixinSupport implements MixinEngine {
	
	private static MixinSupport singleton;
	
	private static Object singletonLock = new Object();
	
	/**
	 * @return an instance of this class for general purpose use
	 */
	public static MixinSupport getSingleton() {
		synchronized (singletonLock) {
			if (singleton == null) {
				singleton = new MixinSupport();
			}
		}
		return singleton;
	}
	
	//
	// PUBLIC API
	//

	/**
	 * {@inheritDoc}
	 */
	public <T> T newInstanceOf(Class<T> mixinBase) {
		MixinUtils.validateMixinBase(mixinBase);
		return getFactory(mixinBase).newInstance();
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T newInstanceOf(Class<T> mixinBase, Class<?>[] constructorArgTypes, Object[] constructorArgs) {
		MixinUtils.validateMixinBase(mixinBase);
		return getFactory(mixinBase).newInstance(constructorArgTypes, constructorArgs);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T newInstanceOf(Class<T> mixinBase, Object[] constructorArgs) {
		MixinUtils.validateMixinBase(mixinBase);
		Class<?>[] constructorArgTypes = MixinUtils.getConstructorArgTypes(mixinBase, constructorArgs.length);
		return getFactory(mixinBase).newInstance(constructorArgTypes, constructorArgs);
	}
	
	public <T> T create(Class<T> mixinBase, Object... constructorArgs) {
		return newInstanceOf(mixinBase, constructorArgs);
	}
	
	//
	// PRIVATE MACHINERY
	//


	private Map<Class<?>, Factory<?>> factoryCache = new HashMap<Class<?>, Factory<?>>();

	// safe because putFactoryCache ensures that only Factory<? extends T> is stored with a key Class<T>
	private <T> Factory<? extends T> getFactoryCache(Class<T> mixinClass) {
		return (Factory<? extends T>) factoryCache.get(mixinClass);
	}
	private <T> void putFactoryCache(Class<T> mixinClass, Factory<? extends T> factory) {
		factoryCache.put(mixinClass, factory);
	}
	
	private <C> Factory<? extends C> getFactory(Class<C> mixinBase) {
		Factory<? extends C> factory = getFactoryCache(mixinBase);
		if (factory == null) {
			
			MixinBase annotation = getMixinBaseAnnotation(mixinBase);
			
			List<Mixin<C>> mixins = new ArrayList<Mixin<C>>();
			
			// for each parameterised interface implemented by the type
			for (Class<?> mixinType: mixinBase.getInterfaces()) {
				
				Class<?> mixinImpl = null;
				
				MixinType typeAnnotation = mixinType.getAnnotation(MixinType.class);
				if (typeAnnotation == null) {
					continue;
				}
				
				Class<?> sourceClass = typeAnnotation.implementation();
				
				// if sourceClass is an implementation of the mixin type, use it directly
				if (mixinType.isAssignableFrom(sourceClass)) {
					// safe cast because of enclosing check
					mixinImpl = sourceClass;
				}
				// else if sourceClass is an implementationSource, use that to look up an implementation
				else if (ImplementationSource.class.isAssignableFrom(sourceClass)) {
					ImplementationSource source;
					try {
						source = (ImplementationSource) sourceClass.newInstance();
					} catch (Exception e) {
						throw new MixinException("Can't resolve mixin implementation source", e);
					}

					// safe cast because mixinType is an interface declared on mixinBase, which is a
					// Class<X>, so mixinBase can't not be a Class<? extends X>
					mixinImpl = source.getImplementation(mixinType, mixinBase);
					
				}
				if (mixinImpl != null) {
					mixins.add(new Mixin<C>(mixinType, mixinImpl, mixinBase));
				}
			}
			
			// get mixed instance according to the engine annotation parameter
			Mixer mixer;
			try {
				mixer = annotation.mixerClass().newInstance();
			} catch (Exception e) {
				throw new MixinException("Can't load mixer class '" + annotation.mixerClass() + "'", e);
			}
			
			factory = mixer.getFactory(mixinBase, mixins);
			
			putFactoryCache(mixinBase, factory);
		}
		return factory;
	}

	/**
	 * Check that a mixin base class is valid and return its {@link MixinBase} annotation.
	 */
	private <T> MixinBase getMixinBaseAnnotation(Class<T> mixinBase) {
		MixinBase annotation = mixinBase.getAnnotation(MixinBase.class);
		if (annotation == null) {
			throw new MixinException("'" + mixinBase.getCanonicalName()
					+ "' is not a mixin base class (not annotated @MixinBase)");
		}
		return annotation;
	}

}

package com.berniecode.mixin4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * <p>Use CGLib proxies to subclass the base class. All calls to the base class are intercepted, and
 * calls to methods from the Mixin interfaces are trapped and dispatched to an instance of
 * the appropriate mixin implementation class.
 * 
 * @author Bernard Sumption
 */
public class DynamicProxyMixer implements Mixer {

	/**
	 * {@inheritDoc}
	 */
	public <T> Factory<? extends T> getFactory(Class<T> mixinBase, Mixin<T> mixin) {
		List<Mixin<T>> mixins = new ArrayList<Mixin<T>>();
		mixins.add(mixin);
		return getFactory(mixinBase, mixins);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> Factory<? extends T> getFactory(Class<T> mixinBase, List<Mixin<T>> mixins) {
		
		Map<Class<?>, Class<?>> delegateClasses = new HashMap<Class<?>, Class<?>>();
		for (Mixin<T> mixin: mixins) {
			try {
				Class<?> delegateClass = mixin.getMixinImpl();
				List<Class<?>> list = new ArrayList<Class<?>>();
//				delegateClasses.put(mixinBase, delegateClass);
				getAllInterfacesForClass(mixin.getMixinType(), list);
				for (Class<?> extended: list) {
					if (extended.isAssignableFrom(delegateClass)) {
						delegateClasses.put(extended, delegateClass);
					}
				}
			} catch (Exception e) {
				throw new MixinException("Could not create mixin delegate instance", e);
			}
		}
		
		DelegatingMethodInterceptor interceptor = new DelegatingMethodInterceptor(delegateClasses);
		
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(mixinBase);
        enhancer.setCallback(interceptor);

		return new CGLibEnhancerFactory<T>(enhancer, interceptor, mixinBase);
	}

	private void getAllInterfacesForClass(Class<?> type, List<Class<?>> accumulator) {
		accumulator.add(type);
		for (Class<?> extended: type.getInterfaces()) {
			getAllInterfacesForClass(extended, accumulator);
		}
	}
	
}

/**
 * A {@link Factory} that uses CGLib enhancers to generate instances of a mixed class
 * @author Bernard Sumption
 */
class CGLibEnhancerFactory <T> implements Factory <T> {
	
	private Enhancer enhancer;
	private DelegatingMethodInterceptor interceptor;
	private Class<T> typeExpected;
	
	public CGLibEnhancerFactory(Enhancer enhancer, DelegatingMethodInterceptor interceptor, Class<T> typeExpected) {
		this.enhancer = enhancer;
		this.interceptor = interceptor;
		this.typeExpected = typeExpected;
	}
	public T newInstance(Class<?>[] constructorArgTypes, Object[] constructorArgs) {
		// safe as long as CGLib returns the correct type of object
		return processNewObject(enhancer.create(constructorArgTypes, constructorArgs));
	}
	public T newInstance() {
		// safe as long as CGLib returns the correct type of object
		return processNewObject(enhancer.create());
	}
	
	private T processNewObject(Object newObject) {
		interceptor.registerNewObject(newObject);
		return typeExpected.cast(newObject);
	}
}

/**
 * <p>This CGLib MethodInterceptor traps every call to the generated object, and decides whether to
 * delegate it to a mixin implementation, or to the super class (the mixin base)
 * 
 * @author Bernard Sumption
 */
class DelegatingMethodInterceptor implements MethodInterceptor {
	
	// stored constructor arg
	private Map<Class<?>, Class<?>> interfaceToImplementationMap;
	
	/**
	 * A weakly keyed map that stores the delegate map as returned by createDelegateMap for each active mixed object
	 */
	private Map<Object, Map<Class<?>, Object>> delegateMaps = new WeakHashMap<Object, Map<Class<?>, Object>>();
	
	/**
	 * <p>Constructor
	 * 
	 * @param interfaceToImplementationMap A map of any mixin type interfaces or super-types of mixin type interfaces
	 * to the mixin implementation class that implements that interface.
	 */
	public DelegatingMethodInterceptor(Map<Class<?>, Class<?>> interfaceToImplementationMap) {
		this.interfaceToImplementationMap = interfaceToImplementationMap;
	}

	/**
	 * <p>Called by CGLibEnhancerFactory before the object is returned so that we can create the delegate instances
	 * 
	 * @param newMixedObject the newly created object
	 */
	void registerNewObject(Object newMixedObject) {
		delegateMaps.put(newMixedObject, createDelegateMap(newMixedObject));
	}

	/**
	 * <p>Convert a 'map of interfaces to implementing classes', to a 'map of interfaces to instances of
	 * implementing objects'. There must be only one instance per class.
	 */
	private Map<Class<?>, Object> createDelegateMap(Object newMixedObject) {
		synchronized (delegateMaps) {
			Map<Class<?>, Object> delegates = new HashMap<Class<?>, Object>();
			Map<Class<?>, Object> implementationClassToObjectMap = new HashMap<Class<?>, Object>();
			for (Class<?> interfaceClass: interfaceToImplementationMap.keySet()) {
				Class<?> implementationClass = interfaceToImplementationMap.get(interfaceClass);
				if (!implementationClassToObjectMap.containsKey(implementationClass)) {
					Object implementation;
					try {
						implementation = implementationClass.newInstance();
						implementationClassToObjectMap.put(implementationClass, implementation);
					} catch (Exception e) {
						throw new MixinException("Could not create mixin implementation instance", e);
					}
					MixinUtils.handleMixinAwareness(implementation, newMixedObject,
							newMixedObject.getClass().getSuperclass());
				}
				delegates.put(interfaceClass, implementationClassToObjectMap.get(implementationClass));
			}
			return delegates;
		}
	}

	/**
	 * The mixed object
	 */
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		Object delegate = null;
		Class<?> declaringClass = method.getDeclaringClass();
		if (!declaringClass.equals(Object.class)) { // don't intercept Object methods
			Map<Class<?>, Object> delegateMap = delegateMaps.get(obj);
			delegate = delegateMap.get(declaringClass);
		}
		if (delegate != null) {
			return method.invoke(delegate, args);
		} else {
			return proxy.invokeSuper(obj, args);
		}
	}
}
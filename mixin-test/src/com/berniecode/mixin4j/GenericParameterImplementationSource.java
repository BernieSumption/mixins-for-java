package com.berniecode.mixin4j;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <p>An {@link ImplementationSource} that gets implementations from a generic parameter of the
 * mixin type. This type of mixin is useful when the mixin author wants to make the mixin user
 * choose the implementation, e.g. if there are several implementations of a mixin type and no
 * sensible default.
 * 
 * <p>For example, if the mixin type was declared:
 * 
 * <pre>@MixinType(GenericParameter) MyMixinType&lt;T extends MyMixinType&lt;T&gt;&gt;</pre>
 * 
 * <p>Then the mixin base class should attach the mixin like this:
 * 
 * <pre>@MixinBase MyMixedClass implements MyMixinType&lt;MixinImpl&gt;</pre>
 * 
 * <p>Note the constraint on the generic type parameter <code>T extends MyMixinType&lt;T&gt;</code>.
 * This ensures that the generic type argument <code>MixinImpl</code> must implement the
 * <code>MyMixinType</code> interface.
 * 
 * @author Bernard Sumption
 */
//TODO replace requirement for exactly one parameter with requirement for a parameter names IMPL
public class GenericParameterImplementationSource implements ImplementationSource {
	
	public static final String GENERIC_PARAMETER_NAME = "IMPL";

	/**
	 * {@inheritDoc}
	 */
	public Class<?> getImplementation(Class<?> declaredInteface, Class<?> declaringClass) {
		
		for (Type implemented: declaringClass.getGenericInterfaces()) {
			if (implemented instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) implemented;
				if (pt.getRawType().equals(declaredInteface)) {
					return getImplementationFromParameterizedType(declaredInteface, pt);
				}
			}
		}
		throw new MixinException("Class '" + declaringClass.getCanonicalName()
				+ "' does not implement the interface '" + declaredInteface.getCanonicalName() + "'");
	}
	
	/**
	 * Given a reflection object representing the implementation of a mixin interface,
	 * return a Mixin object.
	 * 
	 * For example, Bar&lt;Baz&gt; yields new Mixin(Bar.class, Baz.class)
	 */
	private <T> Class<? extends T> getImplementationFromParameterizedType(Class<T> declaredInteface, ParameterizedType pInterface) {
//		Class mixinType = (Class) pInterface.getRawType();
		
		// check that MixinType has a single type argument that is an implementation of MixinType
		Type[] arguments = pInterface.getActualTypeArguments();
		if (arguments.length == 0) {
			rejectSpec(pInterface, "does not take an argument");
		}
		if (arguments.length > 1) {
			rejectSpec(pInterface, "takes too many arguments");
		}
		if (!(arguments[0] instanceof Class)) {
			// i.e. MixinType<MixinImpl> not MixinType<MixinImpl<T>>
			rejectSpec(pInterface, "the mixin implementation must not be generic");
		}
		Class<?> argument = (Class<?>) arguments[0];
		if (!declaredInteface.isAssignableFrom(argument)) {
			rejectSpec(pInterface, "the mixin implementation '" + argument
					+ "' does not implement the mixin type '" + declaredInteface + "'");
		}
		
		// safe cast because we have already tested that argument is a type that extends declaredInteface
		Class<? extends T> mixinImpl = (Class<? extends T>) argument;
		
		int mods = mixinImpl.getModifiers();
		if (Modifier.isInterface(mods)) {
			rejectSpec(pInterface, "the mixin implementation must be a class, not an interface");
		}
		if (Modifier.isAbstract(mods)) {
			rejectSpec(pInterface, "the mixin implementation must not be abstract");
		}
		
		return mixinImpl;
	}

	private void rejectSpec(ParameterizedType pInterface, String reason) {
		throw new MixinException("Invalid mix specification. The proper form is MixinType<MixinImpl>." +
				" The provided specification - " + pInterface + " - " + reason);
	}

	/**
	 * <p>This class has no functionality in it directly, but is used to qualify parameterised mixin types
	 * the programmer wants to provide the implementation of at runtime.
	 * 
	 * <p>For example, if a mixin type is defined:
	 * 
	 * <pre>{@code @MixinType(implementation = GenericParameterImplementationSource.class)
	 * public class MyMixinType<T>}</pre>
	 * 
	 * <p>Then mixin base classes would be expected to provide an implementation in the parameter T:
	 * 
	 * <pre>{@code @MixinBase
	 * public class MyMixinBase implements MyMixinType<MyMixinImplementation>}</pre>
	 * 
	 * <p>However, a programmer might want to decide the implementation at runtime, in which case they
	 * can plug the parameter with the special value <code>Dynamic</code>.
	 * 
	 * <pre>{@code
	 * import com.berniecode.mixin.GenericParameterImplementationSource.Dynamic; 
	 * <b></b>@MixinBase
	 * public class MyMixinBase implements MyMixinType<Dynamic>}</pre>
	 * 
	 * <p>The mixin implementation can then be provided at runtime by directly using a {@link Mixer}.
	 * 
	 * @author Bernard Sumption
	 *
	 */
	public static final class Dynamic {}
	
	/**
	 * <p>This class has no functionality, but is used to qualify parameterised mixin implementations.
	 * 
	 * <p>{@link GenericParameterImplementationSource} requires that mixin types have a generic type
	 * parameter so that mixin base classes can specify an implementation when they request the mixin.
	 * However, this generic type parameter is useless on the mixin implementation, so the space can
	 * be plugged with this class.
	 * 
	 * <p>For example, if a mixin type is defined:
	 * 
	 * <pre>{@code @MixinType(implementation = GenericParameterImplementationSource.class)
	 * public class MyMixinType<T>}</pre>
	 * 
	 * <p>Then the mixin implementation class could be defined:
	 * 
	 * <pre>{@code
	 * import com.berniecode.mixin.GenericParameterImplementationSource.Implementation; 
	 * public class MyMixinImplementation implements MyMixinType<Implementation>}</pre>
	 * 
	 * @author Bernard Sumption
	 *
	 */
	public static final class IMPL {}


}

@interface Foo {
	
}
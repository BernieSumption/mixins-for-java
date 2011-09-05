package com.berniecode.mixin4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>Used to label a class as a mixin base.
 * 
 * <p>Only classes labelled as mixin base classes can have mixins applied to them.
 * 
 * @author Bernard Sumption
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MixinBase {

	/**
	 * <p>The name of a class that implements the Mixer interface.
	 * 
	 * <p>Overriding this is an advanced technique to be used if you need a non-standard
	 * mixing method. Normally, the default should be fine.
	 */
	public Class<? extends Mixer> mixerClass() default ClassGeneratingMixer.class;
}
package com.berniecode.mixin4j.test.aware;

import com.berniecode.mixin4j.MixinSupport;

/**
 * <p>Demonstrates the use of the MixinAware interface
 * 
 * @author Bernard Sumption
 *
 */
public class MixinAwareTestRunner {
	
	private static MixinSupport mixins = MixinSupport.getSingleton();

	public static void main(String[] args) {
		
		RandomNumberSequence sequence = mixins.newInstanceOf(
				RandomNumberSequence.class, new Object[] {10, 50, 100});

		System.out.println("Result of getXml():");
		System.out.println(sequence.getXml());
	}

}




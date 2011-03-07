package com.berniecode.mixin4j.test.aware;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.berniecode.mixin4j.MixinBase;

@MixinBase
/**
 * A sequence of random numbers
 */
public abstract class RandomNumberSequence implements DumpToXmlMixin<DumpIteratorToXml>, Iterator {
	
	private int remaining;
	private int min;
	private int max;
	
	public RandomNumberSequence(int length, int min, int max) {
		this.remaining = length;
		this.min = min;
		this.max = max;
	}

	public boolean hasNext() {
		return remaining > 0;
	}

	public Object next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		remaining--;
		return min + (int) (Math.random() * (max - min));
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}

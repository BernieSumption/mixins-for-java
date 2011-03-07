package com.berniecode.mixin4j.test.aware;

import java.util.Iterator;

import com.berniecode.mixin4j.MixinAware;
import com.berniecode.mixin4j.GenericParameterImplementationSource.IMPL;

/**
 * <p>An implementation of {@link DumpToXmlMixin} that uses the {@link MixinAware} interface to
 * ensure that it is only mixed onto an iterator.
 * 
 * @author Bernard Sumption
 *
 */
public class DumpIteratorToXml implements DumpToXmlMixin<IMPL>, MixinAware<Iterator<?>> {

	private Iterator<?> mixinBase;

	/**
	 * {@inheritDoc}
	 */
	public void setMixinBase(Iterator<?> mixinBase) {
		this.mixinBase = mixinBase;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<items>\n");
		while (mixinBase.hasNext()) {
			sb.append("\t<item value=\"" + mixinBase.next() + "\">\n");
		}
		sb.append("</items>");
		return sb.toString();
	}
	
}
package com.berniecode.mixin4j.test.aware;

import com.berniecode.mixin4j.GenericParameterImplementationSource;
import com.berniecode.mixin4j.MixinType;

/**
 * A mixin that ads the ability to dump the object into an XML format
 * 
 * @author Bernard Sumption
 */
@MixinType(implementation = GenericParameterImplementationSource.class)
public interface DumpToXmlMixin<IMPL> {
	/**
	 * Return an XML representation of this object
	 */
	public String getXml();
}
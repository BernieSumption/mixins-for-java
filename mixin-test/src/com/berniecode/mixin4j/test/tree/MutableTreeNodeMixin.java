package com.berniecode.mixin4j.test.tree;

import com.berniecode.mixin4j.*;

import javax.swing.tree.MutableTreeNode;

/**
 * <p>A mixin type that adds tree functionality to any class it is mixed in to.
 * 
 * <p>Since it is based on javax.swing.tree.MutableTreeNode, any class can be organised
 * in a hierarchy simply by requesting this mixin
 * 
 * @author Bernard Sumption
 */
@MixinType( implementation = MutableTreeNodeMixinImpl.class)
public interface MutableTreeNodeMixin extends MutableTreeNode {
	
}

package com.berniecode.mixin4j.test.tree;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import com.berniecode.mixin4j.MixinAware;

/**
 * Implementation of MutableTreeNode for {@link MutableTreeNodeMixin}
 * 
 * @author Bernard Sumption
 */
public class MutableTreeNodeMixinImpl implements MutableTreeNodeMixin, MixinAware<MutableTreeNode> {
	
	private TreeNode parent;
	
	Vector<TreeNode> children = new Vector<TreeNode>();

	private MutableTreeNode mixinBase;

	/**
	 * <p>Remove this node from its parent
	 * 
	 * @throws NullPointerException if the node has no parent
	 */
	public void removeFromParent() {
		if (parent != null) {
			((MutableTreeNode) getParent()).remove(mixinBase);
			parent = null;
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setParent(MutableTreeNode newParent) {
		if (!newParent.equals(parent)) {
			if (parent != null) {
				removeFromParent();
			}
			parent = newParent;
			newParent.insert(mixinBase, 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void insert(MutableTreeNode child, int index) {
		if (child.equals(mixinBase)) {
			throw new IllegalArgumentException("Can't add a TreeNode as its own child");
		}
		if (children.remove(child));
		children.add(index, child);
		child.setParent(mixinBase);
	}
	
	/**
	 * <p>Removes the child at the specified index
	 * 
	 * @param index the index of the child to remove
	 */
	public void remove(int index) {
		remove((MutableTreeNode) children.get(index));
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(MutableTreeNode node) {
		children.remove(node);
		
	}

	/**
	 * {@inheritDoc}
	 */
	public TreeNode getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getChildCount() {
		return children.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public Enumeration<?> children() {
		return children.elements();
	}

	/**
	 * Always return true: this implementation has no restrictions on which nodes can have children,
	 * we'll leave that to the alternative implementation ChineseGovernmentTreeNodeMixinImpl.
	 */
	public boolean getAllowsChildren() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isLeaf() {
		return children.size() == 0;
	}

	/**
	 * Not implemented - this method has no effect when called, it is present because it is required
	 * by the MutableTreeNode interface.
	 */
	public void setUserObject(Object object) {
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMixinBase(MutableTreeNode mixinBase) {
		this.mixinBase = mixinBase;
	}
	
	public String toString() {
		return "MutableTreeNodeMixin for " + mixinBase;
	}
	
}

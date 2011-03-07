package com.berniecode.mixin4j.test.tree;

import com.berniecode.mixin4j.MixinBase;
import com.berniecode.mixin4j.MixinSupport;

/**
 * {@link javax.swing.tree.TreeNode}
 * @author Bernard Sumption
 *
 */
@MixinBase
public abstract class Employee implements MutableTreeNodeMixin {
	
	private String name;

	/**
	 * <p>Constructor
	 * 
	 * @param name the employee's name
	 * @param lineManager the employee's direct manager, or null if the employee has no superiors
	 */
	protected Employee(String name) {
		this.name = name;
	}
	
	/**
	 * Create a new employee object
	 * @param name
	 */
	public static Employee create(String name) {
		return MixinSupport.getSingleton().newInstanceOf(Employee.class, new Object[] {name});
	}
	
	/**
	 * Create a new employee object with a number of direct reports
	 * @param name
	 */
	public static Employee create(String name, Employee... directReports) {
		Employee employee = MixinSupport.getSingleton().newInstanceOf(Employee.class, new Object[] {name});
		for (Employee directReport: directReports) {
			directReport.setParent(employee);
		}
		return employee;
	}
	
	/**
	 * @return the employee's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the employee's name
	 * @param name the new name
	 */
	public void setName(String name) {
		if (name == null) throw new NullPointerException("name must not be null");
		this.name = name;
	}
	
	public String toString() {
		return name;
	}

}

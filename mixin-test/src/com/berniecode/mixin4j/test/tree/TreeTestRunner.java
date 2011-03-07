package com.berniecode.mixin4j.test.tree;

import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * 
 * @author Bernard Sumption
 *
 */
public class TreeTestRunner {

	/**
	 * @param args
	 * @throws NoSuchMethodException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		

		Employee theBoss = Employee.create("The CEO",
				Employee.create("Director of marketing"),
				Employee.create("Director of accounting",
						Employee.create("Troll 1"),
						Employee.create("Troll 2"),
						Employee.create("Troll 3")
				),
				Employee.create("Director of manufacturing",
						Employee.create("First blind mouse"),
						Employee.create("Second blind mouse"),
						Employee.create("Third blind mouse")
				)
		);
		
		theBoss.getChildAt(0);
		
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }


        //Create and set up the window.
        JFrame frame = new JFrame("Employees as a tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new TreeEditorUI(theBoss));
        frame.pack();
        frame.setLocation(new Point(100, 100));
        frame.setVisible(true);
	}

}

interface I1 {
	void foo();
}

interface I2 extends I1 {
	void other();
}

abstract class CA implements I1 {
	
}

class C1 implements I2 {

	@Override
	public void other() {
		System.out.println("Other!");
	}

	@Override
	public void foo() {
		System.out.println("Foo!");
	}
	
}
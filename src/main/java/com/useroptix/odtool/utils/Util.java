package com.useroptix.odtool.utils;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.useroptix.odtool.to.EPIconsEnum;

public class Util {

	public static JComponent createHorizontalSeparator() {
        JSeparator x = new JSeparator(SwingConstants.HORIZONTAL);
        x.setPreferredSize(new Dimension(450,3));
        return x;
    }
	
	public static JButton getDecoratedButton(JButton button, String fileName) {
		button.setBorderPainted(false);
		button.setBorder(null);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//		//button.setFocusable(false);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setContentAreaFilled(false);
		button.setIcon(new ImageIcon(ClassLoader.getSystemResource(fileName)));
		button.setPreferredSize(new Dimension(120,30));
		return button;
	}
	
	/***
	 * To return image path for the given endpoint name
	 * @param endPointName
	 * @return
	 */
	public static Icon getImageIconForEndPoint(String endPointName) {
		if(endPointName == null || endPointName.isEmpty()) {
			return null;
		}
		for (EPIconsEnum iconEnum : EPIconsEnum.values()) {
    		if(endPointName.equalsIgnoreCase(iconEnum.getValue())) {
    			return new ImageIcon(ClassLoader.getSystemResource(iconEnum.getImagePath()));
    		}
    	}
		return null;
	}
	
	public static Component getTopLevelAncestor(Component c) {
	    while (c != null) {
	      if (c instanceof Window || c instanceof JFrame)
	        break;
	      c = c.getParent();
	    }
	    return c;
	  }
}

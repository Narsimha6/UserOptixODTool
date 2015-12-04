package com.useroptix.odtool.screens;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.AbstractBorder;

public class BorderCellRenderer extends DefaultListCellRenderer implements ListCellRenderer<Object> {
	   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	AbstractBorder border;
	  
	   public BorderCellRenderer(AbstractBorder border) {
	      this.border = border;
	   }
	  
	   public Component getListCellRendererComponent(
	      @SuppressWarnings("rawtypes") JList list,
	      Object value,            // value to display
	      int index,               // cell index
	      boolean isSelected,      // is the cell selected
	      boolean cellHasFocus)    // the list and the cell have the focus
	   {
	      Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	  
	      ((JComponent) c).setBorder(border);
	  
	      return c;
	   }
	}

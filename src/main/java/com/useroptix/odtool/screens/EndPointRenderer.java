package com.useroptix.odtool.screens;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.useroptix.odtool.to.EPIconsEnum;

public class EndPointRenderer extends JLabel implements ListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2073363655284991913L;
	private JLabel label;
	public EndPointRenderer() {
		label = new JLabel();
		label.setOpaque(true);
//		setOpaque(true);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		if(value != null) {
			String endPointName = String.valueOf(value);
			label.setText(endPointName);
			ImageIcon imgIcon = (ImageIcon) getImageIconForEndPoint(endPointName);
			if(imgIcon != null) {
				label.setIcon(imgIcon);
			}
			label.setIconTextGap(10);
			label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		}
		if (isSelected) {
			label.setBackground(Color.white);
			label.setForeground(Color.blue);
		} else {
			label.setBackground(list.getBackground());
			label.setForeground(list.getForeground());
		}
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setOpaque(true);
		return label;
	}

	/***
	 * To return image path for the given endpoint name
	 * @param endPointName
	 * @return
	 */
	public Icon getImageIconForEndPoint(String endPointName) {
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
} 
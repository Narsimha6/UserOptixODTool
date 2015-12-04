package com.useroptix.odtool.components;

import java.awt.Graphics;

import javax.swing.JList;

import com.useroptix.odtool.screens.EndPointRenderer;

public class UserOptixList<String> extends JList<String> {
	public UserOptixList() { 
		super();
//		this.setUI(new UOListUI());
		EndPointRenderer renderer = new EndPointRenderer();
		this.setCellRenderer(renderer);
	}
	public UserOptixList(String[] args) { 
		super(args);
	}
	
	protected void paintBorder(Graphics g) {
//		g.setColor(Color.red);
//		g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 35, 35);
	}
}
package com.useroptix.odtool.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
 
/**
 * 
 *@author narasimhar
 */
public class IconTextField extends JTextField {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Shape shape;
    private Icon icon;
    private Insets dummyInsets;
 
    public IconTextField(){
        super();
        this.icon = null;
        setOpaque(false); 
        Border border = UIManager.getBorder("TextField.border");
        JTextField dummy = new JTextField();
        this.dummyInsets = border.getBorderInsets(dummy);
    }
    public IconTextField(Integer size){
    	super(size);
    	this.icon = null;
    	setOpaque(false); 
    	Border border = UIManager.getBorder("TextField.border");
    	JTextField dummy = new JTextField();
    	this.dummyInsets = border.getBorderInsets(dummy);
    }
 
    public void setIcon(Icon icon){
        this.icon = icon;
    }
 
    public Icon getIcon(){
        return this.icon;
    }
 
    @Override
    protected void paintComponent(Graphics g) {
    	g.setColor(getBackground());
		g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
		super.paintComponent(g);
//        super.paintComponent(g);
 
        int textX = 2;
 
        if(this.icon!=null){
            int iconWidth = icon.getIconWidth();
            int iconHeight = icon.getIconHeight();
            int x = dummyInsets.right + 5;//this is our icon's x
            textX = x+iconWidth+2; //this is the x where text should start
            int y = (this.getHeight() - iconHeight)/2;
            icon.paintIcon(this, g, x, y);
        }
 
        setMargin(new Insets(2, textX, 2, 2));
    }
    
    protected void paintBorder(Graphics g) {
		g.setColor(Color.lightGray);
		g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
	}
	public boolean contains(int x, int y) {
		if (shape == null || !shape.getBounds().equals(getBounds())) {
			shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15);
		}
		return shape.contains(x, y);
	}
 }
package com.useroptix.odtool.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.JButton;

public class RoundedButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8818223104225410415L;
	private Icon icon=null; 
	/**
	 * Creates a button with an icon.
	 *
	 * @param icon  the Icon image to display on the button
	 */
	public RoundedButton(Icon icon) {
		super(null, icon);
		this.icon = icon;
		setBorderPainted(false);
		setBorder(null);
		//setFocusable(false);
		setMargin(new Insets(0, 0, 0, 0));
//		setContentAreaFilled(false);
		setIcon(icon);
//		setRolloverIcon(icon);
//		setPressedIcon(icon);
//		setDisabledIcon(icon);
	}

	public RoundedButton(String label) {
		super(label);
		// These statements enlarge the button so that it 
		// becomes a circle rather than an oval.
		Dimension size = getPreferredSize();
		size.width = size.height = Math.max(size.width, 
				size.height);
		setPreferredSize(size);

		// This call causes the JButton not to paint 
		// the background.
		// This allows us to paint a round background.
		setContentAreaFilled(false);
	}

	// Paint the round background and label.
	/*protected void paintComponent(Graphics g) {
		if (getModel().isArmed()) {
			// You might want to make the highlight color 
			// a property of the RoundButton class.
			g.setColor(Color.lightGray);
		} else {
			g.setColor(getBackground());
		}
		g.fillOval(0, 0, getSize().width-1, 
				getSize().height-1);

		// This call will paint the label and the 
		// focus rectangle.
		super.paintComponent(g);
	}
*/
	// Paint the border of the button using a simple stroke.
	protected void paintBorder(Graphics g) {
		g.setColor(getForeground());
		g.drawRoundRect(0,0,getWidth()-1,getHeight()-1,18,18);
	}

	public void paint(Graphics g) { 
		this.setContentAreaFilled(false); 
		this.setBorderPainted(false);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paint(g);
//		g2d.setColor(new Color(0xDD,0xDD,0xDD,0xFF)); 
		g2d.setColor(Color.darkGray); 
		g2d.fillRoundRect(0,0,getWidth(),getHeight(),18,18);
		g2d.drawRoundRect(0,0,getWidth()-1,getHeight()-1,18,18);

		FontRenderContext frc = new FontRenderContext(null, false, false);
		Rectangle2D r = getFont().getStringBounds(getText(), frc);
		float xMargin = (float)(getWidth()-r.getWidth())/2;
		float yMargin = (float)(getHeight()-getFont().getSize())/2;
		// Draw the text in the center 
		g2d.setColor(Color.WHITE);
		g2d.drawString(getText(),xMargin, (float)getFont().getSize()+yMargin);
	}

	// Hit detection.
	Shape shape;
	public boolean contains(int x, int y) {
		// If the button has changed size, 
		// make a new shape object.
		if (shape == null || 
				!shape.getBounds().equals(getBounds())) {
			shape = new Ellipse2D.Float(0, 0, 
					getWidth(), getHeight());
		}
		return shape.contains(x, y);
	}
}
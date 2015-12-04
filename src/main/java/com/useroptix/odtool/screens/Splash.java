package com.useroptix.odtool.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

/**
 * Splash Form Component.
 * 
 *
 * @author narasimhar
 */
public class Splash extends JFrame {

	/**
	 * serialVersionUID 
	 */
	private static final long serialVersionUID = 7724277653679133347L;
	/**
	 * Title of Splash form.
	 */
	private final String title;

	/**
	 * Create splash form
	 * 
	 * @param title title of splash form for taskbar
	 */
	public Splash(String title) {
		this.title = title;

		initComponents();
	}

	/**
	 * Initialize the for welcoming progress bar components
	 */
	private void initComponents() {
		setTitle(title);
		setResizable(false);
		setUndecorated(true);
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new MigLayout());
		labelPanel.setBorder(new LineBorder(Color.blue, 1));
		JLabel imgSplash = new JLabel(new ImageIcon(ClassLoader.getSystemResource("useroptix.png")));
		JLabel projectLabel = new JLabel(" ");
		projectLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 2));

		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setIndeterminate(true);
		progressBar.setPreferredSize(new Dimension(7, 10));
		progressBar.setBackground(new Color(165, 196, 238));
		progressBar.setForeground(new Color(243, 179, 69));
		labelPanel.add(imgSplash, "wrap, align center");
		labelPanel.add(projectLabel);
		getContentPane().add(labelPanel, BorderLayout.CENTER);
		getContentPane().add(progressBar, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
	}
}

package com.useroptix.odtool.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import com.useroptix.odtool.to.ApplicationDataModel;
import com.useroptix.odtool.to.UserViewEnum;
import com.useroptix.odtool.utils.ApplicationContextProvider;
import com.useroptix.odtool.utils.Util;

public class CompletePanel extends JPanel {
	private JPanel bottomPanel;
	private JPanel completePanel;

	private JLabel projectLabel;
	private JButton doAnotherButton;
	private JButton finishButton;
	private JSeparator separator;

	private JComponent topLineSeparator;

	private ApplicationDataModel appDataModel;

	public CompletePanel() {

		//		this.add(doAnotherButton, "gapbefore 300");
		appDataModel = ApplicationContextProvider.getApplicationContext()
				.getBean("appDataModel", ApplicationDataModel.class);
		appDataModel.setUserView(UserViewEnum.CONFIGURATION_COMPLETE.getValue());
		initComponents();
	}

	private void initComponents() {

		setBackground(Color.white);
		this.setLayout(new MigLayout());
		doAnotherButton = new JButton();
		doAnotherButton .setBorderPainted(Boolean.FALSE);
		doAnotherButton = Util.getDecoratedButton(doAnotherButton, "do-another.jpg");
		doAnotherButton.setVisible(Boolean.TRUE);

		finishButton = new JButton();
		finishButton .setBorderPainted(Boolean.FALSE);
		finishButton = Util.getDecoratedButton(finishButton, "finish.jpg");

		ImageIcon completeIcon = new ImageIcon(ClassLoader.getSystemResource("complete_header.jpg"));
		projectLabel = new JLabel();
		projectLabel.setIcon(completeIcon);

		completePanel = new JPanel();
		completePanel.setLayout(new MigLayout());
		completePanel.setBackground(Color.white);

		completePanel.add(projectLabel, "wrap");
		topLineSeparator = Util.createHorizontalSeparator();
		topLineSeparator.setPreferredSize(new Dimension(500,3));
		completePanel.add(topLineSeparator, "wrap");

		JLabel completeLabel = new JLabel("Configuration Complete");
		completeLabel.setForeground(Color.blue);
		completePanel.add(completeLabel, "gap 150 0 80 30, wrap");

		JLabel tickIconLabel = new JLabel(new ImageIcon(ClassLoader.getSystemResource("tick.jpg")));
		completePanel.add(tickIconLabel, "gap 150 100 50 80, wrap");
		this.add(completePanel,"wrap");

		bottomPanel = new JPanel();
		bottomPanel.setLayout(new MigLayout());
		bottomPanel.setBackground(Color.white);
		bottomPanel.add(doAnotherButton, "gapbefore 300");
		bottomPanel.add(finishButton, "gapbefore 5");
		this.add(bottomPanel);
		finishButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		doAnotherButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(appDataModel.getUserView());
				if(appDataModel.getUserView().equalsIgnoreCase(
						UserViewEnum.CONFIGURATION_COMPLETE.getValue())) {
					loadEPSelectionScreen();
				}
			}
		});
	}

	private void loadEPSelectionScreen() {
		this.remove(projectLabel);
		this.remove(completePanel);
		this.remove(bottomPanel);
		this.remove(bottomPanel);
		System.out.println("ABCDEFG");
		EPSelectionPanel epsPanel = new EPSelectionPanel();
		this.add(epsPanel);
		System.out.println("XASJHASDG");
		this.updateUI();
	}
}

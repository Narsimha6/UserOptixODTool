package com.useroptix.odtool.screens;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/*
 * This class is for Admin Tabbed pane
 */
public class AdminTabbedPane extends JFrame {

	/**
	 * Serialization version Id
	 */
	private static final long serialVersionUID = -197191958982947894L;
	private	JTabbedPane tabbedPane;
	private	JPanel orgPanel;
	private	JPanel userPanel;
	private	JPanel projectPanel;

	/***
	 * To initialize the components
	 */
	public AdminTabbedPane() {

		setTitle( "Tabbed Pane Application" );
		setSize( 300, 200 );
		setBackground( Color.gray );

		JPanel topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		getContentPane().add( topPanel );

		// Create the tab pages
		createOrganization();
		createUser();
		createProject();

		// Create a tabbed pane
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab( "Organization", orgPanel );
		tabbedPane.addTab( "User", userPanel );
		tabbedPane.addTab( "Project", projectPanel );
		topPanel.add( tabbedPane, BorderLayout.CENTER );
	}
	
	/***
	 * This will create UI for creating the organization
	 */
	public void createOrganization() {
//		"name":"orgname",
//		"address1":"address1",
//		"address2":"address2",
//		"city":"city",
//		"state":"state",
//		"postal_code":"postal_code",
//		"country":"US"
		orgPanel = new JPanel();
		orgPanel.setLayout( null );

		JLabel label1 = new JLabel( "Name:" );
		label1.setBounds( 10, 15, 150, 20 );
		orgPanel.add( label1 );

		JTextField field = new JTextField();
		field.setBounds( 10, 35, 150, 20 );
		orgPanel.add( field );

		JLabel label2 = new JLabel( "Password:" );
		label2.setBounds( 10, 60, 150, 20 );
		orgPanel.add( label2 );

		JPasswordField fieldPass = new JPasswordField();
		fieldPass.setBounds( 10, 80, 150, 20 );
		orgPanel.add( fieldPass );
	}

	/***
	 * This will create UI for creating the user
	 */
	public void createUser() {
//		userPanel = new JPanel();
//		userPanel.setLayout( new BorderLayout() );
//
//		userPanel.add( new JButton( "North" ), BorderLayout.NORTH );
//		userPanel.add( new JButton( "South" ), BorderLayout.SOUTH );
//		userPanel.add( new JButton( "East" ), BorderLayout.EAST );
//		userPanel.add( new JButton( "West" ), BorderLayout.WEST );
//		userPanel.add( new JButton( "Center" ), BorderLayout.CENTER );
	}

	/***
	 * This will create UI for creating the Project
	 */
	public void createProject() {
//		projectPanel = new JPanel();
//		projectPanel.setLayout( new GridLayout( 3, 2 ) );
//
//		projectPanel.add( new JLabel( "Field 1:" ) );
//		projectPanel.add( new TextArea() );
//		projectPanel.add( new JLabel( "Field 2:" ) );
//		projectPanel.add( new TextArea() );
//		projectPanel.add( new JLabel( "Field 3:" ) );
//		projectPanel.add( new TextArea() );
	}

	// Main method to get things started
	public static void main( String args[] ) {
		// Create an instance of the test application
		AdminTabbedPane mainFrame	= new AdminTabbedPane();
		mainFrame.setVisible( true );
	}
}
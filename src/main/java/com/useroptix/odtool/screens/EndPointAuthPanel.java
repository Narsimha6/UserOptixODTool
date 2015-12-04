package com.useroptix.odtool.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.useroptix.odtool.ObjectToolMain;
import com.useroptix.odtool.components.RoundedPanel;
import com.useroptix.odtool.components.RoundedPasswordField;
import com.useroptix.odtool.components.RoundedTextField;
import com.useroptix.odtool.service.EndPointConnectionService;
import com.useroptix.odtool.to.ApplicationDataModel;
import com.useroptix.odtool.to.UserViewEnum;
import com.useroptix.odtool.utils.ApplicationContextProvider;
import com.useroptix.odtool.utils.Util;

/***
 * EndPoint Authentication Panel
 * @author narasimhar
 *
 */
public class EndPointAuthPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// Variables declaration                   
	private JPanel loginPanel;
	private JPanel buttonPanel;
	private JPanel loginOptionPanel;
	private JPanel optionalHeaderLoginPanel;
	private JButton testConnBut;
	private JButton nextButton;
	private static JButton backButton; 
	private JButton doAnotherButton;
	private JButton btnBrowse;
	private JButton connectionButtion;
	private JLabel endPointLabel;

	private RoundedTextField userNameTxt = null;
	private RoundedPasswordField passwordField = null;
	private RoundedPasswordField apiKeyField = null;
	private RoundedTextField wsdlField = null;
	private JRadioButton loginNormal = new JRadioButton("Login Normally");
	private JRadioButton loginSandBox = new JRadioButton("Login to SalesForce.com SandBox");
	private JRadioButton loginWSDL = new JRadioButton("Login to specified partner WSDL login URL");
	private JLabel wsdlURLLabel = new JLabel("WSDL File Location : ");
	// Optional Header fields
	private JLabel optionalHeaderLabel;
	private RoundedTextField optionalUserNameTxt = null;
	private RoundedPasswordField optionalPasswordField = null;
	private ButtonGroup butGroup = new ButtonGroup();

	private JSeparator topSeparator;
	private JSeparator midSeparator;
	private ApplicationDataModel appDataModel;

	private JFrame clientGUI;
	private JPanel loginInnerPanel;
	private JPanel bottomPanel;
	private JPanel completePanel;
	private static String appTitle = "UserOptix Quick Start Tool";
	private JLabel projectLabel = null;
	private JLabel endPointNameLabel = null;
	private JComponent topLineSeparator; 
	private Window window; 
	private EndPointConnectionService endPointConnService;

	public EndPointAuthPanel() {
		super();
		doAnotherButton = new JButton();
		doAnotherButton = Util.getDecoratedButton(doAnotherButton, "do-another.jpg");
		doAnotherButton.setVisible(Boolean.FALSE);

		nextButton = new JButton();
		nextButton = Util.getDecoratedButton(nextButton, "next.jpg");

		//		this.add(doAnotherButton, "gapbefore 300");
		this.setBackground(Color.white);
		appDataModel = ApplicationContextProvider.getApplicationContext()
				.getBean("appDataModel", ApplicationDataModel.class);
		appDataModel.setUserView(UserViewEnum.ENDPOINT_AUTHENTICATION.getValue());
		JFrame clientGUI = ObjectToolMain.getTopFrame();
		initComponents();
		clientGUI.setSize(600, 650);
		clientGUI.revalidate();
		clientGUI.pack();
	}

	/***
	 * Initialize the components
	 */
	private void initComponents() {
		ImageIcon credentialsIcon = new ImageIcon(ClassLoader.getSystemResource("credentials_header.jpg"));
		projectLabel = new JLabel();
		projectLabel.setIcon(credentialsIcon);
		loginPanel = new JPanel(new MigLayout());
		loginPanel.setBackground(Color.white);
		buttonPanel = new JPanel(new MigLayout());
		buttonPanel.setBackground(Color.white);
		loginOptionPanel= new JPanel(new MigLayout());
		loginOptionPanel.setBackground(Color.white);
		optionalHeaderLoginPanel= new JPanel(new MigLayout());

		loginInnerPanel = new JPanel();

		String endPointName = appDataModel.getSelectedEndPoint().toUpperCase();
		Icon epIcon = Util.getImageIconForEndPoint(endPointName);
		endPointNameLabel = new JLabel(endPointName, epIcon, JLabel.CENTER);
		endPointNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		this.setBackground(Color.white);
		this.setLayout(new MigLayout());
		this.add(projectLabel, "wrap");
		topLineSeparator = (JSeparator) Util.createHorizontalSeparator();
		topLineSeparator.setPreferredSize(new Dimension(500,3));
		this.add(topLineSeparator, "wrap");

		this.add(endPointNameLabel, "wrap");
		buildLoginPanel();
		topSeparator = (JSeparator) Util.createHorizontalSeparator();
		topSeparator.setPreferredSize(new Dimension(500,3));
		this.add(loginPanel, "wrap");
		this.add(topSeparator, "wrap");

		buildLoginOptionPanel();
		midSeparator = (JSeparator) Util.createHorizontalSeparator();
		midSeparator.setPreferredSize(new Dimension(500,3));
		this.add(loginOptionPanel, "wrap");
		this.add(midSeparator, "wrap");

		buildOptionalHeaderLoginPanel();
		this.add(optionalHeaderLoginPanel, "wrap");

		backButton = new JButton();
		backButton = Util.getDecoratedButton(backButton, "back.jpg");
		buttonPanel.add(backButton, "gapbefore 250");
		testConnBut = new JButton();
		testConnBut = Util.getDecoratedButton(testConnBut, "test_connection.jpg");
		testConnBut.setBackground(Color.LIGHT_GRAY);
		buttonPanel.add(testConnBut, "wrap");
		this.add(buttonPanel);

		backButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				loadEPSectionPanel();
			}
		});
		testConnBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
			
				System.out.println("Test connection button pressed....");
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() { 
						final JProgressBar progressBar  = new JProgressBar(0, 10);;
						final int MAXIMUM = 100;
						final RoundedPanel panel = new RoundedPanel();
						final JDialog dialog = new JDialog();
						String password = new String(passwordField.getPassword());
						String apiKey = new String(apiKeyField.getPassword());
						JLabel progressLabel = new JLabel("<html>Testing connection progress</html></br>");
						try {
						appDataModel.setSalesForceUserName(userNameTxt.getText());
						appDataModel.setSalesForcePassword(password);
						//String returnVal = new TestConnetionDialog(userNameTxt.getText(), password+apiKey).doJob();
						//Thread.sleep(millis);
						
						progressBar.setIndeterminate(true);
						progressBar.setBackground(Color.WHITE);
						progressBar.setForeground(new Color(243, 179, 69));
						//panel.setPreferredSize(new Dimension(300, 300));
						panel.setLayout(new BorderLayout(20, 20));
						//panel.add(msgLabel, BorderLayout.LINE_END);
						progressLabel.setForeground(Color.WHITE);
						panel.add(progressLabel, BorderLayout.PAGE_START);
						panel.add(progressBar, BorderLayout.CENTER);
						panel.setBorder(BorderFactory.createEmptyBorder(11, 11, 11, 11));
						
						dialog.setUndecorated(true) ;
						panel.setBackground(Color.WHITE);
						dialog.setBackground(Color.WHITE);
						dialog.getContentPane().add(panel);
						dialog.setResizable(false);
						dialog.setSize(120, dialog.getHeight());
						dialog.setLocationRelativeTo(null);
						dialog.setAlwaysOnTop(false);
						dialog.setVisible(true);
						dialog.pack();
						dialog.revalidate();
						dialog.repaint();
						dialog.setVisible(Boolean.TRUE);
					} catch(Exception e) {
						e.printStackTrace();
					}
						Executor executor = java.util.concurrent.Executors.newSingleThreadExecutor();
						((ExecutorService) executor).submit(new Runnable() { 
							public void run() { 
							//To get EP Connection
								 testEPConnection();
						}
						/**
						 * testEPConnection
						 * On connection success close the progress bar
						 */
						private void testEPConnection() {
				              String connectionStatus = null ;
							// Close the dialog
							endPointConnService = ApplicationContextProvider.getApplicationContext()
									.getBean("endPointConnectionServiceImpl", EndPointConnectionService.class);
							try {
								PartnerConnection connection = endPointConnService.connectToSalesForce(
										userNameTxt.getText(), password+apiKey, "D5xgn57Cd4bXdmYT2YAbgp1sR");
								if(connection != null) {
									dialog.dispose();
									/*JOptionPane optionPane = new JOptionPane("Connection Successful"
											, JOptionPane.INFORMATION_MESSAGE, JOptionPane.PLAIN_MESSAGE);
									JDialog dialog = optionPane.createDialog(panel, "Endpoint Connection Status");
									dialog.setVisible(true);*/                                            
									panel.remove(progressBar);
									panel.remove(progressLabel);
									JPanel connPanel = new JPanel();
									connPanel.setBorder(null);
									connPanel.setBackground(Color.WHITE);
									final JDialog connDialog = new JDialog();
									connDialog.setUndecorated(true) ;
									connectionButtion = new JButton();
									connectionButtion.setBorderPainted(false);
									connectionButtion.setBorder(null);
									connectionButtion.setCursor(new Cursor(Cursor.HAND_CURSOR));
//									//button.setFocusable(false);
									connectionButtion.setMargin(new Insets(0, 0, 0, 0));
									connectionButtion.setBackground(Color.white);
									connectionButtion.setContentAreaFilled(false);
									connectionButtion.setIcon(new ImageIcon(ClassLoader.getSystemResource("connection-successful.jpg")));
									//connectionButtion.setPreferredSize(new Dimension(200,30));
									//connectionButtion = Util.getDecoratedButton(connectionButtion, "connection-successful.png");
									connPanel.add(connectionButtion);
									connDialog.setBackground(Color.RED);
									connDialog.getContentPane().add(connPanel);
									connDialog.setResizable(false);
									connDialog.setSize(120, dialog.getHeight());
									connDialog.setLocationRelativeTo(null);
									connDialog.setAlwaysOnTop(false);
									connDialog.setVisible(true);
									connDialog.pack();
									connDialog.revalidate();
									connDialog.repaint();
									connDialog.setVisible(Boolean.TRUE);
									connectionButtion.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											connDialog.dispose();
											attachConfigPanel();
										}
									});
								} else {
									dialog.dispose();
									final JDialog connDialog = new JDialog();
									/*JOptionPane optionPane = new JOptionPane("Connection Successful"
											, JOptionPane.INFORMATION_MESSAGE, JOptionPane.PLAIN_MESSAGE);
									JDialog dialog = optionPane.createDialog(panel, "Endpoint Connection Status");
									dialog.setVisible(true);*/                                            
									panel.remove(progressBar);
									panel.remove(progressLabel);
									JPanel connPanel = new JPanel();
									connPanel.setBorder(null);
									connDialog.setUndecorated(true) ;
									connectionButtion = new JButton();
									connectionButtion.setBorderPainted(false);
									connectionButtion.setBorder(null);
									connectionButtion.setCursor(new Cursor(Cursor.HAND_CURSOR));
//									//button.setFocusable(false);
									connectionButtion.setMargin(new Insets(0, 0, 0, 0));
									connectionButtion.setBackground(Color.white);
									connectionButtion.setContentAreaFilled(false);
									connectionButtion.setIcon(new ImageIcon(ClassLoader.getSystemResource("connection-failed.png.jpg")));
									//connectionButtion.setPreferredSize(new Dimension(200,30));
									//connectionButtion = Util.getDecoratedButton(connectionButtion, "connection-successful.png");
									connPanel.add(connectionButtion);
									connDialog.setBackground(Color.WHITE);
									connDialog.getContentPane().add(connPanel);
									connDialog.setResizable(false);
									connDialog.setSize(120, dialog.getHeight());
									connDialog.setLocationRelativeTo(null);
									connDialog.setAlwaysOnTop(false);
									connDialog.setVisible(true);
									connDialog.pack();
									connDialog.revalidate();
									connDialog.repaint();
									connDialog.setVisible(Boolean.TRUE);
						  }
							} catch (ConnectionException e1) {
								System.out.println();
								e1.printStackTrace();

								dialog.dispose();
								final JDialog connDialog = new JDialog();
								/*JOptionPane optionPane = new JOptionPane("Connection Successful"
										, JOptionPane.INFORMATION_MESSAGE, JOptionPane.PLAIN_MESSAGE);
								JDialog dialog = optionPane.createDialog(panel, "Endpoint Connection Status");
								dialog.setVisible(true);*/                                            
								panel.remove(progressBar);
								panel.remove(progressLabel);
								JPanel connPanel = new JPanel();
								connPanel.setBorder(null);
								connDialog.setUndecorated(true) ;
								connectionButtion = new JButton();
								connectionButtion.setBorderPainted(false);
								connectionButtion.setBorder(null);
								connectionButtion.setCursor(new Cursor(Cursor.HAND_CURSOR));
//								//button.setFocusable(false);
								connectionButtion.setMargin(new Insets(0, 0, 0, 0));
								connectionButtion.setBackground(Color.white);
								connectionButtion.setContentAreaFilled(false);
								connectionButtion.setIcon(new ImageIcon(ClassLoader.getSystemResource("connection-failed.png.png")));
								//connectionButtion.setPreferredSize(new Dimension(200,30));
								//connectionButtion = Util.getDecoratedButton(connectionButtion, "connection-successful.png");
								connPanel.add(connectionButtion);
								connDialog.setBackground(Color.WHITE);
								connDialog.getContentPane().add(connPanel);
								connDialog.setResizable(false);
								connDialog.setSize(120, dialog.getHeight());
								connDialog.setLocationRelativeTo(null);
								connDialog.setAlwaysOnTop(false);
								connDialog.setVisible(true);
								connDialog.pack();
								connDialog.revalidate();
								connDialog.repaint();
								connDialog.setVisible(Boolean.TRUE);
							}
						}
						});
					}
				});
				//				if(userNameTxt.getText()!= null && !userNameTxt.getText().isEmpty() 
				//						&& passwordField.getPassword() != null && passwordField.getPassword().length >0) {
				//				}
			}
		});
	}

	/***
	 * To Remove EP Selection screen and 
	 * load the Dash Board Panel
	 * 
	 */
	private void loadEPSectionPanel() {
		this.remove(endPointNameLabel);
		this.remove(projectLabel);
		this.remove(topLineSeparator);
		this.remove(midSeparator);
		this.remove(topSeparator);
		this.remove(loginPanel);
		this.remove(loginOptionPanel);
		this.remove(optionalHeaderLoginPanel);
		this.remove(buttonPanel);
		this.revalidate();
		this.repaint();
		JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
		frame.getContentPane().removeAll(); 
		System.out.println("endPointSelectionPanel : 4");
		AppDashBoardPanel dashBoardPanel = new AppDashBoardPanel("ABCD",null);
		dashBoardPanel.setBackground(Color.white);
		dashBoardPanel.setVisible(Boolean.TRUE);
		dashBoardPanel.showEndPointSelection();
		frame.add(dashBoardPanel);
		Dimension dimension = new Dimension();
		dimension .setSize(600,  600);
		frame.setSize(dimension);
		frame.invalidate();
		frame.validate();
		frame.repaint();
		//		this.add(dashBoardPanel);
		//		this.updateUI();
	}

	private void buildLoginOptionPanel() {
		JLabel loginOptionsLabel = new JLabel("Login Options");
		loginOptionsLabel.setForeground(Color.BLUE);
		loginOptionPanel.add(loginOptionsLabel, "gap 0 445 10, wrap");
		butGroup.add(loginNormal);
		butGroup.add(loginSandBox);
		butGroup.add(loginWSDL);
		loginSandBox.setSelected(Boolean.TRUE);

		loginNormal.setBackground(Color.white);
		loginSandBox.setBackground(Color.white);
		loginWSDL.setBackground(Color.white);

		loginNormal.setActionCommand("normalLogin");
		loginSandBox.setActionCommand("loginSandbox");
		loginWSDL.setActionCommand("loginWSDL");
		btnBrowse = new JButton();
		btnBrowse = Util.getDecoratedButton(btnBrowse, "browse_but.jpg");
		final JPanel wsdlPanel = new JPanel();
		wsdlPanel.setLayout(new MigLayout());
		loginNormal.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		loginSandBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loginPanel.remove(wsdlPanel);
				loginPanel.revalidate();
				window = SwingUtilities.windowForComponent(loginPanel);
				window.setSize(new Dimension(600, 650));
				window.repaint();
			}
		});
		loginWSDL.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				wsdlPanel.add(wsdlURLLabel, "gap 10 10");
				wsdlPanel.add(wsdlField, "gap 0 5");
				wsdlPanel.setBackground(Color.white);
				loginPanel.add(wsdlPanel, "gap 0 5");
				loginPanel.revalidate();
				loginPanel.getRootPane().revalidate();
				window = SwingUtilities.windowForComponent(loginPanel);
				window.setSize(new Dimension(600, 650));
				window.repaint();
			}
		});

		loginOptionPanel.add(loginNormal, "gap 0 445, wrap");
		loginOptionPanel.add(loginSandBox, "gap 0 445, wrap");
		loginOptionPanel.add(loginWSDL, "gap 0 445, wrap");
	}

	/***
	 * To build Optional Header panel
	 */
	private void buildOptionalHeaderLoginPanel() {
		optionalHeaderLabel = new JLabel("Optional Header");
		optionalHeaderLabel.setForeground(Color.BLUE);
		optionalHeaderLoginPanel.add(optionalHeaderLabel, "wrap");
		optionalHeaderLoginPanel.setBackground(Color.white);
		JLabel userName = new JLabel("User Name : ");
		optionalUserNameTxt = new RoundedTextField(20);
		JLabel passwordLabel = new JLabel("Password : ");
		optionalPasswordField = new RoundedPasswordField(20);
		userName.setLabelFor(userNameTxt);
		passwordLabel.setLabelFor(passwordField);
		optionalUserNameTxt.setText("lahari.be@gmail.com");
		optionalPasswordField.setText("menlo@123");
		optionalHeaderLoginPanel.add(userName);
		optionalHeaderLoginPanel.add(optionalUserNameTxt, "gap 0 10, wrap");
		optionalHeaderLoginPanel.add(passwordLabel);
		optionalHeaderLoginPanel.add(optionalPasswordField, "gap 0 10, wrap");
	}

	/***
	 * To attach Configuration panel to EndPoint screen
	 */
	private void attachConfigPanel() {
		System.out.println(">>>> "+this.getParent().getParent().getComponents());
		final ConfigurationPanel configPanel = new ConfigurationPanel();
		remove(projectLabel);
		remove(topLineSeparator);
		remove(endPointNameLabel);
		remove(loginPanel);
		remove(loginOptionPanel);
		remove(optionalHeaderLoginPanel);
		remove(testConnBut);
		remove(midSeparator);
		remove(topSeparator);
		remove(buttonPanel);
		appDataModel.setUserView(UserViewEnum.TEAMPLATE_SELECTION.getValue());
		configPanel.setSize(new Dimension(600,300));
		this.add(configPanel, "wrap");
		doAnotherButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				System.out.println(" appDataModel.getUserView() : "+appDataModel.getUserView());
				if(appDataModel.getUserView().equalsIgnoreCase(
						UserViewEnum.CONFIGURATION_COMPLETE.getValue())) {
					loadEPSelectionScreen();
				}
			}
		});
		nextButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				if(appDataModel.getUserView().equalsIgnoreCase(
						UserViewEnum.TEAMPLATE_SELECTION.getValue())) {
					System.out.println(appDataModel.getSelectedTemplateId());
					System.out.println("Next button pressed....");
					appDataModel.setUserView(UserViewEnum.FINISH.getValue());
					nextButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("finish_button.jpg")));
					backButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("cancel.jpg")));
				} else if(appDataModel.getUserView().equalsIgnoreCase(
						UserViewEnum.FINISH.getValue())){
					System.out.println("Finsih button pressed....");
					try{
						appDataModel.setUserView(UserViewEnum.CONFIGURATION_COMPLETE.getValue());
						loadCompleteScreen(configPanel);
					}catch(Exception e) {
						System.out.println("Exception in saving Data: "+e.getMessage());
					}
				} else if(appDataModel.getUserView().equalsIgnoreCase(
						UserViewEnum.CONFIGURATION_COMPLETE.getValue())){
					System.exit(0);
				} else {
					new TestConnetionDialog("","").doJob();
				}
			}
		});
		this.revalidate();
		this.repaint();
	}

	private void attchDashBoardPanel() {
		AppDashBoardPanel dashBoardPanel = new AppDashBoardPanel(appTitle,clientGUI);
		dashBoardPanel.setBackground(Color.white);
		dashBoardPanel.setVisible(Boolean.TRUE);
		this.add(dashBoardPanel);
	}

	private void loadEPSelectionScreen() {
		System.out.println("1");
		remove(completePanel);
		System.out.println("2");
		remove(bottomPanel);
		remove(endPointLabel);
		System.out.println("3");
		appDataModel.setUserView(UserViewEnum.DO_ANOTHER.getValue());
		AppDashBoardPanel adbPanel = new AppDashBoardPanel("", clientGUI); 
		System.out.println("4");
		adbPanel.showEndPointSelection();
		System.out.println("5");
		clientGUI.repaint();
	}

	private void loadCompleteScreen(JPanel configPanel) {
		completePanel = new JPanel();
		remove(configPanel);
		remove(configPanel);
		remove(nextButton);
		ImageIcon configIcon = new ImageIcon(ClassLoader.getSystemResource("complete_header.jpg"));
		//		AppDashBoardPanel.getProjectLabel().setIcon(configIcon);
		JLabel completeLabel = new JLabel("Configuration Complete");
		completeLabel.setForeground(Color.blue);

		JSeparator separator = (JSeparator) Util.createHorizontalSeparator();
		JLabel tickIconLabel = new JLabel(new ImageIcon(ClassLoader.getSystemResource("tick.jpg")));

		completePanel.setLayout(new MigLayout());
		completePanel.setBackground(Color.white);
		completePanel.add(completeLabel);
		completePanel.add(completeLabel, "gap 150 0 80 30, wrap");
		separator.setPreferredSize(new Dimension(500,3));
		//		completePanel.add(separator, "wrap");
		completePanel.add(tickIconLabel, "gap 150 100 50 80, wrap");
		add(completePanel,"wrap");
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new MigLayout());
		bottomPanel.setBackground(Color.white);
		bottomPanel.add(doAnotherButton, "gapbefore 200");
		bottomPanel.add(nextButton, "gapbefore 5");
		add(bottomPanel);
	}

	/***
	 * To build the login panel in EndPoint authentication screen
	 */
	private void buildLoginPanel() {
		endPointLabel = new JLabel("EndPoint Credentials");
		endPointLabel.setForeground(Color.BLUE);
		JLabel userName = new JLabel("User Name : ");
		userNameTxt = new RoundedTextField(20);
		JLabel passwordLabel = new JLabel("Password : ");
		JLabel aPIKeyLabel = new JLabel("API Key : ");
		passwordField = new RoundedPasswordField(20);
		apiKeyField = new RoundedPasswordField(20);
		wsdlField = new RoundedTextField(20);
		wsdlField.setText("https://login.salesforce.com/services/Soap/u/31.0");
		wsdlField.setForeground(Color.lightGray);
		wsdlURLLabel.setLabelFor(wsdlField);
		userName.setLabelFor(userNameTxt);
		passwordLabel.setLabelFor(passwordField);
		aPIKeyLabel.setLabelFor(apiKeyField);
		userNameTxt.setText("lahari.be@gmail.com");
		passwordField.setText("menlo@123");
		apiKeyField.setText("D5xgn57Cd4bXdmYT2YAbgp1sR");
		loginPanel.add(endPointLabel, "wrap");

		loginInnerPanel.setLayout(new MigLayout());
		loginInnerPanel.add(userName, "gap 10 10 10 0, ");
		loginInnerPanel.add(userNameTxt, "gap 0 200 10 0, wrap");
		loginInnerPanel.add(passwordLabel, "gap 10 0 10 0");
		loginInnerPanel.add(passwordField, "gap 0 200 10 0, wrap");
		loginInnerPanel.add(aPIKeyLabel, "gap 10 0 10 0");
		loginInnerPanel.add(apiKeyField, "gap 0 205 10 0, wrap");
		loginInnerPanel.setBackground(Color.white);
		loginPanel.add(loginInnerPanel, "wrap");
	}
}

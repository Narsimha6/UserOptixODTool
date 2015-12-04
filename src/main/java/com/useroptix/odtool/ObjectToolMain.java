package com.useroptix.odtool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.useroptix.odtool.components.RoundedButton;
import com.useroptix.odtool.components.RoundedPasswordField;
import com.useroptix.odtool.components.RoundedTextField;
import com.useroptix.odtool.screens.AppDashBoardPanel;
import com.useroptix.odtool.screens.Splash;
import com.useroptix.odtool.service.ProjectService;
import com.useroptix.odtool.to.ApplicationDataModel;
import com.useroptix.odtool.to.UserTo;
import com.useroptix.odtool.to.UserViewEnum;
import com.useroptix.odtool.utils.ApplicationContextProvider;
import com.useroptix.odtool.utils.JersyClientUtil;

/***
 * This is the main class for the UserOptix Object Discovery Tool
 * @author narasimhar
 *
 */
public class ObjectToolMain {

	private static Splash splash;
	private static JFrame clientGUI;
	private JPanel defaultPanel;
	private JPanel loginPanel;
	private static String appTitle = "UserOptix Quick Start Tool";

	private static ApplicationContext context;

	private ProjectService projectService;
	
	private ApplicationDataModel appDataModel;
	public static void main(String[] args) {
		context = new ClassPathXmlApplicationContext("classpath:application-context.xml");
		splash = new Splash(appTitle);
		splash.setVisible(true);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			//	            LOGGER.log(Level.SEVERE, null, ex);
		}
		// Invoke thread to show welcome screen
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				splash.setVisible(false);
				clientGUI = new JFrame();
				clientGUI.setVisible(true);
				ObjectToolMain toolMain = new ObjectToolMain();
				toolMain.initComponents();
			}
		});
	}

	/***
	 * Initialize  the welcome screen
	 */
	private void initComponents() {
		appDataModel = ApplicationContextProvider.getApplicationContext()
				.getBean("appDataModel", ApplicationDataModel.class);
		appDataModel.setUserView(UserViewEnum.LOGIN.getValue());
		JFrame.setDefaultLookAndFeelDecorated(true);
		Dimension dimension = new Dimension();
		dimension .setSize(600,  600);
		clientGUI.setSize(dimension);
		clientGUI.getContentPane().setBackground(Color.white);
		clientGUI.setLocationRelativeTo(null);
		clientGUI.setTitle(appTitle);
		ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("uo_icon.png"));
		clientGUI.setIconImage(icon.getImage());

		loginPanel = new JPanel(new MigLayout("insets 0 0 0 0"));
		loginPanel.setBackground(Color.white);
		loginPanel.setSize(dimension);
		final RoundedTextField userNameTxt = new RoundedTextField(20);
		RoundedButton loginButton = new RoundedButton("Login");
		final RoundedPasswordField passwordTxt = new RoundedPasswordField(20);
		userNameTxt.setText("amoore");//UserName
		userNameTxt.setForeground(Color.lightGray);
		passwordTxt.setEchoChar('\u0000');
		passwordTxt.setForeground(Color.lightGray);
		passwordTxt.setText("password");//Password
		userNameTxt.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				userNameTxt.setText("");
				userNameTxt.setForeground(Color.black);
			}
		});

		passwordTxt.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				passwordTxt.setText("");
				passwordTxt.setForeground(Color.black);
				passwordTxt.setEchoChar('*');
			}
		});
		passwordTxt.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				int key=e.getKeyCode();
			    if(e.getSource()==passwordTxt) {
			        if(key==KeyEvent.VK_ENTER) {
			        	System.out.println("-->>");

						String userName = userNameTxt.getText();
						String password = new String(passwordTxt.getPassword()!=null ?passwordTxt.getPassword():null);
						if(userName != null && !userName.trim().isEmpty()
								&& !password.trim().isEmpty()) {
							if(userName.equalsIgnoreCase("UserName") 
									&& password.equalsIgnoreCase("Password")) {
								JOptionPane.showMessageDialog(
										clientGUI, "UserName/Password are invalid.", "Login Error",
										JOptionPane.QUESTION_MESSAGE);
								return;
							}
							ApplicationContextProvider.setApplicationContext1(context);
							projectService = ApplicationContextProvider.getApplicationContext()
									.getBean("projectService", ProjectService.class);
							UserTo user = new UserTo();
							user.setUsername(userName);
							user.setPassword(password);
							JersyClientUtil cl = new JersyClientUtil();
							System.out.println(cl.userOptixURL);
							Boolean isValidUser = Boolean.FALSE;
							// Old flow to check the User credentials in DB
							isValidUser = projectService.isValidUser(user);
							/*try {
								// New flow to check the user credentials with OAuth20
								user = cl.callLoginUser(user);
								String token = user.getToken(); 
								if( token!= null && !token.isEmpty()) {
									isValidUser = Boolean.TRUE;
								}
							} catch (ConnectException e1) {
								System.out.println("Unable to connect to OAuth Server :" +e1.getMessage());
							}*/
							
							if(isValidUser) {
								appDataModel = ApplicationContextProvider.getApplicationContext()
										.getBean("appDataModel", ApplicationDataModel.class);
								appDataModel.setUserName(userName);
								appDataModel.setPassword(password);
								loginPanel.setVisible(Boolean.FALSE);
								AppDashBoardPanel dashBoardPanel = new AppDashBoardPanel(appTitle,clientGUI);
								clientGUI.remove(loginPanel);
								clientGUI.getContentPane().add(dashBoardPanel);
								dashBoardPanel.setBackground(Color.white);
								dashBoardPanel.setVisible(Boolean.TRUE);
							} else {
								JOptionPane.showMessageDialog(
										clientGUI, "UserName/Password are invalid.", "Login Error",
										JOptionPane.QUESTION_MESSAGE);
								userNameTxt.setText("User Name");
								userNameTxt.setForeground(Color.lightGray);
								passwordTxt.setEchoChar('\u0000');
								passwordTxt.setForeground(Color.lightGray);
								passwordTxt.setText("Password");
							}
						} else  {
							JOptionPane.showMessageDialog(
									clientGUI, "UserName/Password are required.", "Login Error",
									JOptionPane.QUESTION_MESSAGE);
							userNameTxt.setText("User Name");
							userNameTxt.setForeground(Color.lightGray);
							passwordTxt.setEchoChar('\u0000');
							passwordTxt.setForeground(Color.lightGray);
							passwordTxt.setText("Password");

						}
			        }
			    }
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});
		passwordTxt.addFocusListener(new FocusAdapter(){
			@Override
			public void focusGained(FocusEvent e){
				passwordTxt.setText("");
				passwordTxt.setForeground(Color.black);
				passwordTxt.setEchoChar('*');
			}
		});

		loginButton.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				String userName = userNameTxt.getText();
				String password = new String(passwordTxt.getPassword()!=null ?passwordTxt.getPassword():null);
				if(userName != null && !userName.trim().isEmpty()
						&& !password.trim().isEmpty()) {
					if(userName.equalsIgnoreCase("UserName") 
							&& password.equalsIgnoreCase("Password")) {
						JOptionPane.showMessageDialog(
								clientGUI, "UserName/Password are invalid.", "Login Error",
								JOptionPane.QUESTION_MESSAGE);
						return;
					}
					ApplicationContextProvider.setApplicationContext1(context);
					projectService = ApplicationContextProvider.getApplicationContext()
							.getBean("projectService", ProjectService.class);
					UserTo user = new UserTo();
					user.setUsername(userName);
					user.setPassword(password);
					JersyClientUtil cl = new JersyClientUtil();
					System.out.println(cl.userOptixURL);
					Boolean isValidUser = Boolean.FALSE;
					// Old flow to check the User credentials in DB
					isValidUser = projectService.isValidUser(user);
					/*try {
						// New flow to check the user credentials with OAuth20
						user = cl.callLoginUser(user);
						String token = user.getToken(); 
						if( token!= null && !token.isEmpty()) {
							isValidUser = Boolean.TRUE;
						}
					} catch (ConnectException e1) {
						System.out.println("Unable to connect to OAuth Server :" +e1.getMessage());
					}*/
					
					if(isValidUser) {
						appDataModel = ApplicationContextProvider.getApplicationContext()
								.getBean("appDataModel", ApplicationDataModel.class);
						appDataModel.setUserName(userName);
						appDataModel.setPassword(password);
						loginPanel.setVisible(Boolean.FALSE);
						AppDashBoardPanel dashBoardPanel = new AppDashBoardPanel(appTitle,clientGUI);
						clientGUI.remove(loginPanel);
						clientGUI.getContentPane().add(dashBoardPanel);
						dashBoardPanel.setBackground(Color.white);
						dashBoardPanel.setVisible(Boolean.TRUE);
					} else {
						JOptionPane.showMessageDialog(
								clientGUI, "UserName/Password are invalid.", "Login Error",
								JOptionPane.QUESTION_MESSAGE);
						userNameTxt.setText("User Name");
						userNameTxt.setForeground(Color.lightGray);
						passwordTxt.setEchoChar('\u0000');
						passwordTxt.setForeground(Color.lightGray);
						passwordTxt.setText("Password");
					}
				} else  {
					JOptionPane.showMessageDialog(
							clientGUI, "UserName/Password are required.", "Login Error",
							JOptionPane.QUESTION_MESSAGE);
					userNameTxt.setText("User Name");
					userNameTxt.setForeground(Color.lightGray);
					passwordTxt.setEchoChar('\u0000');
					passwordTxt.setForeground(Color.lightGray);
					passwordTxt.setText("Password");

				}
			}
		});
		JTextField txtField = new JTextField();
		txtField.setVisible(Boolean.FALSE);
		txtField.requestFocusInWindow();
		txtField.requestFocus();
		EtchedBorder eBorder = new EtchedBorder(EtchedBorder.LOWERED);
		loginPanel.setBorder(eBorder);
		JLabel projectLabel = new JLabel(appTitle);
		projectLabel.setForeground(Color.orange);
		JPanel compPanel = new JPanel();
		compPanel.setLayout(new MigLayout());
		compPanel.add(txtField, "gap 80 5, wrap");
		projectLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		loginPanel.setBackground(Color.white);
		loginPanel.add(projectLabel, "gap 100 0 180, wrap");
		loginPanel.add(compPanel, "gap 30 0 10 20, wrap");
		compPanel.setBackground(Color.white);
		compPanel.add(userNameTxt, "gap 80 5, wrap");
		compPanel.add(passwordTxt, "gap 80 5");
		loginButton.setPreferredSize(new Dimension(100, 30));
		loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		compPanel.add(loginButton, "gap 0 150 0 10, wrap");
		JLabel forgotLabel = new JLabel("Forgot your username or password ?");
		forgotLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 8));
		forgotLabel.setForeground(Color.blue);
		forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		forgotLabel.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				JOptionPane.showMessageDialog(
						clientGUI, "You forgot your UserName/Password.", "Forgot UserName/Password",
						JOptionPane.QUESTION_MESSAGE);
			}
		});
		compPanel.add(forgotLabel, "gap 80 50 0 200");
		defaultPanel = new JPanel(new MigLayout("insets 0 0 0 0"));
		defaultPanel.setBackground(Color.white);;
		clientGUI.getContentPane().setLayout(new MigLayout());
		clientGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		defaultPanel.setVisible(false);
		clientGUI.getContentPane().add(loginPanel);
		clientGUI.setResizable(Boolean.TRUE);
		clientGUI.pack();
		userNameTxt.requestFocusInWindow(); 
	}
	
	public static JFrame getTopFrame() {
		return clientGUI;
	}
}

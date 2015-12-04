package com.useroptix.odtool.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.useroptix.odtool.service.EndPointConnectionService;
import com.useroptix.odtool.utils.ApplicationContextProvider;

/**
 * This is a Test connection dialog showing screen
 * @author narasimhar
 */
public class TestConnetionDialog  extends JFrame {

	private EndPointConnectionService endPointConnService;
	private String userName;
	private String password;
	private String token;

	/***
	 * Constructor
	 * @param userName
	 * @param password
	 */
	public TestConnetionDialog(String userName, String password) {
		this.userName = "lahari.be@gmail.com";
		this.password = "menlo@123D5xgn57Cd4bXdmYT2YAbgp1sR";
	}

	@SuppressWarnings("rawtypes")
	public void doJob() {

		JTextArea msgLabel;
		JProgressBar progressBar;
		final int MAXIMUM = 100;
		final JPanel panel;
		setResizable(false);
		progressBar = new JProgressBar(0, MAXIMUM);
		progressBar.setIndeterminate(true);
		progressBar.setBackground(Color.white);
		progressBar.setForeground(new Color(243, 179, 69));
		progressBar.setValue(90);
		msgLabel = new JTextArea("Testing connection progress");
		msgLabel.setEditable(false);
		msgLabel.setForeground(Color.white);

		panel = new JPanel(new BorderLayout(15, 15));
		panel.add(msgLabel, BorderLayout.PAGE_START);
		panel.add(progressBar, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createEmptyBorder(11, 11, 11, 11));

		final JDialog dialog = new JDialog();
		dialog.setUndecorated(true) ;
		panel.setBackground(Color.BLACK);
		dialog.setBackground(Color.white);
		dialog.getContentPane().add(panel);
		dialog.setResizable(false);
		dialog.setSize(100, dialog.getHeight());
		dialog.setLocationRelativeTo(null);
		dialog.setAlwaysOnTop(false);
		dialog.setVisible(true);
		dialog.pack();
		dialog.revalidate();
		dialog.repaint();
		dialog.setVisible(Boolean.TRUE);
		msgLabel.setBackground(panel.getBackground());

		Executor executor = java.util.concurrent.Executors.newSingleThreadExecutor();
		((ExecutorService) executor).submit(new Runnable() { public void run() { 
			//To get EP Connection
			testEPConnection();
		}
		/**
		 * testEPConnection
		 * On connection suucess close the progress bar
		 */
		private void testEPConnection() {

			// Close the dialog
			endPointConnService = ApplicationContextProvider.getApplicationContext()
					.getBean("endPointConnectionServiceImpl", EndPointConnectionService.class);
			try {
				PartnerConnection connection = endPointConnService.connectToSalesForce(
						userName, password, "D5xgn57Cd4bXdmYT2YAbgp1sR");
				if(connection != null) {
					progressBar.setValue(90);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dialog.dispose();
					JOptionPane optionPane = new JOptionPane("Connection Successful"
							, JOptionPane.INFORMATION_MESSAGE, JOptionPane.PLAIN_MESSAGE);
					JDialog dialog = optionPane.createDialog(panel, "Endpoint Connection Status");
					dialog.setVisible(true);

					//					DescribeGlobalResult describeGlobalResult =
					//							connection.describeGlobal();
					// Get the sObjects from the describe global result
					//					DescribeGlobalSObjectResult[] sobjectResults =
					//							describeGlobalResult.getSobjects();
					//					// Write the name of each sObject to the console
					//					for (int i = 0; i < sobjectResults.length; i++) {
					//						System.out.println(sobjectResults[i].getName());
					//					}
					//					DescribeGlobalResult global = connection.describeGlobal();
					//					System.out.println(global);
				} else {
					dialog.dispose();
					JOptionPane optionPane1 = new JOptionPane("Connection failed"
							, JOptionPane.INFORMATION_MESSAGE, JOptionPane.PLAIN_MESSAGE);
					JDialog dialog1 = optionPane1.createDialog(panel, "Endpoint Connection Status");
					dialog1.setVisible(true);
				}
			} catch (ConnectionException e1) {
				System.out.println();
				e1.printStackTrace();
				JOptionPane optionPane1 = new JOptionPane("Connection failed"
						, JOptionPane.INFORMATION_MESSAGE, JOptionPane.PLAIN_MESSAGE);
				JDialog dialog1 = optionPane1.createDialog(panel, "Endpoint Connection Status");
				dialog1.setVisible(true);
			}
		}});
	}
	
}
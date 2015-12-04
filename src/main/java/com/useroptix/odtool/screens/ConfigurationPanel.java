package com.useroptix.odtool.screens;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.useroptix.odtool.bo.Project;
import com.useroptix.odtool.bo.ProjectTemplateProperties;
import com.useroptix.odtool.components.RoundedTextField;
import com.useroptix.odtool.service.ProjectService;
import com.useroptix.odtool.service.impl.ProjectServiceImpl;
import com.useroptix.odtool.to.ApplicationDataModel;
import com.useroptix.odtool.to.UserViewEnum;
import com.useroptix.odtool.utils.ApplicationContextProvider;
import com.useroptix.odtool.utils.Util;

/***
 * EndPoint Configuration Panel
 * @author narasimhar
 *
 */
public class ConfigurationPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// Variables declaration                   
	private JPanel configurationPanel;
	private JComponent lineSeparator; 
	private JComponent lastLineSeparator; 
	final RoundedTextField objectTypeTxt = new RoundedTextField(20);
	private ApplicationDataModel appDataModel;
	private String selTemplateName;
	private JButton backButton;
	private JButton nextButton;
	private JPanel buttonPanel;
	private JLabel projectLabel = null;
	private JLabel endPointNameLabel = null;
	private JComponent topLineSeparator; 
	private ProjectService projectServiceImpl;

	public ConfigurationPanel() {
		super();
		this.setBackground(Color.white);
		initComponents();
	}

	/***
	 * Initialize the components
	 */
	private void initComponents() {
		this.setLayout(new MigLayout());
		buttonPanel= new JPanel(new MigLayout());
		configurationPanel= new JPanel(new MigLayout());
		ImageIcon credentialsIcon = new ImageIcon(ClassLoader.getSystemResource("configure_header.jpg"));
		projectLabel = new JLabel();
		projectLabel.setIcon(credentialsIcon);
		configurationPanel.add(projectLabel, "wrap");

		topLineSeparator = (JSeparator) Util.createHorizontalSeparator();
		topLineSeparator.setPreferredSize(new Dimension(500,3));
		configurationPanel.add(topLineSeparator, "wrap");

		appDataModel = ApplicationContextProvider.getApplicationContext()
				.getBean("appDataModel", ApplicationDataModel.class);
		String endPointName = appDataModel.getSelectedEndPoint().toUpperCase();
		Icon epIcon = Util.getImageIconForEndPoint(endPointName);
		endPointNameLabel = new JLabel(endPointName, epIcon, JLabel.CENTER);
		endPointNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		configurationPanel.setBackground(Color.white);

		configurationPanel.add(endPointNameLabel, "wrap");
		this.setLayout(new MigLayout());
		JLabel configureLabel = new JLabel("CONFIGURE");
		configureLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		configurationPanel.add(configureLabel, "wrap");

		buildConfigurePanel();
		this.add(configurationPanel, "wrap");

		backButton = new JButton();
		backButton = Util.getDecoratedButton(backButton, "back.jpg");

		nextButton = new JButton();
		nextButton = Util.getDecoratedButton(nextButton, "next.jpg");
		buttonPanel.setBackground(Color.white);
		buttonPanel.add(backButton, "gapbefore 300");
		buttonPanel.add(nextButton, "gap 5, wrap");
		this.add(buttonPanel);

		backButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				loadEPSelectionPanel();
			}
		});
		nextButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				if(appDataModel.getUserView().equalsIgnoreCase(
						UserViewEnum.TEAMPLATE_SELECTION.getValue())){
					System.out.println("Finsih button pressed....");
					try{
						buildAndSavePTPData();
						appDataModel.setUserView(UserViewEnum.CONFIGURATION_COMPLETE.getValue());
					}catch(Exception e) {
						System.out.println("Exception in saving Data: "+e.getMessage());
						e.printStackTrace();
					}
				} else if(appDataModel.getUserView().equalsIgnoreCase(
						UserViewEnum.CONFIGURATION_COMPLETE.getValue())){
					loadCompletePanel();
				}
			}

		});
	}

	private void loadCompletePanel() {
		remove(buttonPanel);
		remove(configurationPanel);
		remove(projectLabel);
		remove(lineSeparator);
		remove(topLineSeparator);
		remove(buttonPanel);
		CompletePanel completePanel = new CompletePanel();
		add(completePanel);
		revalidate();
	}
	/***
	 * To build the ProjectTemplateProperties object from data 
	 * and save them into PTP table.
	 */
	private void buildAndSavePTPData() {
		if(appDataModel.getProjectName() == null 
				|| appDataModel.getSelectedTemplateName() == null) {
			System.out.println("Invalid params for saving data into "
					+ "Project and PTP tables.");
			return;
		}
		projectServiceImpl = ApplicationContextProvider.getApplicationContext()
				.getBean("projectServiceImpl", ProjectServiceImpl.class);
		Long projectId = appDataModel.getProjectId();
		try {
			if(projectId == null) {
				System.out.println("Project Id is null ");
				projectId = saveProjectData(appDataModel.getProjectName());
				if(projectId == null) {
					System.out.println("Not able to save records in Project table.");
					return;
				}
			}
			appDataModel.setProjectId(projectId);
			System.out.println("Project Id : " + projectId);
			ProjectTemplateProperties ptpObject = buildPTPObject(projectId);
			Long ptpId = projectServiceImpl.saveProjectTemplateProperties(ptpObject);
			if(ptpId != null) {
				System.out.println("-->> PTP details saved and Id : "+ptpId);
				appDataModel.setUserView(UserViewEnum.SAVE_PTP_DATA.getValue());
				
				nextButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("finish.jpg")));
				nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
				nextButton.setSize(90, 30);
				nextButton.getParent().revalidate();
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
			JOptionPane.showMessageDialog(
					null, "Already entry exists for the same, Choose another template.", "Template Error",
					JOptionPane.INFORMATION_MESSAGE);	
		}
	}
	
	private ProjectTemplateProperties buildPTPObject(Long projectId) {
		ProjectTemplateProperties ptpObject = new ProjectTemplateProperties();
		ptpObject.setOrgId(1L);
		ptpObject.setProjectId(projectId);
		ptpObject.setTemplateId(appDataModel.getSelectedTemplateId());
		String propJSON = buildPropertiesJson();
		ptpObject.setProperties(propJSON);

		ptpObject.setCreatedUser(1l);
		ptpObject.setCreatedDate(new Date());
		ptpObject.setUpdatedUser(1l);
		ptpObject.setUpdatedDate(new Date());
		return ptpObject;
	}
	
	/***
	 * To save data into Project table.
	 * @param projectName
	 * @return
	 */
	private Long saveProjectData(String projectName) {
		Project project = new Project();
		project.setOrgId(1L);
		project.setName(projectName);
		project.setShortName(System.currentTimeMillis()+"");
		project.setActive(Boolean.TRUE);
		project.setCreatedUser(1L);
		project.setUpdatedUser(1L);
		project.setCreatedDate(new Date());
		project.setUpdatedDate(new Date());
		Long projectId = projectServiceImpl.saveProject(project);
		return projectId;
	}

	/***
	 * To build the JSON for saving properties into PTP table
	 * @return
	 */
	private String buildPropertiesJson() {
		StringBuilder sb = new StringBuilder();
		sb.append(" {\"properties\":[{\"name\":\"SalesforceUser\", \"value\":" + appDataModel.getSalesForceUserName() + "\"}");
		sb.append(" ,{\"name\":\"SalesforcePassword\", \"value\":\"" + appDataModel.getSalesForcePassword() + "\"}]}");
		return sb.toString();
	}

	/***
	 * To hide the existing the Configure panel and
	 * show the App DashBoard Panel
	 */
	private void loadEPSelectionPanel() {
		this.remove(projectLabel);
		this.remove(endPointNameLabel);
		this.remove(topLineSeparator);
		this.remove(topLineSeparator);
		this.remove(configurationPanel);
		this.remove(buttonPanel);
		JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
		frame.getContentPane().removeAll(); 
		AppDashBoardPanel dashBoardPanel = new AppDashBoardPanel("",
				(JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this));
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
	}

	/***
	 * To build the login panel in EndPoint authentication screen
	 */
	private void buildConfigurePanel() {
		JPanel browsePanel = new JPanel();
		browsePanel.setLayout(new MigLayout());
		browsePanel.setBackground(Color.white);
		JLabel endPointLabel = new JLabel("EndPoint Credentials");
		endPointLabel.setForeground(Color.BLUE);
		JLabel objectType = new JLabel("Object Type : ");

		objectTypeTxt.setText("Object Type");
		objectTypeTxt.setForeground(Color.lightGray);
		JLabel externalId = new JLabel("External ID : ");
		RoundedTextField externalIDTxt = new RoundedTextField(20);
		browsePanel.add(endPointLabel, "gap 0 5, wrap");
		browsePanel.add(objectType, "gap 0 5");
		browsePanel.add(objectTypeTxt, "gap 0 5");
		JButton btnBrowse = new JButton();
		btnBrowse = Util.getDecoratedButton(btnBrowse, "browse.jpg");
		browsePanel.add(btnBrowse, "gap 0 92, wrap");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				appDataModel = ApplicationContextProvider.getApplicationContext()
						.getBean("appDataModel", ApplicationDataModel.class);
				if(appDataModel.getSelectedTemplateId() == null) {
					System.out.println(">>>>>>>>>> before ");
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							final BrowseEndPointDialog epDialog = new BrowseEndPointDialog(configurationPanel);
							epDialog.show(configurationPanel);
							epDialog.addWindowListener(new WindowAdapter() {
								@Override
								public void windowClosed(WindowEvent e) {
									selTemplateName = epDialog.getSelectedTemplateName();
									System.out.println(">>>>>>>>>> "+selTemplateName);
									if(selTemplateName!= null && !selTemplateName.isEmpty()) {
										objectTypeTxt.setText(selTemplateName);
										objectTypeTxt.setForeground(Color.black);
									}
								}
							});
						}
					});
				} else {
					//					JOptionPane.showMessageDialog(browsePanel.getParent(), "Already Row inserted to PTP table.",
					//							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		objectTypeTxt.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				objectTypeTxt.setText("");
				objectTypeTxt.setForeground(Color.black);
			}
		});
		browsePanel.add(externalId, "gap 0 1 1 0");
		browsePanel.add(externalIDTxt, "gap 0 10 10 0, wrap");
		lineSeparator = Util.createHorizontalSeparator();
		lineSeparator.setPreferredSize(new Dimension(500,3));
		configurationPanel.add(browsePanel, "gap 0 1 1 20, wrap");
		configurationPanel.add(lineSeparator, "wrap");
		JLabel objFields = new JLabel("Object Fields");
		objFields.setForeground(Color.BLUE);
		configurationPanel.add(objFields, "gap 10, wrap");
		lastLineSeparator = Util.createHorizontalSeparator();
		lastLineSeparator.setPreferredSize(new Dimension(500,3));
		configurationPanel.add(lastLineSeparator, "gap 0 0 180, wrap");
		addFieldsPanel();
	}

	public void setObjectTypeTxt(String text) {
		objectTypeTxt.setText(text);
	}

	/***
	 * To add fields panel
	 */
	private void addFieldsPanel() {/*
		setLayout(new MigLayout());

		JLabel objectFields = new JLabel("Object Fields ");
		JPanel fieldsPanel = new JPanel();
		JPanel content = new JPanel(new MigLayout());
		String[] chekBoxStrs = new String[]{"Bananas", "Oranages", "Apples", "Pears", "Bananas", "Oranages", "Apples", "Pears","Bananas", "Oranages", "Apples", "Pears",
				"Bananas", "Oranages", "Apples", "Pears", "Bananas", "Oranages", "Apples", "Pears","Bananas", "Oranages", "Apples", "Pears",
				"Bananas", "Oranages", "Apples", "Pears", "Bananas", "Oranages", "Apples", "Pears","Bananas", "Oranages", "Apples", "Pears", 
				"Bananas", "Oranages", "Apples", "Pears", "Bananas", "Oranages", "Apples", "Pears","Bananas", "Oranages", "Apples", "Pears"};
		List<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
		if (chekBoxStrs.length > 0) {
			for (int index = 0; index < chekBoxStrs.length - 1; index++) {
				JCheckBox cb = new JCheckBox(chekBoxStrs[index]);
				cb.setOpaque(false);
				checkBoxes.add(cb);
				if(index != 0 && index%4 == 0) {
					content.add(cb, "gap 0 10, wrap");
				} else {
					content.add(cb, "gap 0 10");
				}
			}

			JCheckBox cb = new JCheckBox(chekBoxStrs[chekBoxStrs.length - 1]);
			cb.setOpaque(false);
			checkBoxes.add(cb);
		}
		JScrollPane jsp = new JScrollPane(content);
		fieldsPanel.add(objectFields, "gap 0 1 1 0, wrap");
		jsp.setBackground(Color.blue);
		fieldsPanel.add(jsp, "wrap");
		fieldsPanel.setPreferredSize(new Dimension(300,300));
		fieldsPanel.setBackground(Color.green);
	 */}
}

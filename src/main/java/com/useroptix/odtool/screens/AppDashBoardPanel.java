package com.useroptix.odtool.screens;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.useroptix.odtool.ObjectToolMain;
import com.useroptix.odtool.service.ProjectService;
import com.useroptix.odtool.service.impl.ProjectServiceImpl;
import com.useroptix.odtool.to.ApplicationDataModel;
import com.useroptix.odtool.utils.ApplicationContextProvider;
import com.useroptix.odtool.utils.Util;
/***
 * Dash Board Panel
 * @author narasimhar
 *
 */
public class AppDashBoardPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	@Autowired
	private static ApplicationContext context;

	// Variables declaration                 
	private JLabel newProjectLabel;
	private JLabel openProjectLabel;
	private JLabel recentProjectLabel;
	private JLabel helpLabel;
	private JLabel newProjectIconLabel;
	private JLabel openProjectIconLabel;
	private JLabel recentProjectIconLabel;
	private JLabel helpIconLabel;
	private JPanel defaultPanel;
	private static JLabel projectLabel = null;
	private JComponent topLineSeparator; 
	private ApplicationDataModel appDataModel;
	private ProjectService projectServiceImpl;
	
	public AppDashBoardPanel(String title, JFrame clientGUI) {
		super();
		appDataModel = ApplicationContextProvider.getApplicationContext()
				.getBean("appDataModel", ApplicationDataModel.class);
		appDataModel.setSelectedEndPoint(null);
		initComponents( "<html><font color='#2E3092' size='18pt'>Create Project</font></html>");
		clientGUI = ObjectToolMain.getTopFrame();
		clientGUI.revalidate(); 
	}

	/***
	 * Initialize the components
	 */
	private void initComponents(String appTitle) {
		defaultPanel = new JPanel(new MigLayout());
		defaultPanel.setBackground(Color.white);
		ImageIcon newIcon = new ImageIcon(ClassLoader.getSystemResource("create_project.jpg"));
		ImageIcon openIcon = new ImageIcon(ClassLoader.getSystemResource("open_project_new.jpg"));
		ImageIcon recentIcon = new ImageIcon(ClassLoader.getSystemResource("recent_project_new.jpg"));
		ImageIcon helpIcon = new ImageIcon(ClassLoader.getSystemResource("help_project_new.jpg"));

		newProjectLabel = new JLabel("Create Project");
		openProjectLabel = new JLabel("Open Project");
		recentProjectLabel = new JLabel("Recent Project");
		helpLabel = new JLabel("Help Project");

		newProjectLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		openProjectLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		recentProjectLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		helpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

		newProjectIconLabel = new JLabel(newIcon);
		openProjectIconLabel = new JLabel(openIcon);
		recentProjectIconLabel = new JLabel(recentIcon);
		helpIconLabel = new JLabel(helpIcon);

		defaultPanel.setSize(new Dimension(600,600));

		defaultPanel.add(newProjectIconLabel, "gap 40 1 30");
		newProjectIconLabel.setLabelFor(newProjectLabel);
		defaultPanel.add(newProjectLabel, "gap 20 15 30, wrap");

		defaultPanel.add(openProjectIconLabel, "gap 40 1 30");
		openProjectIconLabel.setLabelFor(openProjectLabel);
		defaultPanel.add(openProjectLabel, "gap 20 15 30, wrap");

		defaultPanel.add(recentProjectIconLabel, "gap 40 1 30");
		recentProjectIconLabel.setLabelFor(recentProjectLabel);
		defaultPanel.add(recentProjectLabel, "gap 20 15 30, wrap");

		defaultPanel.add(helpIconLabel, "gap 40 1 30");
		helpIconLabel.setLabelFor(helpLabel);
		defaultPanel.add(helpLabel, "gap 20 15 30");

		this.setLayout(new MigLayout());
		this.setBackground(Color.white);
		projectLabel = new JLabel("<html><font color='#2E3092' size='18px'>UserOptix Quick Start Tool</font></html>");
		projectLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		//		this.add(projectScreenIcon, "wrap, gap 100 0 50");
		this.add(projectLabel, "wrap, gap 140 0 50 20");

		topLineSeparator = Util.createHorizontalSeparator();
		topLineSeparator.setPreferredSize(new Dimension(500,3));
		this.add(topLineSeparator, "gap 20, wrap");
		defaultPanel.setBackground(Color.white);
		this.add(defaultPanel, "gap 50 10");
		this.setSize(new Dimension(600,  600));

		newProjectLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) { 
				System.out.println("New project clicked");
				String projectName= JOptionPane.showInputDialog("Enter Project Name to save:");
				restart:
					if(projectName != null && !projectName.isEmpty()) {
						appDataModel.setProjectName(projectName);
						projectServiceImpl = ApplicationContextProvider.getApplicationContext()
								.getBean("projectServiceImpl", ProjectServiceImpl.class);
						Boolean projectExists = projectServiceImpl.isProjectNameOrgIdExists(appDataModel.getProjectName()
								, 1L);
						if(!projectExists) {
							System.out.println("Project name is valid, no project exists with Project Name :"+projectName);
							// Remove DashBoard screen and Add EP selection screen
							showEndPointSelection();
						} else {
							JOptionPane.showMessageDialog(
									defaultPanel, "Project name already exists for Org_id 1."
									, "ProjectName Error",
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						break restart;
					}
			}
		});

		openProjectLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) { 
				System.out.println("Open project clicked");
			}
		});

		recentProjectLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) { 
				defaultPanel.setVisible(Boolean.TRUE);
				new RecentProjectsDialog(defaultPanel);
			}
		});

		helpLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) { 
				System.out.println("Recent project clicked");
			}
		});
	}

	/***
	 * Screen to show EndPoint selection
	 * @param imagePath
	 */
	public void showEndPointSelection() {
		System.out.println("EP selection screen");
		this.removeAll();
		EPSelectionPanel epsPanel = new EPSelectionPanel();
		this.add(epsPanel);
		this.updateUI();
	}
}

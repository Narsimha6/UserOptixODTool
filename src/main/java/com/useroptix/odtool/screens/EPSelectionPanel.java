package com.useroptix.odtool.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.useroptix.odtool.bo.EndPoints;
import com.useroptix.odtool.components.UserOptixList;
import com.useroptix.odtool.service.ProjectService;
import com.useroptix.odtool.to.ApplicationDataModel;
import com.useroptix.odtool.to.UserViewEnum;
import com.useroptix.odtool.utils.ApplicationContextProvider;
import com.useroptix.odtool.utils.Util;

public class EPSelectionPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5136579006493873995L;

	private static JLabel projectLabel = null;

	private ApplicationDataModel appDataModel;
	private ProjectService projectServiceImpl;

	private JPanel endPointSelectionPanel;
	private JPanel buttonPanel;

	private JButton nextButton;
	private JButton cancelButton;

	private UserOptixList<String> endPointList;

	private JComponent topLineSeparator; 
	public EPSelectionPanel() {
		this.setLayout(new MigLayout());
		setBackground(Color.white);
		setBorder(new EmptyBorder(0, 0, 0, 0));
		appDataModel = ApplicationContextProvider.getApplicationContext()
				.getBean("appDataModel", ApplicationDataModel.class);
		appDataModel.setUserView(UserViewEnum.ENDPOINT_SELECTION.getValue());
		appDataModel.setSelectedTemplateId(null);
		appDataModel.setSelectedTemplateName(null);
		endPointSelectionPanel = new JPanel(new MigLayout());
		endPointSelectionPanel.setBackground(Color.white);
		buttonPanel = new JPanel(new MigLayout());
		buttonPanel.setBackground(Color.white);
		// Screen Heading Icon
		Icon cpIcon = new ImageIcon(ClassLoader.getSystemResource("create_project.jpg"));
		projectLabel = new JLabel("<html><font color='#2E3092' size='18px'>Create Project</font></html>"
				, cpIcon ,JLabel.CENTER);
		projectLabel.setIconTextGap(20);
		projectLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		JLabel selectEndPointLabel = new JLabel("Select EndPoint");
		selectEndPointLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		nextButton = new JButton();
		nextButton = Util.getDecoratedButton(nextButton, "next.jpg");
		nextButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		cancelButton = new JButton();
		cancelButton = Util.getDecoratedButton(cancelButton, "cancel.jpg");
		buttonPanel.add(cancelButton, "gap 220 5");
		buttonPanel.add(nextButton, "gapright 170");
		endPointList = new UserOptixList<String>();
		endPointSelectionPanel.add(selectEndPointLabel, "gap 0 10 80 20");
		// Load endPoints
		loadEndPoints();
		JScrollPane scrollPane = new JScrollPane(endPointList);
		scrollPane.setMaximumSize(new Dimension(250, 160));
		scrollPane.setMinimumSize (new Dimension (250,160));
		scrollPane.setPreferredSize(new Dimension (250,160));
		endPointSelectionPanel.add(scrollPane, "gap 0 0 0 10, wrap");
		endPointSelectionPanel.setVisible(Boolean.TRUE);
		topLineSeparator = Util.createHorizontalSeparator();
		topLineSeparator.setPreferredSize(new Dimension(500,3));
		this.add(projectLabel, "gap 50 0 20 20, wrap");
		this.add(topLineSeparator, "gap 20, wrap");
		this.add(endPointSelectionPanel, "gap 50 10 20, wrap");
		this.add(buttonPanel, "wrap, gap 10");

		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Next button pressed....");
				String epSelected = String.valueOf(endPointList.getSelectedValue());
				if(epSelected == null || epSelected.equalsIgnoreCase("null")
						|| epSelected.trim().isEmpty()) {
					JOptionPane.showMessageDialog(
							endPointSelectionPanel, "No EndPoint selected, select an EndPoint"
							, "EndPoint Error",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					String selectedEP = endPointList.getSelectedValue();

					appDataModel.setSelectedEndPoint(selectedEP);
					showAuthPanel();
				}
			}

		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("endPointSelectionPanel : 0");
				if(appDataModel.getUserView().equalsIgnoreCase(
						UserViewEnum.ENDPOINT_SELECTION.getValue())) {
					loadDashBoard();
				}
			}

		});
	}

	/***
	 * To Remove EP Selection screen and 
	 * load the Dash Board Panel
	 * 
	 */
	private void loadDashBoard() {
		this.remove(projectLabel);
		this.remove(topLineSeparator);
		this.remove(endPointSelectionPanel);
		this.remove(buttonPanel);
		JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
		frame.getContentPane().removeAll(); 

		AppDashBoardPanel dashBoardPanel = new AppDashBoardPanel("ABCD",null);
		dashBoardPanel.setBackground(Color.white);
		dashBoardPanel.setVisible(Boolean.TRUE);
		dashBoardPanel.updateUI();
		frame.add(dashBoardPanel);
		Dimension dimension = new Dimension();
		dimension .setSize(600,  600);
		frame.setSize(dimension);
		frame.invalidate();
		frame.validate();
		frame.repaint();
	}

	/***
	 * To hide EP selection panel 
	 * show EP authentication panel
	 */
	private void showAuthPanel() {
		this.remove(projectLabel);
		this.remove(topLineSeparator);
		this.remove(endPointSelectionPanel);
		this.remove(buttonPanel);
		EndPointAuthPanel authPanel = new EndPointAuthPanel();
		this.add(authPanel);
		System.out.println("endPointSelectionPanel : 4");
		authPanel.setSize(new Dimension(600, 650));
		JFrame clientGUI = (JFrame) authPanel.getTopLevelAncestor();
		authPanel.revalidate();
		authPanel.repaint();
		clientGUI.setSize(new Dimension(600, 650));
		clientGUI.revalidate();
		clientGUI.repaint();
	}

	/***
	 * This method will load EndPoints from DB using the Spring applicationContext
	 */
	private void loadEndPoints() {
		projectServiceImpl = ApplicationContextProvider.getApplicationContext()
				.getBean("projectService", ProjectService.class);
		List<EndPoints> enpList = projectServiceImpl.getAllEndPoints();
		if(enpList != null && !enpList.isEmpty()) {
			DefaultListModel<String> model = new DefaultListModel<String>();
			endPointList.setFixedCellWidth(300);
			endPointList.setVisibleRowCount(5);
			for (EndPoints endPoint : enpList) {
				model.addElement(endPoint.getEndpoint());
			}
			endPointList.setModel(model);
		}
	}
}

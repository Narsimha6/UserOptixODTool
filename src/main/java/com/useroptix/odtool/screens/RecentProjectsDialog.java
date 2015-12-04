package com.useroptix.odtool.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.useroptix.odtool.bo.ProjectTemplateProperties;
import com.useroptix.odtool.service.ProjectService;
import com.useroptix.odtool.utils.ApplicationContextProvider;

/**
 * This is a Test connection dialog showing screen
 * @author narasimhar
 */
public class RecentProjectsDialog extends JDialog {

	/**
	 * Serialization version
	 */
	private static final long serialVersionUID = 9175259797193701602L;

	@Autowired
	private static ApplicationContext context;

	private ProjectService projectServiceImpl;

	public RecentProjectsDialog(JPanel parent) {
		this.setBackground(Color.white);
		initUI(parent);
	}

	/***
	 * Initialize the UI for Recent projects
	 */
	private void initUI(JPanel parent) {
		final JPanel panel = new JPanel(new MigLayout());
		this.setLayout(new MigLayout());
		this.getContentPane().add(panel);
		this.setTitle("Recent projects... ");
		final JPanel dialogPanel = new JPanel();
		panel.setBackground(Color.white);
		dialogPanel.setBackground(Color.white);
		// Set border to the Dialog
		EtchedBorder eBorder = new EtchedBorder(EtchedBorder.LOWERED);
		dialogPanel.setBorder(eBorder);
		dialogPanel.setLayout(new MigLayout());
		projectServiceImpl = ApplicationContextProvider.getApplicationContext()
				.getBean("projectServiceImpl", ProjectService.class);
		List<ProjectTemplateProperties> ptpList = projectServiceImpl.getRecentProjects();
		if(ptpList == null || ptpList.isEmpty()) {
			JOptionPane.showMessageDialog(
					panel, "No recent projects found.", "Recent projects Error",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JLabel selectProjectLabel = new JLabel("Select project to Load...");
		dialogPanel.add(selectProjectLabel, "wrap");
		for (ProjectTemplateProperties projTProperties : ptpList) {
			JButton button = new JButton(projTProperties.getId().toString());
			final Long ptpId = projTProperties.getId();
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					JOptionPane.showMessageDialog(
							dialogPanel, "You selected project : "+ ptpId,
							"Recent projects Alert",
							JOptionPane.INFORMATION_MESSAGE);
					dialogPanel.getParent().setVisible(Boolean.FALSE);
					dialogPanel.getParent().getParent().setVisible(Boolean.FALSE);
					RecentProjectsDialog.this.dispose();
				}
			});
			dialogPanel.add(button, "gap 20 30 10 10,wrap");
		}
		this.setResizable(false);
		this.setLocation(new Point(550,250));
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setAlwaysOnTop(false);
		this.setVisible(true);
		this.add(dialogPanel, "wrap");
		this.setSize(new Dimension(500,500));
		this.pack();
	}
}
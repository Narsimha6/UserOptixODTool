package com.useroptix.odtool.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.useroptix.odtool.components.SearchTextField;
import com.useroptix.odtool.components.UserOptixList;
import com.useroptix.odtool.service.ProjectService;
import com.useroptix.odtool.to.ApplicationDataModel;
import com.useroptix.odtool.to.UserViewEnum;
import com.useroptix.odtool.utils.ApplicationContextProvider;
import com.useroptix.odtool.utils.Util;

/**
 * This is a Test connection dialog showing screen
 * @author narasimhar
 */
public class BrowseEndPointDialog extends JDialog {

	/**
	 * Serialization version
	 */
	private static final long serialVersionUID = 9175259797193701602L;

	private ApplicationDataModel appDataModel;
	@Autowired
	private static ApplicationContext context;

	private ProjectService projectServiceImpl;

	private Map<String, Long> teamplateMap;
	private UserOptixList<String> templateList; 
	private String selectedTemplateName;

	public BrowseEndPointDialog(JPanel parent ) {
		projectServiceImpl = ApplicationContextProvider.getApplicationContext()
				.getBean("projectServiceImpl", ProjectService.class);
		appDataModel = ApplicationContextProvider.getApplicationContext()
				.getBean("appDataModel", ApplicationDataModel.class);
		
		Map<String, Long> teamplateMap = projectServiceImpl.getTemplateForEndpOint(
				appDataModel.getSelectedEndPoint(), null);
		if(teamplateMap != null && !teamplateMap.isEmpty()) {
			System.out.println("Teamplates found : "+teamplateMap.size());
			this.teamplateMap = teamplateMap;
		}
	}

	public void show(JPanel parent) {
//		setModal(true);
		getContentPane().setBackground(Color.white);
		initUI(parent);
		pack();
//		setLocationRelativeTo(null);
//		toFront();
	}
	/***
	 * Initialize the UI for Templates selection
	 * @param parent
	 * @param selectedTemplateId
	 * 
	 */
	private void initUI(JPanel parent) {
		setUndecorated(Boolean.TRUE);
		getRootPane().setBorder( BorderFactory.createLineBorder(Color.black) );
		setAlwaysOnTop(Boolean.TRUE);
		setLocationRelativeTo(null);
		setBackground(Color.white);
//		setShape(shape);
		// create JList with Templates
		Set<String> templateSet = teamplateMap.keySet();
		if(templateSet != null && !templateSet.isEmpty()) {
			String[] endPoints = teamplateMap.keySet().toArray(new String[teamplateMap.keySet().size()]);
			templateList = new UserOptixList<String>(endPoints);
		}
		appDataModel.setUserView(UserViewEnum.TEAMPLATE_SELECTION.getValue());
		final JPanel panel = new JPanel(new MigLayout());
		panel.setBackground(Color.white);
		this.setLayout(new MigLayout());
		this.getContentPane().add(panel);
		this.setTitle("Browse EndPoint");
		JPanel dialogPanel = new JPanel(); 
		// Set border to the Dialog
		//		EtchedBorder eBorder = new EtchedBorder(EtchedBorder.LOWERED);
		//		dialogPanel.setBorder(eBorder);
		dialogPanel.setLayout(new MigLayout());

		JLabel browseEPLabel = new JLabel("BROWSE ENDPOINT");
		dialogPanel.add(browseEPLabel, "gap 10 0 5 20, wrap");

		JPanel searchPanel = new JPanel();
		searchPanel.setBackground(Color.white);
		searchPanel.setLayout(new MigLayout());
		JLabel searchLabel = new JLabel("Search for an Object Type ");
		searchLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		searchLabel.setForeground(Color.blue);
		searchPanel.add(searchLabel, "gap 0 0 5 15");

		SearchTextField searchTxt = new SearchTextField(18);
		ImageIcon searchIcon = new ImageIcon(ClassLoader.getSystemResource("search-Icon.png"));
		searchTxt.setIcon(searchIcon);
		searchPanel.add(searchTxt, "gap 10 0 5 15, wrap");
		dialogPanel.add(searchPanel, "gap 10, wrap");

		templateList.setVisibleRowCount(5);
		JScrollPane scrollPane = new JScrollPane(templateList);
		scrollPane.setPreferredSize(new Dimension(380, 200));
		panel.add(scrollPane , BorderLayout.CENTER);
		dialogPanel.add(panel, "gap 30, wrap");
		
		this.setResizable(false);
		this.setLocation(new Point(550,250));
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setAlwaysOnTop(false);
		this.setVisible(true);
		JButton okButton = new JButton();
		okButton = Util.getDecoratedButton(okButton, "ok.jpg");
		JButton cancelButton = new JButton();
		cancelButton = Util.getDecoratedButton(cancelButton, "cancel.jpg");
		okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		okButton.setForeground(Color.white);
		cancelButton.setForeground(Color.white);
		okButton.setPreferredSize(new Dimension(90, 30));
		cancelButton.setPreferredSize(new Dimension(120, 30));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.cyan);
		buttonPanel.setLayout(new MigLayout());
		buttonPanel.setBackground(Color.white);
		buttonPanel.add(okButton, "gapbefore 15");

		buttonPanel.add(cancelButton, "wrap");
		dialogPanel.setBackground(Color.orange);
		dialogPanel.add(buttonPanel, "gap 200 10, wrap");
		dialogPanel.setBackground(Color.white);
		this.add(dialogPanel, "wrap");
		this.setBackground(Color.white);
		this.pack();
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String selectedItem = (String) templateList.getSelectedValue();
				if(selectedItem != null ){
					Container c = ((JButton)(e.getSource())).getParent();
					// get the container for this button.
					while( (c.getParent() != null)&&(!(c instanceof JDialog))) {
						c = c.getParent();
					}
					if(c instanceof JDialog) {
						System.out.println("You selected : Cancel ");
						JDialog d = ( JDialog )c;
						d.dispose();
					}
				}
			}
		});
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedItem = (String) templateList.getSelectedValue();
				if(selectedItem != null ){
					Container c = ((JButton)(e.getSource())).getParent();
					// get the container for this button.
					while( (c.getParent() != null)&&(!(c instanceof JDialog))) {
						c = c.getParent();
					}
					if(c instanceof JDialog) {
						System.out.println("You selected : "+templateList.getSelectedValue());
						JDialog d = ( JDialog )c;
						prepareResponse(selectedItem);
						selectedTemplateName = templateList.getSelectedValue();
						d.dispose();
					}
				}
			}
		});
	}

	/***
	 * To get the selected Template from the List
	 * @param selectedValue
	 * @return
	 */
	public void prepareResponse(String selectedValue) {
		if(!selectedValue.isEmpty() 
				&& teamplateMap.containsKey(selectedValue)) {
			appDataModel = ApplicationContextProvider.getApplicationContext()
					.getBean("appDataModel", ApplicationDataModel.class);
			appDataModel.setSelectedTemplateId(teamplateMap.get(selectedValue));
			appDataModel.setSelectedTemplateName(selectedValue);
			selectedTemplateName = selectedValue;
		}
	}
	
	 public void paintComponent(Graphics g) {
		 final RoundRectangle2D frameShape =
	                new RoundRectangle2D.Double(0,0,320,200,50,50);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fill(frameShape);
    }
	 
	 public String getSelectedTemplateName() {
		 return selectedTemplateName;
	 }
}
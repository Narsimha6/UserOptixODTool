package com.useroptix.odtool.to;

public class ApplicationDataModel {
	
	private String userName;
	private String password;
	private String salesForceUserName;
	private String salesForcePassword;
	private String apiKey;
	private Long selectedTemplateId;
	private String selectedTemplateName;
	private String selectedEndPoint;
	private String userView;
	private String projectName;
	private Long projectId;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalesForceUserName() {
		return salesForceUserName;
	}
	public void setSalesForceUserName(String salesForceUserName) {
		this.salesForceUserName = salesForceUserName;
	}
	public String getSalesForcePassword() {
		return salesForcePassword;
	}
	public void setSalesForcePassword(String salesForcePassword) {
		this.salesForcePassword = salesForcePassword;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public Long getSelectedTemplateId() {
		return selectedTemplateId;
	}
	public void setSelectedTemplateId(Long selectedTemplateId) {
		this.selectedTemplateId = selectedTemplateId;
	}
	public String getSelectedTemplateName() {
		return selectedTemplateName;
	}
	public void setSelectedTemplateName(String selectedTemplateName) {
		this.selectedTemplateName = selectedTemplateName;
	}
	public String getSelectedEndPoint() {
		return selectedEndPoint;
	}
	public void setSelectedEndPoint(String selectedEndPoint) {
		this.selectedEndPoint = selectedEndPoint;
	}
	public String getUserView() {
		return userView;
	}
	public void setUserView(String userView) {
		this.userView = userView;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
}

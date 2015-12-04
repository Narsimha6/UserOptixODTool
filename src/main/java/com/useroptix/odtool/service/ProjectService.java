/*
 * Created on 14-Oct-2015
 *  
 */
package com.useroptix.odtool.service;

import java.util.List;
import java.util.Map;

import com.useroptix.odtool.bo.EndPoints;
import com.useroptix.odtool.bo.Project;
import com.useroptix.odtool.bo.ProjectTemplateProperties;
import com.useroptix.odtool.to.UserTo;

/**
 * Services related to the GUI configuration
 * 
 * @author narasimhar
 * 
 */
public interface ProjectService {

	public List<EndPoints> getAllEndPoints();
	
	public List<ProjectTemplateProperties> getRecentProjects();

	public Boolean isValidUser(UserTo user);

	public Map<String, Long> getTemplateForEndpOint(String endPoint, String language);
	
	public Long saveProjectTemplateProperties(ProjectTemplateProperties ptpObject);
	
	public Long saveProject(Project ptpObject);

	public Boolean isProjectNameOrgIdExists(String projectName, Long orgId);
}

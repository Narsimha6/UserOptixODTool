package com.useroptix.odtool.dao;

import java.util.List;
import java.util.Map;

import com.useroptix.odtool.bo.EndPoints;
import com.useroptix.odtool.bo.ProjectTemplateProperties;
import com.useroptix.odtool.bo.UserOptixObject;
import com.useroptix.odtool.to.UserTo;

public interface ProjectDAO {
	public List<EndPoints> getEndPoints();
	
	public List<ProjectTemplateProperties> getrecentProjects();

	public Boolean isValidUser(UserTo user) throws Exception;

	public Map<String, Long> getTemplateForEndpOint(String endPoint, String language);

	public <T extends UserOptixObject> T saveOrUpdate(T entity) ;

	public Boolean isProjectNameOrgIdExists(String projectName, Long orgId);
}

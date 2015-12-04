package com.useroptix.odtool.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.useroptix.odtool.bo.EndPoints;
import com.useroptix.odtool.bo.Project;
import com.useroptix.odtool.bo.ProjectTemplateProperties;
import com.useroptix.odtool.dao.ProjectDAO;
import com.useroptix.odtool.service.ProjectService;
import com.useroptix.odtool.to.UserTo;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectDAO projectDao;

	@Override
	public List<EndPoints> getAllEndPoints() {
		return projectDao.getEndPoints();
	}

	@Override
	public List<ProjectTemplateProperties> getRecentProjects() {
		return projectDao.getrecentProjects();
	}

	@Override
	public Boolean isValidUser(UserTo user) {
		try {
			return projectDao.isValidUser(user);
		} catch (Exception e) {
			System.out.println("Invalid login username/password.");
		}
		return Boolean.FALSE;
	}

	/***
	 * To get the templates for the given endpoint and language
	 * @param endpoint
	 * @param language
	 * @return templateMap
	 */
	@Override
	public Map<String, Long> getTemplateForEndpOint(String endPoint, String language) {
		return projectDao.getTemplateForEndpOint(endPoint, language);
	}

	/***
	 * To save the data in PTP table
	 * @param ProjectTemplateProperties
	 * @return ptp_id
	 */
	@Override
	public Long saveProjectTemplateProperties(ProjectTemplateProperties ptpObject) {
		ptpObject = projectDao.saveOrUpdate(ptpObject);
		return Long.valueOf(ptpObject.getId()); 
	}
	
	/***
	 * To save the data in Project table
	 * @param Project object
	 * @return project_id
	 */
	@Override
	public Long saveProject(Project projectObject) {
		projectObject = projectDao.saveOrUpdate(projectObject);
		return Long.valueOf(projectObject.getProjectId()); 
	}
	
	/***
	 * 
	 */
	@Override
	public Boolean isProjectNameOrgIdExists(String projectName, Long orgId) {
		return  projectDao.isProjectNameOrgIdExists(projectName, orgId);
	}
}

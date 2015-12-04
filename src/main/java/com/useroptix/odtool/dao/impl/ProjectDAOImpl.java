package com.useroptix.odtool.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.useroptix.odtool.bo.EndPoints;
import com.useroptix.odtool.bo.ProjectTemplateProperties;
import com.useroptix.odtool.bo.UserOptixObject;
import com.useroptix.odtool.dao.ProjectDAO;
import com.useroptix.odtool.to.TemplateTo;
import com.useroptix.odtool.to.UserTo;
import com.useroptix.odtool.utils.JersyClientUtil;

@Repository
public class ProjectDAOImpl implements ProjectDAO {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public List<EndPoints> getEndPoints() {
		return (List<EndPoints>) entityManager.createQuery("from EndPoints ep",
				EndPoints.class).getResultList();
	}
	
	public List<ProjectTemplateProperties> getrecentProjects() {
		return (List<ProjectTemplateProperties>) entityManager.createQuery("from ProjectTemplateProperties order by createdDate",
				ProjectTemplateProperties.class).setMaxResults(5).getResultList();
	}

	@Override
	public Boolean isValidUser(UserTo user) throws Exception {
		if (user == null)
			return null;
		Query query = null;
		query = entityManager.createNativeQuery(VALID_LOGGEDIN_USER);
		query.setParameter("userName", user.getUsername());
		query.setParameter("password", user.getPassword());
		System.out.println(" Query : "+VALID_LOGGEDIN_USER);
		Integer result = Integer.parseInt( query.getSingleResult().toString());
		return ((result != null && result == 1) ? Boolean.TRUE : Boolean.FALSE);
	}
	
	/***
	 * To get the templates for the given endpoint and language
	 * @param endpoint
	 * @param language
	 * @return templateMap
	 */
	@Override
	public Map<String, Long> getTemplateForEndpOint(String endPoint, String language) {
		if (Objects.isNull(endPoint) || endPoint.isEmpty()) {
			System.out.println("Invalid EndPoint to get the Templates.");
			return null;
		}	
		Map<String, Long> templateMap;
		TemplateTo templateTo = new TemplateTo();
		templateTo.setEndpoint(endPoint);
		templateTo.setLanguage(language);
		templateMap = JersyClientUtil.getTemplates(endPoint, language);
		return templateMap;
	}
	
	@Transactional
	public <T extends UserOptixObject> T saveOrUpdate(T entity) {
		return entityManager.merge(entity);
	}
	
	@Override
	public Boolean isProjectNameOrgIdExists(String projectName, Long orgId) {
		if (projectName == null)
			return null;
		Query query = null;
		query = entityManager.createNativeQuery(VALID_PROJECT_NAME_ORG_ID);
		query.setParameter("projectName", projectName);
		query.setParameter("orgId", orgId);
		System.out.println(" Query : "+VALID_PROJECT_NAME_ORG_ID);
		Integer result = Integer.parseInt( query.getSingleResult().toString());
		return ((result != null && result == 1) ? Boolean.TRUE : Boolean.FALSE);
	}
	
	private static final String VALID_LOGGEDIN_USER = "select case when count(user_id) >0 then true else"
			+ " false end from user where upper(username)=upper(:userName) and upper(password)=upper(:password)";
	
	private static final String VALID_PROJECT_NAME_ORG_ID = "select case when count(project_id) >0 then true else"
			+ " false end from project where upper(name) = upper(:projectName) and org_id = :orgId ";
	
	/*private static final String GET_TEMPLATES_FOR_ENDPOINT = "select td.template_description, t.template_id from templates t "
			+ " inner join template_description td on td.template_id = t.template_id "
			+ " where td.language = :language and t.endpoint = :endPoint "; */
}

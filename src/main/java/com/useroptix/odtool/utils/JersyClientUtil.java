package com.useroptix.odtool.utils;

import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.useroptix.odtool.to.OrganizationTo;
import com.useroptix.odtool.to.ProjectTo;
import com.useroptix.odtool.to.TemplateTo;
import com.useroptix.odtool.to.UserTo;

@Service
public class JersyClientUtil {

	@Value("${USER_OPTIX_URL}")
	public String userOptixURL; 

	@Value("${OD_TOOL_OAUTH_URL}")
	public String oAuthURL; 

	public String callGet(String handshakeUrl) throws Exception {

		String response = null;
		//		logger.info("Calling Search Organization handshake api for updateOrganizationId");

		try {
			WebResource webResource = getJerseyClient(handshakeUrl);
			response = webResource.type(MediaType.APPLICATION_JSON).get(String.class);
		} catch (Exception e) {
			//			logger.warn("Error in calling handshake api, trying again...", e);
			Thread.sleep(1000);

			WebResource webResource = getJerseyClient(handshakeUrl);
			response = webResource.type(MediaType.APPLICATION_JSON).get(String.class);
		}
		return response;
	}

	/***
	 * Method to call the useroptix createUser by passing userTo
	 * @param userTo
	 * @return user_id
	 */
	public Long createUser(UserTo userTo) {
		userTo = new UserTo();
		userTo.setOrg_id(1L);
		userTo.setUsername("Menlo");
		userTo.setEmail("Menlo@menlo.com");
		userTo.setPassword("Menlo");
		userTo.setFirstname("Menlo_FName");
		userTo.setLastname("Menlo_LName");

		try {
			WebResource webResource = getJerseyClient(userOptixURL + "CreateUser");
			userTo = webResource.type(MediaType.APPLICATION_JSON).post(UserTo.class, userTo);
			if(userTo != null && userTo.getOrg_id() != null) {
				return userTo.getUser_id() ;
			}
		} catch (Exception e) {
			System.out.println("Error in getting the response from REST call CreateUser : "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * Method to call the useroptix login by passing user credentials
	 * @param userTo
	 * @return user_id
	 */
	public UserTo callLoginUser(UserTo userTo) throws ConnectException {
		if(Objects.isNull(userTo.getUsername()) || Objects.isNull(userTo.getPassword())) {
			System.out.println("Invalid login credentials.");
			return null;
		}
		try {
			WebResource webResource = getJerseyClient("http://localhost:8080/useroptix/j_spring_security_check");
			String postBodyStr = "username=" + userTo.getUsername() + "&password="+userTo.getPassword();
			String respStr = webResource.type(MediaType.APPLICATION_FORM_URLENCODED ).post(String.class, postBodyStr);
			if(Objects.nonNull(respStr) && !respStr.isEmpty() && respStr.contains("token")) {
				//response contains date values so need to provide the date formate.
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
				Type userLoginResults = new TypeToken<Map<String, String>>(){}.getType();
				//                // Convert the data from the response JSON object
				Map<String, String> userLoginResultMap = gson.fromJson(respStr, userLoginResults);
				if(userLoginResultMap != null 
						&& !userLoginResultMap.isEmpty() 
						&& userLoginResultMap.containsKey("token")) {
					userTo.setToken(String.valueOf(userLoginResultMap.containsKey("token")));
				}
				System.out.println(" ----->>> " + userLoginResultMap.get("token"));
			}
		} catch (UniformInterfaceException e) {
			System.out.println("Error in getting the response from REST call LoginUser : (401 Unauthorized) "+e.getMessage());
		} catch (Exception e) {
			System.out.println("Error in getting the response from REST call CreateUser : "+e.getMessage());
		}
		return userTo;
	}

	/***
	 * Method to call getTemplates by passing 
	 * @param userTo
	 * @return user_id
	 * Example URL : 108.168.227.158/GetTemplates?endpoint=salesforce&language=en
	 */
	public static Map<String, Long> getTemplates(String endPoint, String language) {
		System.out.println(" EP : "+endPoint);
		System.out.println(" language : "+language);
		String response = null;
		Map<String, Long> templateMap = null;
		if(language == null || !language.isEmpty()) {
			language = "en";
		}
		String urlParamStr = "http://108.168.227.158/GetTemplates?endpoint="+endPoint+"&language="+language;
		try {
			WebResource webResource = getJerseyClient(urlParamStr);
			response = webResource.type(MediaType.APPLICATION_JSON).get(String.class);
		} catch (Exception e) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			WebResource webResource = getJerseyClient(urlParamStr);
			response = webResource.type(MediaType.APPLICATION_JSON).get(String.class);
		}
		List<TemplateTo> templateList = new ArrayList<TemplateTo>();
		if(response != null && !response.isEmpty()) {

			Gson gson = new Gson();
			Type templateResults = new TypeToken<Map<String, List<TemplateTo>>>(){}.getType();
			// Convert the data from the response JSON object
			Map<String, List<TemplateTo>> templateListMap = gson.fromJson(response, templateResults);
			templateList = templateListMap.get("templates");
			if(templateListMap.get("templates") != null && !templateListMap.get("templates").isEmpty() ) {
				templateMap = new HashMap<String, Long>();
				for (TemplateTo templateTo : templateList) {
					templateMap.put(templateTo.getDescription(), templateTo.getId());
				}
			}
		} 
		System.out.println(response);
		return templateMap;
	}

	/***
	 * Method to call CreateOrganization by passing the OrganizationTo
	 * @param OrganizationTo
	 * @return org_id
	 */
	public Long createOrganization(OrganizationTo orgTo) {
		orgTo = new OrganizationTo();
		orgTo.setName("Menlo");
		orgTo.setAddress1("Pioneer Towers");
		orgTo.setAddress2("Madhapur");
		orgTo.setCity("Hyderabad");
		orgTo.setState("AP");
		orgTo.setPostal_code("AP001");
		orgTo.setCountry("AE");

		try {
			WebResource webResource = getJerseyClient(userOptixURL + "CreateOrganization");
			orgTo = webResource.type(MediaType.APPLICATION_JSON).post(OrganizationTo.class, orgTo);
			if(orgTo != null && orgTo.getOrg_id() != null) {
				return orgTo.getOrg_id() ;
			}
		} catch (Exception e) {
			System.out.println("Error in getting the response from REST call CreateOrganization : "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * Method to call CreateOrganization by passing the ProjectTo
	 * @param ProjectTo
	 * @return project_id
	 */
	public Long createProject(ProjectTo projectTo) {
		projectTo = new ProjectTo();
		projectTo.setOrg_id(1L);
		projectTo.setOrg_name("AP");
		projectTo.setName("Menlo");
		projectTo.setShort_name("Pioneer Towers");
		projectTo.setOwner_username("Madhapur");

		try {
			WebResource webResource = getJerseyClient(userOptixURL + "CreateProject");
			projectTo = webResource.type(MediaType.APPLICATION_JSON).post(ProjectTo.class, projectTo);
			if(projectTo != null && projectTo.getProject_id() != null) {
				return projectTo.getProject_id() ;
			}
		} catch (Exception e) {
			System.out.println("Error in getting the response from REST call CreateProject : "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		JersyClientUtil cl = new JersyClientUtil();
		//		System.out.println(cl.userOptixURL);
		TemplateTo t = new TemplateTo();
		t.setEndpoint("salesforce");
		t.setLanguage("en");
		Map<String, Long> templateList = JersyClientUtil.getTemplates("salesforce","en");
		System.out.println(templateList.size());
	}

	/**
	 * Get Jersey client to interact with any REST API
	 * @param url
	 * @return
	 */
	public static WebResource getJerseyClient(String url){
		if(url == null || url.isEmpty()){
			return null;
		}
		Client client = null;

		ClientConfig clientConfigRegisterUser = new DefaultClientConfig();
		clientConfigRegisterUser.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,Boolean.TRUE);

		client = Client.create(clientConfigRegisterUser);

		return client.resource(url);
	}
}

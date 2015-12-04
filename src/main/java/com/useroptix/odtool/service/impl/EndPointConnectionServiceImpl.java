package com.useroptix.odtool.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.useroptix.odtool.dao.ProjectDAO;
import com.useroptix.odtool.service.EndPointConnectionService;

@Service
public class EndPointConnectionServiceImpl implements EndPointConnectionService {
	
	@Autowired
	private ProjectDAO projectDao;

	private static String SALES_FORCE_LOGIN_URL = "https://login.salesforce.com/services/Soap/u/35.0";
	
	/***
	 * This method will allow us to connect to SFDC
	 * @param userName
	 * @param password
	 * @param token
	 * @return EnterpriseConnection
	 * 
	 */
	@Override
	public PartnerConnection connectToSalesForce(String userName, String password, String token) throws ConnectionException {
		ConnectorConfig config = new ConnectorConfig();
		PartnerConnection connection = null;
		config.setAuthEndpoint(SALES_FORCE_LOGIN_URL);
//		config.setServiceEndpoint(SALES_FORCE_LOGIN_URL);
		String instanceUri = "login.salesforce.com";
//		config.setRestEndpoint("https://na1.salesforce.com/services/data/v26.0/sobjects/");
		config.setUsername(userName);
		config.setPassword(password);
		try {
			config.setConnectionTimeout(100000000);
			config.setTraceFile(System.getProperty("user.dir")+"/sf1.log");
			config.setUseChunkedPost(Boolean.TRUE);
			config.setTraceMessage(true);
			config.setPrettyPrintXml(true);
			config.setProxyUsername(userName);
			config.setProxyPassword(password);
			Long startTime = System.currentTimeMillis();
			System.out.println("Start Time : " + new Date(startTime));
			connection = new PartnerConnection(config);
			ConnectorConfig config2 = new ConnectorConfig();
			config2.setSessionId(config.getSessionId());
			// The endpoint for the Bulk API service is the same as for the normal
			// SOAP uri until the /Soap/ part. From here it's '/async/versionNumber'
			String soapEndpoint = config.getServiceEndpoint();
			String apiVersion = "35.0";
			String restEndpoint = soapEndpoint.substring(0, soapEndpoint.indexOf("Soap/"))
			+ "async/" + apiVersion;
			config2.setRestEndpoint(restEndpoint);
			// This should only be false when doing debugging.
			config2.setCompression(true);
			// Set this to true to see HTTP requests and responses on stdout
//			config2.setTraceMessage(false);
//			config2.setServiceEndpoint("https://ap1.salesforce.com/services/Soap/u/35.0/00D90000000eShp");
//			DescribeGlobalResult global = connection.describeGlobal();
//			System.out.println(global);
//			connection.logout();
//			connection = Connector.newConnection(config);
			Long endTime = System.currentTimeMillis(); 
			System.out.println("You are connected to SFDC : " + connection + "  -> EndTime : "+ new Date(endTime) );
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	private static LoginResult loginToSalesforce(
	         final String username,
	         final String password,
	         final String loginUrl) throws ConnectionException {
	     final ConnectorConfig config = new ConnectorConfig();
	     config.setAuthEndpoint(loginUrl);
	     config.setServiceEndpoint(loginUrl);
	     config.setManualLogin(true);
	     return null;//(new EnterpriseConnection(config)).login(username, password);
	 }
}

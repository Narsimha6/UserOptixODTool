/*
 * Created on 14-Oct-2015
 *  
 */
package com.useroptix.odtool.service;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;


/**
 * Service o connect EndPoints
 * 
 * @author narasimhar
 * 
 */
public interface EndPointConnectionService {

	public PartnerConnection connectToSalesForce(String userName, String password, String token) 
			throws ConnectionException;
}

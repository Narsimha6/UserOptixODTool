package com.useroptix.odtool.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * @author narasimhar 
 */
public class SpringUtilities {

	private static GenericObjectPool connectionPool = null;

	
	public static DataSource setUp() throws Exception {
		// Load JDBC Driver class.
		Class.forName(ObjectToolProperty.DB_DRIVER.getValue()).newInstance();

		//
		// Creates an instance of GenericObjectPool that holds our
		// pool of connections object.
		//
		connectionPool = new GenericObjectPool();
		connectionPool.setMaxActive(10);

		//
		// Creates a connection factory object which will be use by
		// the pool to create the connection object. We passes the
		// JDBC url info, username and password.
		//
		ConnectionFactory cf = new DriverManagerConnectionFactory(
				ObjectToolProperty.DB_URL.getValue(),
				ObjectToolProperty.DB_USER_NAME.getValue(),
				ObjectToolProperty.DB_PASSWORD.getValue());

		//
		// Creates a PoolableConnectionFactory that will wraps the
		// connection object created by the ConnectionFactory to add
		// object pooling functionality.
		//
		PoolableConnectionFactory pcf =
				new PoolableConnectionFactory(cf, connectionPool,
						null, null, false, true);
		return new PoolingDataSource(connectionPool);
	}
	
	public static void main(String[] args) throws Exception {
		getConnection();
    }
	
	public static void getConnection() throws Exception {
		DataSource dataSource = setUp();

		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = dataSource.getConnection();

			stmt = conn.prepareStatement("select * from term_information");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				System.out.println("Username: " + rs.getString("term_being_polled"));
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}
}
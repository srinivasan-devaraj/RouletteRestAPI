package com.roulette.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;


public class ConnectionPool {
	
	private static DataSource dataSource = null;
	
	public ConnectionPool() {
		if(dataSource == null) {
			dataSource = setupDataSource();
		}
	}
	
	public static BasicDataSource setupDataSource() {
		BasicDataSource source = new BasicDataSource();
		source.setDriverClassName("com.mysql.jdbc.Driver");
		source.setUsername("root");
		source.setPassword("");
		source.setUrl("jdbc:mysql://localhost:3306/Roulette");
		return source;
	}
	
	public static Connection getConnection() {
		if(dataSource == null) {
			dataSource = setupDataSource();
		}
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}

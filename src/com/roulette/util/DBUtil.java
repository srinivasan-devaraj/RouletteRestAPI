package com.roulette.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.roulette.db.ConnectionPool;

public class DBUtil {
	
	public static Long addUser(String name, String email) throws SQLException {
		if(hasUser(email)) {
			return getUserID(email);
		}
		ConnectionPool.getStatement().executeUpdate("insert into User (Name, Email_Id) values ('"+name+"','"+email+"')");
		return getUserID(email);
	}
	
	public static Long getUserID(String email) throws SQLException {
		ResultSet rs = ConnectionPool.getStatement().executeQuery("select Id from User where Email_Id = '"+email+"'");
		if(rs.next()) {
			return rs.getLong("Id");
		}else {
			return 0L;
		}
	}
	
	public static boolean hasUser(String email) {
		try {
			return !getUserID(email).equals(0L);
		} catch (SQLException e) {
			return false;
		}
	}
}

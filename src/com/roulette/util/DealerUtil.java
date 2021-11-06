package com.roulette.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.roulette.db.ConnectionPool;

public class DealerUtil {

	public static Long addDealer(String name, String email, Long casino_id) throws SQLException {
		if (hasDealer(email, casino_id)) {
			return getDealerID(email, casino_id);
		}
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			stmt.executeUpdate("insert into Dealer (Name, Email_Id, Casino_Id) values ('" + name + "','" + email + "','"
					+ casino_id + "')");
		}
		return getDealerID(email, casino_id);
	}

	public static Long getDealerID(String email, Long casino_id) throws SQLException {
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt.executeQuery(
					"select Id from Dealer where Email_Id = '" + email + "' and Casino_Id = " + casino_id)) {
				if (rs.next()) {
					return rs.getLong("Id");
				} else {
					return 0L;
				}
			}
		}
	}

	public static boolean hasDealer(String email, Long casino_id) {
		try {
			return !getDealerID(email, casino_id).equals(0L);
		} catch (SQLException e) {
			return false;
		}
	}

}

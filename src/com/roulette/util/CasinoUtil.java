package com.roulette.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.roulette.dao.CasinoDAO;
import com.roulette.dao.DealerDAO;
import com.roulette.db.ConnectionPool;

public class CasinoUtil {

	public static void addCasino(String name, String email) throws SQLException {
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			stmt.executeUpdate("insert into Casino (Name, Email_Id) values ('" + name + "','" + email + "')");
		}
	}

	public static Long getCasinoID(String email) throws SQLException {
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt.executeQuery("select Id from Casino where Email_Id = '" + email + "'");) {
				if (rs.next()) {
					return rs.getLong("Id");
				} else {
					return 0L;
				}
			}
		}
	}

	public static Long getCasinoID(Long gameID) throws SQLException {
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt.executeQuery(
					"select dealer.Casino_Id Casino_Id from Game game left join Dealer dealer on game.DealerId = dealer.Id where game.Id = "
							+ gameID)) {
				if (rs.next()) {
					return rs.getLong("Casino_Id");
				} else {
					return 0L;
				}
			}
		}
	}

	public static boolean isValidCasino(Long casino_id) throws SQLException {
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt.executeQuery("select Id from Casino where Id = " + casino_id)) {
				if (rs.next()) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasCasino(String email) {
		try {
			return !getCasinoID(email).equals(0L);
		} catch (SQLException e) {
			return false;
		}
	}

	public static Long getBalanceAmount(Long casinoId) throws SQLException {
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt.executeQuery("select Amount from Casino where Id = " + casinoId)) {
				if (rs.next()) {
					return rs.getLong("Amount");
				}
			}
		}
		return 0L;
	}

	public static synchronized void updateBalance(Long casinoId, Long amount) throws SQLException {
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			stmt.executeUpdate("update Casino set Amount = Amount + " + amount + " where Id = " + casinoId);
		}
	}

	public static List<DealerDAO> getAllDealers(Long casino_id) throws SQLException {
		List<DealerDAO> dealersList = new ArrayList<>();
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt
					.executeQuery("select Id, Name, Email_Id from Dealer where Casino_Id = " + casino_id)) {
				while (rs.next()) {
					dealersList.add(
							new DealerDAO(rs.getLong("Id"), rs.getString("Name"), rs.getString("Email_Id"), casino_id));
				}
			}
		}
		return dealersList;
	}
	
	public static List<CustomJSONObject> getAllCasino() throws SQLException {
		List<CustomJSONObject> casinoList = new ArrayList<>();
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt.executeQuery("select Id, Name from Casino where Id != 0")) {
				while (rs.next()) {
					casinoList.add(new CustomJSONObject(false).put("id", rs.getLong("Id")).put("name", rs.getString("Name")));
				}
			}
		}
		return casinoList;
	}

}

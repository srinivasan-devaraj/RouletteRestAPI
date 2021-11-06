package com.roulette.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;

import com.roulette.db.ConnectionPool;

public class UserUtil {

	public static Long addUser(String name, String email) throws SQLException {
		if (hasUser(email)) {
			return getUserID(email);
		}
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			stmt.executeUpdate("insert into User (Name, Email_Id) values ('" + name + "','" + email + "')");
		}
		return getUserID(email);
	}

	public static Long getUserID(String email) throws SQLException {
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt.executeQuery("select Id from User where Email_Id = '" + email + "'")) {
				if (rs.next()) {
					return rs.getLong("Id");
				} else {
					return 0L;
				}
			}
		}
	}

	public static boolean hasUser(String email) {
		try {
			return !(getUserID(email).equals(0L));
		} catch (SQLException e) {
			return false;
		}
	}

	public static Long getBalanceAmount(Long userId) throws SQLException {
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt.executeQuery("select Amount from User where Id = " + userId)) {
				if (rs.next()) {
					return rs.getLong("Amount");
				}
			}
		}
		return 0L;
	}

	public static synchronized void updateBalance(Long userId, Long amount, boolean isAddition) throws SQLException {
		if (isAddition) {
			try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
				stmt.executeUpdate("update User set Amount = Amount + " + amount + " where Id = " + userId);
			}
		} else {
			Long balance = getBalanceAmount(userId);
			if (balance.compareTo(amount) >= 0) {
				try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
					stmt.executeUpdate("update User set Amount = Amount + " + amount + " where Id = " + userId);
				}
			} else {
				// TODO throw Insufficent Balance
				// response.put("message", "Insufficent balance");
			}
		}
	}

	private static Long getBetId(Long userId, Long gameId, Long betTime) throws SQLException {
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt.executeQuery("select Id from Bet where User_Id = " + userId + " and Game_Id = "
					+ gameId + " and Time = " + betTime)) {
				if (rs.next()) {
					return rs.getLong("Id");
				} else {
					return 0L;
				}
			}
		}
	}

	public static synchronized JSONObject cashOut(Long userId, Long amount) throws SQLException {
		Long balance = getBalanceAmount(userId);
		JSONObject response = new JSONObject();
		if (balance.compareTo(amount) >= 0) {

		} else {
			response.put("message", "Insufficent balance");
		}
		return response;
	}

	public static synchronized JSONObject betOnGame(Long userId, Long gameId, Long casinoId, Long amount, int number)
			throws SQLException {
		Long balance = getBalanceAmount(userId);
		JSONObject response = new JSONObject();
		if (balance.compareTo(amount) >= 0) {
			String gameStatus = GameUtil.getStatus(gameId);
			if (gameStatus.equals(Constants.GAME_OPEN)) {
				Long currentTime = System.currentTimeMillis();
				try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
					stmt.executeUpdate("insert into Bet (User_Id, Game_Id, Time, Number, Amount) values (" + userId
							+ "," + gameId + "," + currentTime + "," + number + "," + amount + ")");
				}
				updateBalance(userId, amount * -1, false);
				CasinoUtil.updateBalance(casinoId, amount);
				response.put("bet_id", getBetId(userId, gameId, currentTime));
				response.put("balance", getBalanceAmount(userId));
				response.put("message", "Bet placed successfully");
			} else {
				response.put("message", "Game not in open state");
			}
		} else {
			response.put("message", "Insufficent balance");
		}
		return response;
	}
}

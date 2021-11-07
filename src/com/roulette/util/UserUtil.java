package com.roulette.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;

import com.roulette.db.ConnectionPool;
import com.roulette.util.Constants.CustomResponse;

public class UserUtil {

	public static void addUser(String name, String email) throws SQLException {
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			stmt.executeUpdate("insert into User (Name, Email_Id) values ('" + name + "','" + email + "')");
		}
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

	public static synchronized JSONObject updateBalance(Long userId, Long amount, boolean isAddition) throws SQLException {
		CustomJSONObject response = new CustomJSONObject(false);
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
				response.respond(CustomResponse.INSUFFICIENT_BALANCE);
			}
		}
		return response;
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

	public static synchronized CustomJSONObject betOnGame(Long userId, Long gameId, Long casinoId, Long amount, int number)
			throws SQLException {
		Long balance = getBalanceAmount(userId);
		CustomJSONObject response = new CustomJSONObject();
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
				response.put("bet_id", getBetId(userId, gameId, currentTime))
						.put("balance", getBalanceAmount(userId))
						.message("Bet placed successfully");
			} else {
				response.respond(CustomResponse.GAME_NOT_OPNED);
			}
		} else {
			response.respond(CustomResponse.INSUFFICIENT_BALANCE);
		}
		return response;
	}
	
	public static boolean isValidUser(Long userID) throws SQLException{
		try(Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()){
			try(ResultSet rs = stmt.executeQuery("select Id from User where Id = "+userID)){
				if(rs.next()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void updateCasino(Long userId, Long casinoId) throws SQLException{
		try(Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()){
			stmt.executeUpdate("update User set Casino_Id = "+casinoId+" where Id = "+userId);
		}
	}
	
	public static Long getCurrentCasinoId(Long userId) throws SQLException{
		try(Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()){
			try(ResultSet rs = stmt.executeQuery("select Casino_Id from User where Id = "+userId)){
				if(rs.next()) {
					return rs.getLong("Casino_Id");
				}
			}
		}
		return 0L;
	}
}

package com.roulette.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.roulette.db.ConnectionPool;

public class GameUtil {

	public static synchronized JSONObject startGame(Long dealerId) throws SQLException {
		long currentTime = System.currentTimeMillis();
		JSONObject response = new JSONObject();
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			stmt.executeUpdate("insert into Game (StartTime, DealerId) values (" + currentTime + "," + dealerId + ")");
			try (ResultSet rs = stmt.executeQuery(
					"select Id from Game where StartTime = " + currentTime + " and DealerId = " + dealerId)) {
				if (rs.next()) {
					response.put("game_id", rs.getLong("Id"));
				}
			}
		}
		response.put("start_time", currentTime);
		response.put("dealer_id", dealerId);
		return response;
	}

	public static void updateGame(Long gameId, String status) throws SQLException {
		updateGame(gameId, status, 0);
	}

	public static void updateGame(Long gameId, String status, int thrownNumber) throws SQLException {
		StringBuilder sb = new StringBuilder("update Game set Status = '").append(status).append("'");
		if (thrownNumber != 0) {
			sb.append(", ThrownNumber = ").append(thrownNumber);
		}
		if(status.equals(Constants.GAME_CLOSE)) {
			sb.append(", EndTime = ").append(System.currentTimeMillis());
		}
		sb.append(" where Id = ").append(gameId);
		if(status.equals(Constants.GAME_CLOSE)) {
			sb.append(" and Status = 'open'");
		}
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(sb.toString());
		}
	}

	private static int getRandomNumber() {
		return ThreadLocalRandom.current().nextInt(1, 37); // 1 to 36
	}

	public static String getStatus(Long gameId) throws SQLException {
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt.executeQuery("select Status from Game where Id = " + gameId)) {
				if (rs.next()) {
					return rs.getString("Status");
				}
			}
		}
		return Constants.GAME_UNKNOWN;
	}

	public static JSONArray getAllGames(Long casinoId) throws SQLException {
		JSONArray responseArray = new JSONArray();
		StringBuilder query = new StringBuilder(
				"SELECT game.Id Id, dealer.Id DealerId, dealer.Casino_Id CasinoId  FROM Game game left join Dealer dealer on game.DealerId = dealer.Id where game.Status = 'open'");
		if (casinoId != null) {
			query.append(" and dealer.Casino_Id = ").append(casinoId);
		}
		try (Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt.executeQuery(query.toString())) {
				while (rs.next()) {
					JSONObject eachGame = new JSONObject();
					eachGame.put("game_id", rs.getLong("Id"));
					eachGame.put("dealer_id", rs.getLong("DealerId"));
					eachGame.put("casino_id", rs.getLong("CasinoId"));
					responseArray.add(eachGame);
				}
			}
		}

		return responseArray;
	}
	
	public static boolean isDealerOwnThisGame(Long dealerId, Long gameId) throws SQLException{
		try(Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()){
			try(ResultSet rs = stmt.executeQuery("select * from Game where Id = "+gameId+" and DealerId = "+dealerId)){
				if(rs.next()) {
					return true;
				}
			}
		}
		return false;
	}

	public static synchronized JSONObject throwBall(Long dealerId, Long casinoId, Long gameId) throws SQLException {
		JSONObject response = new JSONObject();
		if(!isDealerOwnThisGame(dealerId, gameId)) {
			response.put("message", "No suitable game for this dealer");
			return response;
		}
		String status = getStatus(gameId);
		
		if (status.equals(Constants.GAME_CLOSE)) {
			int thrownNumber = getRandomNumber();
			updateGame(gameId, Constants.GAME_THROWN, thrownNumber);
			response.put("thrown_number", thrownNumber);
			response.put("status", Constants.GAME_THROWN);
			WinnerUpdater winnerUpdateObj = new GameUtil().new WinnerUpdater(gameId, casinoId, thrownNumber);
			winnerUpdateObj.start();
		} else {
			response.put("status", status);
			response.put("message", "Game not in closed state");
		}
		return response;
	}

	private class WinnerUpdater extends Thread {
		Long game_id;
		int thrown_number;
		Long casino_id;
		
		WinnerUpdater(Long game_id, Long casino_id, int thrown_number){
			this.game_id = game_id;
			this.casino_id = casino_id;
			this.thrown_number = thrown_number;
		}

		public void run(){
			Map<Long, Long> userBetMap = new HashMap<Long, Long>();
			try(Connection conn = ConnectionPool.getConnection(); Statement stmt = conn.createStatement()){
				try(ResultSet rs = stmt.executeQuery("select User_Id, Amount from Bet where Game_Id = "+game_id+" and Status = 'in-progress' and Number = "+thrown_number)){
					while(rs.next()) {
						Long userId = rs.getLong("User_Id");
						Long amount = userBetMap.getOrDefault(userId, 0L) + rs.getLong("Amount");
						userBetMap.put(userId, amount);
					}
				}
				Long total_amount = 0L;
				for(Long userId : userBetMap.keySet()) {
					Long currentWin = (userBetMap.get(userId)) * 2;
					UserUtil.updateBalance(userId, currentWin, true);
					total_amount += currentWin;
				}
				CasinoUtil.updateBalance(casino_id, total_amount * -1);
				stmt.executeUpdate("update Bet set Status = 'won' where Game_Id = "+game_id+" and Number = "+thrown_number);
				stmt.executeUpdate("update Bet set Status = 'lost' where Game_Id = "+game_id+" and Number != "+thrown_number);
			}catch(SQLException ex) {
				// Notify Admin
				ex.printStackTrace();
			}
			
			
		}
	}

}

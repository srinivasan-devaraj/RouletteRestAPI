package com.roulette.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONObject;

import com.roulette.db.ConnectionPool;
import com.roulette.entity.Bet;
import com.roulette.entity.Dealer;
import com.roulette.entity.Game;
import com.roulette.util.Constants.CustomResponse;

/**
 * Util Class used to provide all the helper methods for Game related functionlaities
 * @author srini
 */
public class GameUtil {

	/**
	 * Used to start the game by the given dealer
	 * @param dealerId
	 * @return
	 */
	public static synchronized JSONObject startGame(Long dealerId) {
		CustomJSONObject response = new CustomJSONObject(false);
		long currentTime = System.currentTimeMillis();
		Dealer dealer = new Dealer();
		dealer.setId(dealerId);

		Game game = new Game();
		game.setStart_time(currentTime);
		game.setDealer(dealer);
		game.setStatus(Constants.GAME_OPEN);

		Session session = null;
		Transaction transaction = null;
		try {
			session = ConnectionPool.getSession();
			transaction = session.beginTransaction();
			session.save(game);
			transaction.commit();
		} catch (Exception ex) {
			ConnectionPool.rollBack(transaction);
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		response.put("id", getGameID(dealerId, currentTime)).put("start_time", currentTime).put("status", Constants.GAME_OPEN);
		return response;
	}

	/**
	 * Returns the game id based on the dealer and time when its started.
	 * @param dealerId
	 * @param startTime
	 * @return
	 */
	public static Long getGameID(Long dealerId, Long startTime) {
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			Query q = session
					.createQuery("from Game game where game.start_time = :start_time and dealer_id = :dealer_id")
					.setLong("start_time", startTime).setLong("dealer_id", dealerId);
			Game game = (Game) q.uniqueResult();
			return game.getId();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return 0L;
	}

	/**
	 * Updates the game status in the specified value
	 * @param gameId
	 * @param status
	 */
	public static void updateGame(Long gameId, String status) {
		updateGame(gameId, status, 0);
	}

	/**
	 * Used to updates either game status or thrown number / both the values
	 * @param gameId
	 * @param status
	 * @param thrownNumber
	 */
	public static void updateGame(Long gameId, String status, int thrownNumber) {
		StringBuilder sb = new StringBuilder("update Game game set game.status = '").append(status).append("'");
		if (thrownNumber != 0) {
			sb.append(", game.thrown_number = ").append(thrownNumber);
		}
		if (status.equals(Constants.GAME_CLOSE)) {
			sb.append(", game.end_time = ").append(System.currentTimeMillis());
		}
		sb.append(" where game.id = ").append(gameId);
		if (status.equals(Constants.GAME_CLOSE)) {
			sb.append(" and game.status = 'open'");
		}

		Session session = null;
		Transaction transaction = null;
		try {
			session = ConnectionPool.getSession();
			transaction = session.beginTransaction();
			session.createQuery(sb.toString()).executeUpdate();
			transaction.commit();
		} catch (Exception ex) {
			ConnectionPool.rollBack(transaction);
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
	}

	/**
	 * Generated random number between 1 <= n <= 36
	 * @return
	 */
	private static int getRandomNumber() {
		return ThreadLocalRandom.current().nextInt(1, 37); // 1 to 36
	}

	/**
	 * Returns the current status of the specified game
	 * @param gameId
	 * @return
	 */
	public static String getStatus(Long gameId) {
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			Game game = (Game) session.get(Game.class, gameId);
			return game.getStatus();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return Constants.GAME_UNKNOWN;
	}
	
	/**
	 * Returns all the open games available in the specified casino id
	 * @param casinoId
	 * @return
	 */
	public static List<CustomJSONObject> getAllGames(Long casinoId) {
		Session session = null;
		List<CustomJSONObject> gamesListResponse = new ArrayList<>();
		try {
			session = ConnectionPool.getSession();
			Query query = session
					.createQuery("from Game game where game.status = 'open' and dealer.casino.id = :casino_id")
					.setLong("casino_id", casinoId);
			List<Game> gameList = query.list();
			for (Game game : gameList) {
				gamesListResponse.add(new CustomJSONObject(false).put("game_id", game.getId())
						.put("dealer_id", game.getDealer().getId())
						.put("casino_id", game.getDealer().getCasino().getId()));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return gamesListResponse;
	}

	/**
	 * Confirms whether the specified dealer is the owner of the specified game
	 * @param dealerId
	 * @param gameId
	 * @return
	 */
	public static boolean isDealerOwnThisGame(Long dealerId, Long gameId) {
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			Query q = session.createQuery("from Game game where game.id = :id and dealer_id = :dealer_id");
			q.setLong("id", gameId).setLong("dealer_id", dealerId);
			return q.uniqueResult() != null;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return false;
	}

	/**
	 * Used to throw the ball for the specified game
	 * @param dealerId
	 * @param casinoId
	 * @param gameId
	 * @return
	 */
	public static synchronized JSONObject throwBall(Long dealerId, Long casinoId, Long gameId) {
		CustomJSONObject response = new CustomJSONObject();
		String status = getStatus(gameId);

		if (status.equals(Constants.GAME_CLOSE)) {
			int thrownNumber = getRandomNumber();
			updateGame(gameId, Constants.GAME_THROWN, thrownNumber);
			response.put("thrown_number", thrownNumber).put("status", Constants.GAME_THROWN);
			WinnerUpdater winnerUpdateObj = new GameUtil().new WinnerUpdater(gameId, casinoId, thrownNumber);
			winnerUpdateObj.start();
		} else {
			response.put("status", status).respond(CustomResponse.GAME_NOT_CLOSED);
		}
		return response;
	}

	/**
	 * Thread used to update the game winners and losers in the background
	 * @author srini
	 */
	private class WinnerUpdater extends Thread {
		Long game_id;
		int thrown_number;
		Long casino_id;

		WinnerUpdater(Long game_id, Long casino_id, int thrown_number) {
			this.game_id = game_id;
			this.casino_id = casino_id;
			this.thrown_number = thrown_number;
		}

		/**
		 * Doubles the bet amount for the winners and deduct the overall winning amount from the casino's balance
		 */
		public void run() {
			Map<Long, Long> userBetMap = new HashMap<Long, Long>();

			Session session = null;
			Transaction transaction = null;
			try {
				session = ConnectionPool.getSession();
				transaction = session.beginTransaction();
				Query q = session.createQuery(
						"from Bet bet where game_id = :game_id and bet.status = :bet_status and bet.number = :thrown_number");
				q.setLong("game_id", game_id).setString("bet_status", Constants.BET_DEFAULT_STATUS)
						.setInteger("thrown_number", thrown_number);
				List<Bet> betList = (List<Bet>) q.list();
				for (Bet bet : betList) {
					Long userId = bet.getUser().getId();
					Long amount = userBetMap.getOrDefault(userId, 0L) + bet.getAmount();
					userBetMap.put(userId, amount);
				}
				Long total_amount = 0L;
				for (Long userId : userBetMap.keySet()) {
					Long currentWin = (userBetMap.get(userId)) * 2;
					UserUtil.updateBalance(userId, currentWin, true);
					total_amount += currentWin;
				}
				CasinoUtil.updateBalance(casino_id, total_amount * -1);
				Query winnerQuery = session.createQuery(
						"update Bet bet set bet.status = :status where game_id = :game_id and bet.number = :thrown_number")
						.setString("status", Constants.BET_STATUS_WON).setLong("game_id", game_id)
						.setInteger("thrown_number", thrown_number);
				winnerQuery.executeUpdate();
				Query loserQuery = session.createQuery(
						"update Bet bet set bet.status = :status where game_id = :game_id and bet.number != :thrown_number")
						.setString("status", Constants.BET_STATUS_LOSE).setLong("game_id", game_id)
						.setInteger("thrown_number", thrown_number);
				loserQuery.executeUpdate();
				transaction.commit();
			} catch (Exception ex) {
				ConnectionPool.rollBack(transaction);
				ex.printStackTrace();
			} finally {
				ConnectionPool.closeSession(session);
			}
		}
	}

}

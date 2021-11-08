package com.roulette.util;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONObject;

import com.roulette.db.ConnectionPool;
import com.roulette.entity.Bet;
import com.roulette.entity.Game;
import com.roulette.entity.User;
import com.roulette.util.Constants.CustomResponse;

/**
 * Util Class used to provide all the helper methods for User.class
 * @author srini
 */
public class UserUtil {

	/**
	 * Adds the specified user into the DB
	 * @param user
	 */
	public static void addUser(User user) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = ConnectionPool.getSession();
			transaction = session.beginTransaction();
			session.save(user);
			transaction.commit();
		} catch (Exception ex) {
			ConnectionPool.rollBack(transaction);
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
	}

	/**
	 * Returns the user id of the specified email id
	 * @param email
	 * @return
	 */
	public static Long getUserID(String email) {
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			Query q = session.createQuery("from User user where user.email_id = :email_id");
			q.setString("email_id", email);
			User user = (User) q.uniqueResult();
			return user.getId();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return 0L;
	}

	/**
	 * Confirms whether the specified email id already available in the db or not
	 * @param email
	 * @return
	 */
	public static boolean hasUser(String email) {
		return !(getUserID(email).equals(0L));
	}

	/**
	 * Returns the balance amount of the specified user
	 * @param userId
	 * @return
	 */
	public static Long getBalanceAmount(Long userId) {
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			User user = (User) session.get(User.class, userId);
			return user.getAmount();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return 0L;
	}

	/**
	 * Updates(increase/decrease) the balance amount of the specified user by the specified value
	 * @param userId
	 * @param amount
	 * @param isAddition
	 * @return
	 */
	public static synchronized JSONObject updateBalance(Long userId, Long amount, boolean isAddition) {
		CustomJSONObject response = new CustomJSONObject(false);
		if (isAddition) {
			updateBalance(userId, amount);
		} else {
			Long balance = getBalanceAmount(userId);
			if (balance.compareTo(amount) >= 0) {
				updateBalance(userId, amount * -1);
			} else {
				response.respond(CustomResponse.INSUFFICIENT_BALANCE);
			}
		}
		return response;
	}

	/**
	 * Updates the balance in the DB
	 * @param userId
	 * @param amount
	 */
	private static void updateBalance(Long userId, Long amount) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = ConnectionPool.getSession();
			transaction = session.beginTransaction();
			session.createQuery("update User user set user.amount = user.amount + :amount where user.id = :user_id")
					.setLong("amount", amount).setLong("user_id", userId).executeUpdate();
			transaction.commit();
		} catch (Exception ex) {
			ConnectionPool.rollBack(transaction);
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
	}

	/**
	 * Returns the Bet ID for the specified user id, game id and bet time
	 * @param userId
	 * @param gameId
	 * @param betTime
	 * @return
	 */
	private static Long getBetId(Long userId, Long gameId, Long betTime) {
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			Query q = session
					.createQuery("from Bet bet where game_id = :game_id and user_id = :user_id and bet.time = :time");
			q.setLong("game_id", gameId).setLong("user_id", userId).setLong("time", betTime);
			Bet bet = (Bet) q.uniqueResult();
			return bet.getId();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return 0L;
	}

	/**
	 * Place the specified amount bet on the specified number for the specified user and deducts the money for the bid from the user account
	 * @param userId
	 * @param gameId
	 * @param casinoId
	 * @param amount
	 * @param number
	 * @return
	 */
	public static synchronized CustomJSONObject betOnGame(Long userId, Long gameId, Long casinoId, Long amount, int number) {
		Long balance = getBalanceAmount(userId);
		CustomJSONObject response = new CustomJSONObject();
		if (balance.compareTo(amount) >= 0) {
			String gameStatus = GameUtil.getStatus(gameId);
			if (gameStatus.equals(Constants.GAME_OPEN)) {
				Long currentTime = System.currentTimeMillis();

				User user = new User();
				user.setId(userId);
				Game game = new Game();
				game.setId(gameId);

				Bet bet = new Bet(null, user, game, currentTime, number, amount, Constants.BET_DEFAULT_STATUS);

				Session session = null;
				Transaction transaction = null;
				try {
					session = ConnectionPool.getSession();
					transaction = session.beginTransaction();
					session.save(bet);
					transaction.commit();
				} catch (Exception ex) {
					ConnectionPool.rollBack(transaction);
					ex.printStackTrace();
				} finally {
					ConnectionPool.closeSession(session);
				}
				updateBalance(userId, amount * -1, false);
				CasinoUtil.updateBalance(casinoId, amount);
				response.put("bet_id", getBetId(userId, gameId, currentTime)).put("balance", getBalanceAmount(userId))
						.message("Bet placed successfully");
			} else {
				response.respond(CustomResponse.GAME_NOT_OPNED);
			}
		} else {
			response.respond(CustomResponse.INSUFFICIENT_BALANCE);
		}
		return response;
	}

	/**
	 * Validates whether the given user id is already available or not
	 * @param userID
	 * @return
	 */
	public static boolean isValidUser(Long userID) {
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			return session.get(User.class, userID) != null;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return false;
	}

	/**
	 * Updates the casino id of the specified user while the user enter the corresponding casino
	 * @param userId
	 * @param casinoId
	 */
	public static void updateCasino(Long userId, Long casinoId) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = ConnectionPool.getSession();
			transaction = session.beginTransaction();
			session.createQuery("update User user set casino_id = :casino_id where user.id = :user_id")
					.setLong("casino_id", casinoId).setLong("user_id", userId).executeUpdate();
			transaction.commit();
		} catch (Exception ex) {
			ConnectionPool.rollBack(transaction);
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
	}

	/**
	 * Returns the current casino id of the specified user 
	 * @param userId
	 * @return
	 */
	public static Long getCurrentCasinoId(Long userId) {
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			User user = (User) session.get(User.class, userId);
			return user.getCasino().getId();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return 0L;
	}
}

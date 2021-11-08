package com.roulette.util;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.roulette.db.ConnectionPool;
import com.roulette.entity.Casino;
import com.roulette.entity.Dealer;
import com.roulette.entity.Game;

/**
 * Util Class used to provide all the helper methods for Casino.class
 * @author srini
 */
public class CasinoUtil {
	
	/**
	 * Stores the casino detail in the DB
	 * @param casino
	 */
	public static void addCasino(Casino casino) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = ConnectionPool.getSession();
			transaction = session.beginTransaction();
			session.save(casino);
			transaction.commit();
		} catch (Exception ex) {
			ConnectionPool.rollBack(transaction);
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
	}
	
	/**
	 * Returns the casino id for the provided corresponding email 
	 * @param email
	 * @return
	 */
	public static Long getCasinoID(String email) {
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			Query q = session.createQuery("from Casino casino where casino.email_id = :email_id");
			q.setString("email_id", email);
			Casino casino = (Casino) q.uniqueResult();
			return casino.getId();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return 0L;
	}

	/**
	 * Returns the casino id of the provided game id
	 * @param gameID
	 * @return
	 */
	public static Long getCasinoID(Long gameID) {
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			Game game = (Game) session.get(Game.class, gameID);
			return game.getDealer().getCasino().getId();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return 0L;
	}

	/**
	 * Validated whether the given casino id is valid or not
	 * @param casino_id
	 * @return
	 */
	public static boolean isValidCasino(Long casino_id) {
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			return session.get(Casino.class, casino_id) != null;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return false;
	}

	/**
	 * Confirms whether the casino table contains the provided email or not
	 * @param email
	 * @return
	 */
	public static boolean hasCasino(String email) {
		return !getCasinoID(email).equals(0L);
	}

	/**
	 * Returns the balance amount available for the provided casino id
	 * @param casinoId
	 * @return
	 */
	public static Long getBalanceAmount(Long casinoId) {
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			Casino casino = (Casino) session.get(Casino.class, casinoId);
			return casino.getAmount();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return 0L;
	}

	/**
	 * Loads/updates the balance amount of the provided casino
	 * @param casinoId
	 * @param amount
	 */
	public static synchronized void updateBalance(Long casinoId, Long amount) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = ConnectionPool.getSession();
			transaction = session.beginTransaction();
			session.createQuery(
					"update Casino casino set casino.amount = casino.amount + :amount where casino.id = :casino_id")
					.setLong("amount", amount).setLong("casino_id", casinoId).executeUpdate();
			transaction.commit();
		} catch (Exception ex) {
			ConnectionPool.rollBack(transaction);
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
	}

	/**
	 * Returns the list of all the dealers available in the corresponding casino
	 * @param casino_id
	 * @return
	 */
	public static List<CustomJSONObject> getAllDealers(Long casino_id) {
		Session session = null;
		List<CustomJSONObject> dealerListResponse = new ArrayList<>();
		try {
			session = ConnectionPool.getSession();
			Query query = session.createQuery("from Dealer dealer where dealer.casino.id = :casino_id")
					.setLong("casino_id", casino_id);
			List<Dealer> dealerList = query.list();
			for (Dealer dealer : dealerList) {
				dealerListResponse.add(new CustomJSONObject(false).put("dealer_id", dealer.getId())
						.put("dealer_name", dealer.getName()).put("dealer_email_id", dealer.getEmail_id()));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return dealerListResponse;
	}
	
	/**
	 * Returns the list of all casinos available in the application/server
	 * @return
	 */
	public static List<CustomJSONObject> getAllCasino() {
		Session session = null;
		List<CustomJSONObject> casinoListResponse = new ArrayList<>();
		try {
			session = ConnectionPool.getSession();
			Query query = session.createQuery("from Casino casino");
			List<Casino> casinoList = query.list();
			for (Casino casino : casinoList) {
				casinoListResponse.add(new CustomJSONObject(false).put("id", casino.getId()).put("name", casino.getName()));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return casinoListResponse;
	}

}

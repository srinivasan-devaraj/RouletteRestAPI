package com.roulette.util;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.roulette.db.ConnectionPool;
import com.roulette.entity.Casino;
import com.roulette.entity.Dealer;

/**
 * Util Class used to provide all the helper methods for Dealer.class
 * @author srini
 */
public class DealerUtil {

	/**
	 * Stores the dealer in the DB
	 * @param name
	 * @param email_id
	 * @param casino_id
	 */
	public static void addDealer(String name, String email_id, Long casino_id) {
		Casino casino = new Casino();
		casino.setId(casino_id);
		Dealer dealer = new Dealer(null, name, email_id, casino, null);
		Session session = null;
		Transaction transaction = null;
		try {
			session = ConnectionPool.getSession();
			transaction = session.beginTransaction();
			session.save(dealer);
			transaction.commit();
		} catch (Exception ex) {
			ConnectionPool.rollBack(transaction);
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
	}
	
	/**
	 * Returns the dealer id of the corresponding associated email Id and casino Id
	 * @param casino_id
	 * @param email_id
	 * @return
	 */
	public static Long getDealerID(Long casino_id, String email_id){
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			Query q = session.createQuery("from Dealer dealer where dealer.email_id = :email_id and casino_id = :casino_id")
					.setString("email_id", email_id).setLong("casino_id", casino_id);
			Dealer dealer = (Dealer) q.uniqueResult();
			return dealer.getId();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return 0L;
	}

	/**
	 * Returns whether the corresponding casino Id and email Id already exists or not
	 * @param casino_id
	 * @param email_id
	 * @return
	 */
	public static boolean hasDealer(Long casino_id, String email_id) {
		return !getDealerID(casino_id, email_id).equals(0L);
	}

	/**
	 * Validates whether the given dealer id is valid or not
	 * @param dealer_id
	 * @return
	 */
	public static boolean isValidDealer(Long dealer_id) {
		Session session = null;
		try {
			session = ConnectionPool.getSession();
			return session.get(Dealer.class, dealer_id) != null;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ConnectionPool.closeSession(session);
		}
		return false;
	}

}

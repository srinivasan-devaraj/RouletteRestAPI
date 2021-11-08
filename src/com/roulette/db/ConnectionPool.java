package com.roulette.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import com.roulette.entity.*;

/**
 * Class used to handle all the process related to DB operations
 * @author srini
 */
public class ConnectionPool {
	
	private static SessionFactory sessionFactory = null;
	
	/**
	 * Method used to load the db configuration data into memory and establish a sessionfactory for hibernate
	 */
	private static void loadSessionFactory() {
		Configuration conf = new Configuration()
				.addAnnotatedClass(Casino.class)
				.addAnnotatedClass(Dealer.class)
				.addAnnotatedClass(User.class)
				.addAnnotatedClass(Game.class)
				.addAnnotatedClass(Bet.class)
				.configure();
		ServiceRegistry serReg = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
		sessionFactory = conf.buildSessionFactory(serReg);
	}
	
	/**
	 * Method used to get the session from the sessionFactory 
	 * @return
	 */
	public static Session getSession() {
		if(sessionFactory == null) {
			loadSessionFactory();
		}
		return sessionFactory.openSession();
	}
	
	/**
	 * Method used to rollback the transaction if there is any issue while continuing the transaction
	 * @param transaction
	 */
	public static void rollBack(Transaction transaction) {
		if(transaction != null) {
			transaction.rollback();
		}
	}
	
	/**
	 * Method used to close the session
	 * @param session
	 */
	public static void closeSession(Session session) {
		if(session != null) {
			session.close();
		}
	}
	
	
}

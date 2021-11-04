package com.roulette.dao;

import java.sql.SQLException;

import com.roulette.db.ConnectionPool;

public class UserDAO {
	
	private Long id;
	private String name;
	private String email_id;
	private Long balance = 0L;
	
	public UserDAO(Long id, String name, Long balance) {
		this.id = id;
		this.name = name;
		this.balance = balance;
	}
	
	public UserDAO(String name, String email_id) {
		this.name = name;
		this.email_id = email_id;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail_id() {
		return email_id;
	}

	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}
	
	public Long getBalance() {
		return balance;
	}
	
	public void setBalance(Long balance) {
		this.balance = balance;
	}
	

	

}

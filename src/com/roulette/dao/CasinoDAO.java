package com.roulette.dao;

public class CasinoDAO {
	
	private Long id;
	private String name;
	private String email_id;
	private Long amount = 0L;
	
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
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	
	public CasinoDAO(String name, String email_id) {
		super();
		this.name = name;
		this.email_id = email_id;
	}
	

}

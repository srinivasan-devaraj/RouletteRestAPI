package com.roulette.dao;

public class DealerDAO {
	
	private Long id;
	private String name;
	private String email_id;
	private Long casino_id;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public DealerDAO(String name, String email_id, Long casino_id) {
		this.name = name;
		this.email_id = email_id;
		this.casino_id = casino_id;
	}
	public DealerDAO(Long id, String name, String email_id, Long casino_id) {
		this.id = id;
		this.name = name;
		this.email_id = email_id;
		this.casino_id = casino_id;
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
	public Long getCasino_id() {
		return casino_id;
	}
	public void setCasino_id(Long casino_id) {
		this.casino_id = casino_id;
	}
	

}

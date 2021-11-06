package com.roulette.dao;

public class GameDAO {
	
	private Long id;
	private Long startTime;
	private Long endTime;
	private Long dealerId;
	private String status;
	private int thrownNumber;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public Long getDealerId() {
		return dealerId;
	}
	public void setDealerId(Long dealerId) {
		this.dealerId = dealerId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getThrownNumber() {
		return thrownNumber;
	}
	public void setThrownNumber(int thrownNumber) {
		this.thrownNumber = thrownNumber;
	}
	
	
	
	
}

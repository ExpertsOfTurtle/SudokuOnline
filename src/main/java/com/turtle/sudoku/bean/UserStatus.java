package com.turtle.sudoku.bean;

public class UserStatus {
	private String username;
	private Integer gameId;
	private String action;
	private Double process;
	private String details;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Double getProcess() {
		return process;
	}
	public void setProcess(Double process) {
		this.process = process;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
}

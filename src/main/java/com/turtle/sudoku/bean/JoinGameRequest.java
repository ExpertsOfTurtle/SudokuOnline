package com.turtle.sudoku.bean;

public class JoinGameRequest {
	private String username;
	private Integer gameId;
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
}

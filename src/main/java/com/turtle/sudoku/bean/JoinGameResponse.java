package com.turtle.sudoku.bean;

import java.util.List;

public class JoinGameResponse extends SocketResponse {
	private String username;
	private Integer gameId;
	private List<UserStatus> userList;
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
	public List<UserStatus> getUserList() {
		return userList;
	}
	public void setUserList(List<UserStatus> userList) {
		this.userList = userList;
	}
}

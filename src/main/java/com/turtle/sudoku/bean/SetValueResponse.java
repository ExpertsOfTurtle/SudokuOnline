package com.turtle.sudoku.bean;

public class SetValueResponse extends SocketResponse {
	private Integer setType;	//0:Set, 1:Clean
	private Integer gameId;
	private Integer x;
	private Integer y;
	private String value;
	private String username;
	public Integer getSetType() {
		return setType;
	}
	public void setSetType(Integer setType) {
		this.setType = setType;
	}
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	public Integer getX() {
		return x;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public Integer getY() {
		return y;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}

package com.turtle.sudoku.bean;

public class StartGameRequest extends SocketRequest{
	private Integer gameId;
	private Long timestamp;
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
}

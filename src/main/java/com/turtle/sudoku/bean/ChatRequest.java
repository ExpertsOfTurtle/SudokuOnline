package com.turtle.sudoku.bean;

public class ChatRequest extends SocketRequest {
	private String message;
	private Integer gameId;
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
}

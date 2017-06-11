package com.turtle.sudoku.bean;

public class StartGameResponse extends SocketResponse {
	private Long timestamp;
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}	
}

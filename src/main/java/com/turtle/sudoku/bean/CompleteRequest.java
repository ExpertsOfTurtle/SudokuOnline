package com.turtle.sudoku.bean;

public class CompleteRequest extends SocketRequest {
	private Integer usetime;
	private Integer gameId;
	private String details;
	private String answer;
	public Integer getUsetime() {
		return usetime;
	}
	public void setUsetime(Integer usetime) {
		this.usetime = usetime;
	}
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
}

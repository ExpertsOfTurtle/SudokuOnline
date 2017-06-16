package com.turtle.sudoku.bean;

public class UpdateStatusRequest extends SocketRequest {
	private Double process;
	private String details;
	private Integer gameId;
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
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
}

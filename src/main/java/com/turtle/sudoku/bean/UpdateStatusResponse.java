package com.turtle.sudoku.bean;

public class UpdateStatusResponse extends SocketResponse {
	private String username;
	private Double process;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Double getProcess() {
		return process;
	}
	public void setProcess(Double process) {
		this.process = process;
	}
}

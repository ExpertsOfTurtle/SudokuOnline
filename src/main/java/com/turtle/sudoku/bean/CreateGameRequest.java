package com.turtle.sudoku.bean;

public class CreateGameRequest {
	private String username;
	private Integer secondToStart;
	private String title;
	private String level;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getSecondToStart() {
		return secondToStart;
	}
	public void setSecondToStart(Integer secondToStart) {
		this.secondToStart = secondToStart;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
}

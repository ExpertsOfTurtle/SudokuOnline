package com.turtle.sudoku.bean;

public class CreateGameRequest {
	private String username;
	private Integer secondToStart;
	private String title;
	private Integer level;
	private String gameMode;
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
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getGameMode() {
		return gameMode;
	}
	public void setGameMode(String gameMode) {
		this.gameMode = gameMode;
	}
}

package com.turtle.sudoku.bean;

public class LockRequest extends SocketRequest {
	private Integer lockType;	//0:Lock, 1:Unlock
	private Integer gameId;
	private Integer x;
	private Integer y;
	public Integer getLockType() {
		return lockType;
	}
	public void setLockType(Integer lockType) {
		this.lockType = lockType;
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
	
}

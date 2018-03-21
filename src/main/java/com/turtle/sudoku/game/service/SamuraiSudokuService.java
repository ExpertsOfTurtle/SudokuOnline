package com.turtle.sudoku.game.service;

public interface SamuraiSudokuService {
	public boolean dispatch(Integer type, String username, Integer gameId, int x, int y, String value);
	boolean lock(String username, Integer gameId, int x, int y);
	boolean unlock(String username, Integer gameId, int x, int y);
	boolean setValue(String username, Integer gameId, int x, int y, String value);
	boolean cleanValue(String username, Integer gameId, int x, int y);
}

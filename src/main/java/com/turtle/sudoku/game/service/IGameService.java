package com.turtle.sudoku.game.service;

import java.util.List;

import com.turtle.sudoku.bean.SudokuBoard;
import com.turtle.sudoku.bean.UserStatus;

public interface IGameService {
	public List<UserStatus> getUsers(Integer gameId);
	public UserStatus getUser(Integer gameId, String username);
	public List<UserStatus> addUser(Integer gameId, String username);
	public String appendUserAction(Integer gameId, String username, String details);
	public void extendGame(Integer gameId);
	
	public SudokuBoard getBoard(Integer gameId);
	public void setBoard(SudokuBoard board, Integer gameId);
}

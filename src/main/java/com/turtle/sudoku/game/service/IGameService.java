package com.turtle.sudoku.game.service;

import java.util.List;

import com.turtle.sudoku.bean.UserStatus;

public interface IGameService {
	public List<UserStatus> getUsers(Integer gameId);
	public UserStatus getUser(Integer gameId, String username);
	public List<UserStatus> addUser(Integer gameId, String username);
}

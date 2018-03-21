package com.turtle.sudoku.game.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turtle.sudoku.bean.SudokuBoard;
import com.turtle.sudoku.game.service.IGameService;
import com.turtle.sudoku.game.service.SamuraiSudokuService;

@Service
public class SamuraiSudokuServiceImpl implements SamuraiSudokuService {
	private static Logger logger = LoggerFactory.getLogger(SamuraiSudokuServiceImpl.class);
	
	@Autowired
	private IGameService gameService;
	
	public static Integer[] arr;
	private static int N = 100;
	static {
		arr = new Integer[N];
		for (int i = 0; i < N; i++) {
			arr[i] = new Integer(0);
		}
	}

	@Override
	public boolean dispatch(Integer type, String username, Integer gameId, int x, int y, String value) {
		synchronized (arr[gameId % N]) {
			switch (type) {
			case 0:
				return lock(username, gameId, x, y);
			case 1:
				return unlock(username, gameId, x, y);
			case 2:
				return setValue(username, gameId, x, y, value);
			case 3:
				return cleanValue(username, gameId, x, y);
			default:
				
				break;
			}
		}
		return false;
	}
	

	@Override
	public boolean lock(String username, Integer gameId, int x, int y) {
		SudokuBoard board = gameService.getBoard(gameId);
		boolean flag = board.lock(x, y, username);
		if (flag) {
			gameService.setBoard(board, gameId);
		} else {
			logger.warn("[{}] lock ({},{}) fail", username, x, y);
		}
		return flag;
	}

	@Override
	public boolean unlock(String username, Integer gameId, int x, int y) {
		SudokuBoard board = gameService.getBoard(gameId);
		boolean flag = board.unlock(x, y, username);
		if (flag) {
			gameService.setBoard(board, gameId);
		} else {
			logger.warn("[{}] unlock ({},{}) fail", username, x, y);
		}
		return flag;
	}

	@Override
	public boolean setValue(String username, Integer gameId, int x, int y, String value) {
		SudokuBoard board = gameService.getBoard(gameId);
		boolean flag = board.setValue(x, y, value, username);
		if (flag) {
			gameService.setBoard(board, gameId);
		}
		return flag;
	}

	@Override
	public boolean cleanValue(String username, Integer gameId, int x, int y) {
		SudokuBoard board = gameService.getBoard(gameId);
		boolean flag = board.setValue(x, y, "", username);
		if (flag) {
			gameService.setBoard(board, gameId);
		}
		return flag;
	}

}

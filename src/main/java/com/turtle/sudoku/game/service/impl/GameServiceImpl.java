package com.turtle.sudoku.game.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.turtle.sudoku.bean.SudokuBoard;
import com.turtle.sudoku.bean.UserStatus;
import com.turtle.sudoku.game.service.IGameService;
import com.turtle.sudoku.game.service.IRedisService;
import com.turtle.sudoku.util.StringUtil;

@Service
public class GameServiceImpl implements IGameService {
	private static Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
		
	@Value("${redis.expireTime}")
	private Integer expireTimeInteger;
	
	public static Integer[] arr;
	private static int N = 100;
	static {
		arr = new Integer[N];
		for (int i = 0; i < N; i++) {
			arr[i] = new Integer(0);
		}
	}
	
	@Autowired
	private IRedisService redisService;
	
	@Override
	public List<UserStatus> getUsers(Integer gameId) {
		synchronized (arr[gameId%N]) {
			String key = buildGameKey(gameId);
			List<UserStatus> list = redisService.getList(key, UserStatus.class);
			return list;
		}
	}

	@Override
	public UserStatus getUser(Integer gameId, String username) {
		synchronized (arr[gameId%N]) {
			List<UserStatus> list = getUsers(gameId);
			if (list == null) {
				return null;
			} else {
				for (UserStatus user : list) {
					if (user.getUsername().equals(username)) {
						return user;
					}
				}
			}
			return null;
		}
	}

	@Override
	public List<UserStatus> addUser(Integer gameId, String username) {
		synchronized (arr[gameId%N]) {
			String key = buildGameKey(gameId);
			UserStatus user = new UserStatus();
			user.setGameId(gameId);
			user.setUsername(username);
			user.setProcess(0.0);
			user.setAction("Join");
			List<UserStatus> list = getUsers(gameId);
			if (list == null) {
				list = new ArrayList<>();
				list.add(user);
				redisService.setList(key, list);
				redisService.expire(key, expireTimeInteger);
			} else {
				for (UserStatus us : list) {
					if (us.getUsername().equals(username)) {
						redisService.expire(key, expireTimeInteger);
						return list;
					}
				}
				list.add(user);
				redisService.setList(key, list);
				redisService.expire(key, expireTimeInteger);
			}
			return list;
		}
	}

	@Override
	public void extendGame(Integer gameId) {
		String key = buildGameKey(gameId);
		redisService.expire(key, expireTimeInteger);
	}
	@Override
	public String appendUserAction(Integer gameId, String username, String details) {
		String key = buildUserActionKey(gameId, username);
	/*	String str = redisService.get(key);
		if (StringUtil.isEmpty(str)) {
			str = "";
		}
		str += details;*/
		String str = details;
		redisService.set(key, str);
		redisService.expire(key, expireTimeInteger);
		return str;
	}

	private String buildGameKey(Integer gameId) {
		String key = String.format("Game_%d", gameId);
		return key;
	}
	private String buildUserActionKey(Integer gameId, String username) {
		String key = String.format("Game_%d_User_%s", gameId, username);
		return key;
	}

	@Override
	public SudokuBoard getBoard(Integer gameId) {
		String key = String.format("GameBoard_%d", gameId);
		String value = redisService.get(key);
		SudokuBoard sb = JSON.parseObject(value, SudokuBoard.class);
		return sb;
	}

	@Override
	public void setBoard(SudokuBoard board, Integer gameId) {
//		logger.debug("gameId={}, setBoard", gameId);
		String key = String.format("GameBoard_%d", gameId);
		String value = JSON.toJSONString(board);
		redisService.set(key, value);
		redisService.expire(key, expireTimeInteger);
	}

}

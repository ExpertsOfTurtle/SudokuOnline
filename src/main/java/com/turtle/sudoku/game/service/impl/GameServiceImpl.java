package com.turtle.sudoku.game.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turtle.sudoku.bean.UserStatus;
import com.turtle.sudoku.game.service.IGameService;
import com.turtle.sudoku.game.service.IRedisService;

@Service
public class GameServiceImpl implements IGameService {
	private static final long expireTime = 60;
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
			String key = String.format("Game_%d", gameId);
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
			String key = String.format("Game_%d", gameId);
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
				redisService.expire(key, expireTime);
			} else {
				for (UserStatus us : list) {
					if (us.getUsername().equals(username)) {
						redisService.expire(key, expireTime);
						return list;
					}
				}
				list.add(user);
				redisService.setList(key, list);
				redisService.expire(key, expireTime);
			}
			return list;
		}
	}

}
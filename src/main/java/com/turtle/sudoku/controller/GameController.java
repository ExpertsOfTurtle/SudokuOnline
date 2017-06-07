package com.turtle.sudoku.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turtle.sudoku.bean.CreateGameRequest;
import com.turtle.sudoku.bean.ErrorResponse;
import com.turtle.sudoku.exception.ValidationException;
import com.turtle.sudoku.model.GamesModel;
import com.turtle.sudoku.model.SudokuModel;
import com.turtle.sudoku.service.GamesService;
import com.turtle.sudoku.service.SudokuService;
import com.turtle.sudoku.util.DateUtil;

@RestController
@EnableAutoConfiguration
@RequestMapping(value="/game")
public class GameController {
	private static Logger logger = LoggerFactory.getLogger(GameController.class);
	
	@Autowired
	private SudokuService sudokuService = null;
	
	@Autowired
	private GamesService gameService = null;
	
	@RequestMapping(value="/create/{username}/username")
	public ResponseEntity<?> queryByProductIdList(@PathVariable("username") String username) {
		logger.debug("username={}", username);
		
		SudokuModel sudokuModel = sudokuService.findByPrimaryKey(1);
		
		return ResponseEntity.ok(sudokuModel);
	}
	
	/*
	 * 创建游戏
	 * */
	@RequestMapping(value="/create")
	public ResponseEntity<?> createGame(@RequestBody CreateGameRequest request) {
		logger.debug("[{}] create a game of leve [{}]", request.getUsername(), request.getLevel());
		
		try {
			validateUsername(request.getUsername());
			validateLevel(request.getLevel());
			validateSecondToStart(request.getSecondToStart());
		} catch (ValidationException e) {
			ErrorResponse er = new ErrorResponse();
			er.setErrorCode(e.getCode());
			er.setMessage(e.getMessage());
			return ResponseEntity.ok(er);
		}
		long createTime = System.currentTimeMillis();
		
		GamesModel gm = new GamesModel();
		gm.setCreator(request.getUsername());
		gm.setLevel(request.getLevel());
		gm.setTitle(request.getTitle());		
		gm.setCreateTime(createTime);
		gm.setStartTime(createTime + request.getSecondToStart());
		gm.setDatetime(DateUtil.format(createTime, "yyyy-MM-dd HH:mm:ss"));
		gm.setStatus("PENDING");
		int rt = gameService.create(gm);
		gm.setId(rt);
		
		return ResponseEntity.ok(gm);
	}
	
	/*
	 * 查询所有已经创建，还没开始的游戏
	 * */
	@RequestMapping(value="/queryAll")
	public ResponseEntity<?> queryAllGames() {
		long time = System.currentTimeMillis();
		List<GamesModel> list = gameService.selectPendingGames(time);
		return ResponseEntity.ok(list);
	}
	
	@RequestMapping(value="/getProblem")
	public ResponseEntity<?> getProblem() {
		long time = System.currentTimeMillis();
		List<GamesModel> list = gameService.selectPendingGames(time);
		return ResponseEntity.ok(list);
	}
	
	private boolean validateUsername(String username) throws ValidationException {
		if ("could".equalsIgnoreCase(username) || "dfs".equalsIgnoreCase(username)) {
			return true;
		} else {
			throw new ValidationException("", "Username error");
		}
	}
	private boolean validateLevel(Integer level) throws ValidationException {
		if (level == null) {
			throw new ValidationException("", "Level is null");
		}
		if (level < 11 || level > 19) {
			throw new ValidationException("", "No such level");
		}
		return true;
	}
	private boolean validateSecondToStart(Integer second) throws ValidationException {
		if (second == null) {
			throw new ValidationException("", "secondToStart is null");
		}
		if (second < 0 || second % 10 != 0) {
			throw new ValidationException("", "secondToStart is not valid");
		}
		return true;
	}
	
}

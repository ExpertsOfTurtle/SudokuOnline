package com.turtle.sudoku.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turtle.sudoku.bean.CreateGameRequest;
import com.turtle.sudoku.bean.ErrorResponse;
import com.turtle.sudoku.enums.GameMode;
import com.turtle.sudoku.exception.SudokuException;
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
	private SimpMessagingTemplate messagingTemplate;
	
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
		
		int problemId = 0;
		try {
			validateUsername(request.getUsername());
			validateLevel(request.getLevel());
			validateSecondToStart(request.getSecondToStart());
			validateGameMode(request.getGameMode());
			problemId = getRandomProblem(request.getLevel());
		} catch (ValidationException e) {
			ErrorResponse er = new ErrorResponse();
			er.setErrorCode(e.getCode());
			er.setMessage(e.getMsg());
			return ResponseEntity.ok(er);
		} catch (SudokuException e) {
			ErrorResponse er = new ErrorResponse();
			er.setErrorCode(e.getCode());
			er.setMessage(e.getMsg());
			return ResponseEntity.ok(er);
		}
		long createTime = System.currentTimeMillis();
		
		GamesModel gm = new GamesModel();
		gm.setCreator(request.getUsername());
		gm.setLevel(request.getLevel());
		gm.setTitle(request.getTitle());		
		gm.setCreateTime(createTime);
		gm.setGameMode(request.getGameMode());
		gm.setStartTime(createTime + request.getSecondToStart() * 1000);
		gm.setDatetime(DateUtil.format(createTime, "yyyy-MM-dd HH:mm:ss"));
		gm.setStatus("W");
		gm.setProblemid(problemId);
		int rt = gameService.create(gm);
//		gm.setId(rt);
		System.out.println("id=" + rt);
		
		//广播：创建游戏
		messagingTemplate.convertAndSend("/topic/create", gm);
		
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
	
	/*
	 * 根据题目Id获取题目数据
	 * */
	@RequestMapping(value="/getProblem/{problemId}")
	public ResponseEntity<?> getProblem(@PathVariable("problemId")Integer problemId) {
		SudokuModel sudoku = sudokuService.findByPrimaryKey(problemId);
		return ResponseEntity.ok(sudoku);
	}
	
	/*
	 * 根据gameId，先查找题目Id，然后获取题目数据
	 * */
	@RequestMapping(value="/getProblem/gameId/{gameId}")
	public ResponseEntity<?> getProblemByGameId(@PathVariable("gameId")Integer gameId) {
		GamesModel game = gameService.findByPrimaryKey(gameId);
		if (game == null || game.getId() == null) {
			ErrorResponse er = new ErrorResponse();
			er.setErrorCode("");
			er.setMessage("No such game");
			return ResponseEntity.ok(er);	
		}
		Integer problemId = game.getId();
		SudokuModel sudoku = sudokuService.findByPrimaryKey(problemId);
		if (sudoku == null) {
			ErrorResponse er = new ErrorResponse();
			er.setErrorCode("");
			er.setMessage("No such problem");
			return ResponseEntity.ok(er);
		}
		return ResponseEntity.ok(sudoku);
	}
	
	/*
	 * 根据Level从数据库查找所有题目，然后随机抽
	 * */
	private Integer getRandomProblem(Integer level) throws SudokuException {
		List<SudokuModel> list = sudokuService.selectByLevel(level);
		if (list == null || list.size() == 0) {
			throw new SudokuException("", "No problem of such level");
		}
		int n = list.size();
		int x = (int)(Math.random() * n * 2) % n;
		SudokuModel sudoku = list.get(x);
		return sudoku.getId();
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
		if (level <= 0 || level >= 20) {
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
	private boolean validateGameMode(String gameMode) throws ValidationException {
		for (GameMode gm : GameMode.values()) {
			if (gm.getValue().equals(gameMode)) {
				return true;
			}
		}
		throw new ValidationException("", "gameMode is not valid");
	}
}

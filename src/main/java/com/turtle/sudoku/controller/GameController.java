package com.turtle.sudoku.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.turtle.sudoku.bean.CompleteRequest;
import com.turtle.sudoku.bean.CompleteResponse;
import com.turtle.sudoku.bean.CreateGameRequest;
import com.turtle.sudoku.bean.ErrorResponse;
import com.turtle.sudoku.bean.SudokuActivity;
import com.turtle.sudoku.bean.SudokuBoard;
import com.turtle.sudoku.bean.VerifyResponse;
import com.turtle.sudoku.enums.GameMode;
import com.turtle.sudoku.enums.SudokuLevel;
import com.turtle.sudoku.exception.SudokuException;
import com.turtle.sudoku.exception.ValidationException;
import com.turtle.sudoku.game.service.IGameService;
import com.turtle.sudoku.game.service.SamuraiSudokuService;
import com.turtle.sudoku.model.ActivityModel;
import com.turtle.sudoku.model.GamesModel;
import com.turtle.sudoku.model.SudokuModel;
import com.turtle.sudoku.model.SudokuResultModel;
import com.turtle.sudoku.service.GamesService;
import com.turtle.sudoku.service.HttpService;
import com.turtle.sudoku.service.SudokuResultService;
import com.turtle.sudoku.service.SudokuService;
import com.turtle.sudoku.util.DateUtil;

@Controller
@EnableAutoConfiguration
@RequestMapping(value="/game")
public class GameController {
	private static Logger logger = LoggerFactory.getLogger(GameController.class);
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private SudokuService sudokuService;
	@Autowired
	private SamuraiSudokuService samuraiSudokuService;
	@Autowired
	private GamesService gameService;
	@Autowired
	private SudokuResultService sudokuResultService;
	@Autowired
	private IGameService redisGameService = null;
	
	@Autowired
	private HttpService httpService;
	
	@Value("${url.turtlebone-core}")
	private String TURTLEBONE_CORE_URL;
	@Autowired
	private Environment env;
	
	
	@RequestMapping(value="/create/{username}/username")
	public @ResponseBody ResponseEntity<?> queryByProductIdList(@PathVariable("username") String username) {
		logger.debug("username={}", username);
		
		SudokuModel sudokuModel = sudokuService.findByPrimaryKey(1);
		
		return ResponseEntity.ok(sudokuModel);
	}
	
	/*
	 * 创建游戏
	 * */
	@RequestMapping(value="/create")
	public @ResponseBody ResponseEntity<?> createGame(@RequestBody CreateGameRequest request) {
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
		gm.setId(rt);
		System.out.println("id=" + rt);
		
		if (gm.getLevel() == 20) {
			//针对武士数独（五连体数独）
			SudokuBoard board = new SudokuBoard(1);
			SudokuModel sudoku = sudokuService.selectByGameId(gm.getId());
			String problem = sudoku.getProblem();
			if (board.updateBoard(problem)) {
				redisGameService.setBoard(board, gm.getId());
			} else {
				logger.error("Update sudoku board FAIL");
			}
		}
		
		//广播：创建游戏
		messagingTemplate.convertAndSend("/topic/create", gm);
		
		return ResponseEntity.ok(gm);
	}
	@RequestMapping(value="/queryBoard/{gameId}")
	public @ResponseBody ResponseEntity<?> queryBoard(@PathVariable Integer gameId) {
		SudokuBoard board = redisGameService.getBoard(gameId);
		return ResponseEntity.ok(board);
	}
	
	@RequestMapping(value="/queryGame")
	public String queryGames(ModelMap modelMap) {
		long time = System.currentTimeMillis();
//		List<GamesModel> list = gameService.selectPendingGames(time);
		List<GamesModel> list = gameService.selectRecentGames();
		modelMap.put("gameList", list);
		return "gameslist";
	}
	
	@RequestMapping(value="/verifyResult")
	public @ResponseBody ResponseEntity<?> verifyResult(@RequestBody CompleteRequest request) {
		Integer gameId = request.getGameId();
		String username = request.getUsername();
		SudokuBoard board = redisGameService.getBoard(gameId);
		boolean rs = board.verify();
		
		VerifyResponse response = new VerifyResponse();
		response.setMessageType("Verify");
		if (rs) {
			response.setResult("YES");
			long s = board.getStartMillis();
			long e = System.currentTimeMillis();
			long diff = (e - s) / 1000;
			long hour = diff / 3600;
			diff %= 3600;
			long minute = diff / 60;
			long second = diff % 60;
			response.setHour(hour);
			response.setMinute(minute);
			response.setSecond(second);
			response.setStartTime(board.getStartDateTime());
			response.setEndTime(DateUtil.format(e, "yyyy-MM-dd HH:mm:ss"));
			response.setUsername(username);
			
			board.setEndMillis(e);
			board.setEndDateTime(response.getEndTime());
			redisGameService.setBoard(board, gameId);
		} else {
			response.setUsername(username);
			response.setResult("NO");
		}
		
		String topic = String.format("/topic/game/%d", gameId);
		messagingTemplate.convertAndSend(topic, JSON.toJSONString(response));
		return ResponseEntity.ok(response);
	}
	
	/*
	 * 查询所有已经创建，还没开始的游戏
	 * */
	@RequestMapping(value="/queryAll")
	public @ResponseBody ResponseEntity<?> queryAll() {
		long time = System.currentTimeMillis();
//		List<GamesModel> list = gameService.selectPendingGames(time);
		List<GamesModel> list = gameService.selectRecentGames();
		return ResponseEntity.ok(list);
	}
	
	/*
	 * 根据题目Id获取题目数据
	 * */
	@RequestMapping(value="/getProblem/{gameid}")
	public @ResponseBody ResponseEntity<?> getProblem(@PathVariable("gameid")Integer gameId) {
		SudokuModel sudoku = sudokuService.selectByGameId(gameId);
		return ResponseEntity.ok(sudoku);
	}
	
	/*
	 * 根据gameId，先查找题目Id，然后获取题目数据
	 * */
	@RequestMapping(value="/getProblem/gameId/{gameId}")
	public @ResponseBody ResponseEntity<?> getProblemByGameId(@PathVariable("gameId")Integer gameId) {
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
	 * 完成
	 * */
	@RequestMapping(value="/complete")
	public @ResponseBody ResponseEntity<?> complete(CompleteRequest request) {
		logger.debug("{} complete the game, answer is:{}", request.getUsername(), request.getAnswer());
		
		long curTime = System.currentTimeMillis();
		
		GamesModel game = gameService.findByPrimaryKey(request.getGameId());
		GamesModel gm = new GamesModel();
		gm.setId(game.getId());
		gm.setEndTime(curTime);
		gm.setStatus("E");
		gameService.updateByPrimaryKeySelective(gm);
		
		
		Map<String, Object> map = new HashMap<>();
		map.put("gameid", request.getGameId());
		map.put("username", request.getUsername());
		SudokuResultModel srm = sudokuResultService.selectByGame(map);
		if (srm != null) {
			return ResponseEntity.ok(srm);
		}
		
		if (game.getLevel() == 20) {
			//五连体数独
			SudokuResultModel rs = completeSamuraiSudoku(game.getId());
			sendSudokuAcitivty(rs, game);
			return ResponseEntity.ok(rs);
		}
		
		srm = new SudokuResultModel();
		srm.setGameid(request.getGameId());
		srm.setLevel(game.getLevel());
		srm.setUsername(request.getUsername());
		srm.setDetails(request.getDetails());
		long useTime = curTime - game.getStartTime();
		srm.setUsetime((int)useTime);
		srm.setGameMode(game.getGameMode());
		srm.setDatetime(DateUtil.getDateTime());
		srm.setTimestamp(curTime);
		
		int rank = 1 + sudokuResultService.getRank(srm.getGameid(), srm.getTimestamp());
		srm.setRank(rank);
		
		sudokuResultService.create(srm);
		
		SudokuModel sudoku = sudokuService.findByPrimaryKey(game.getProblemid());
		if (sudoku.getLevel() != 0 && 
				(sudoku.getBestresult() == null || sudoku.getBestresult() > request.getUsetime())) {
			sudoku.setLastupdatetime(System.currentTimeMillis());
			sudoku.setBestresult(request.getUsetime());
			sudokuService.updateTimeAndResult(sudoku);
		}
		
		sendSudokuAcitivty(srm, game);
		
		return ResponseEntity.ok(srm);
	}
	
	@RequestMapping(value="/querySudokuResult/{id}")
	public @ResponseBody ResponseEntity<?> querySudokuResult(@PathVariable("id") Integer id) {
		logger.debug("id={}", id);
		
		SudokuResultModel sudokuResultModel = sudokuResultService.findByPrimaryKey(id);
		
		return ResponseEntity.ok(sudokuResultModel);
	}
	
	
	/*
	 * 根据Level从数据库查找所有题目，然后随机抽
	 * */
	private @ResponseBody Integer getRandomProblem(Integer level) throws SudokuException {
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
//			throw new ValidationException("", "Username error");
			return true;
		}
	}
	private SudokuLevel getLevel(Integer level) {
		SudokuLevel rs = SudokuLevel.TEST;
		if (level != null) {
			for (SudokuLevel sl : SudokuLevel.values()) {
				if (level.intValue() == sl.getValue().intValue()) {
					return sl;
				}
			}	
		}		
		return rs;
	}
	private boolean validateLevel(Integer level) throws ValidationException {
		if (level == null) {
			throw new ValidationException("", "Level is null");
		}
		if (level < 0 || level > 20) {
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
	private void sendSudokuAcitivty(SudokuResultModel srm, GamesModel gm) {
		SudokuActivity sudokuActivity = new SudokuActivity();
		sudokuActivity.setDatetime(srm.getDatetime());
		sudokuActivity.setGameId(srm.getGameid());
		sudokuActivity.setLevel(getLevel(srm.getLevel()));
		sudokuActivity.setProblemId(gm.getProblemid());
		sudokuActivity.setRank(srm.getRank());
		sudokuActivity.setUsername(srm.getUsername());
		sudokuActivity.setUsetime(srm.getUsetime());
		String body = JSON.toJSONString(sudokuActivity);
		
		Map<String, String> header = new HashMap<>();
		header.put("Content-Type", "application/json");

		String url = TURTLEBONE_CORE_URL + "/activity/sudoku";
		try {
			ActivityModel activity = httpService.sendAndGetResponse(url, header, body,
					ActivityModel.class);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private String formatUsetime(Integer usetime) {
		int minute = usetime / 60;
		int second = usetime % 60;
		return String.format("%d'%d''", minute, second);
	}
	private SudokuResultModel completeSamuraiSudoku(Integer gameId) {
		SudokuBoard board = redisGameService.getBoard(gameId);
		StringBuffer users = new StringBuffer();
		for (Entry<String, Boolean> entry : board.getUserMap().entrySet()) {
			users.append(",").append(entry.getKey());
			
			SudokuResultModel srm = new SudokuResultModel();
			srm.setGameid(gameId);
			srm.setLevel(20);
			srm.setUsername(entry.getKey());
			srm.setDetails("");
			long useTime = board.getEndMillis() - board.getStartMillis();
			srm.setUsetime((int)useTime);
			srm.setGameMode(GameMode.Cooperation.getValue());
			srm.setDatetime(board.getEndDateTime());
			srm.setTimestamp(board.getEndMillis());
			srm.setRank(1);
			sudokuResultService.create(srm);
		}
		String username = users.substring(1);
		SudokuResultModel srm = new SudokuResultModel();
		srm.setGameid(gameId);
		srm.setLevel(20);
		srm.setUsername(username);
		srm.setDetails("");
		long useTime = board.getEndMillis() - board.getStartMillis();
		srm.setUsetime((int)useTime);
		srm.setGameMode(GameMode.Cooperation.getValue());
		srm.setDatetime(board.getEndDateTime());
		srm.setTimestamp(board.getEndMillis());
		srm.setRank(1);
		sudokuResultService.create(srm);
		return srm;
	}
}

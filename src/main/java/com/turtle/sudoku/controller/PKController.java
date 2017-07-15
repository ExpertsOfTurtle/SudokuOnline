package com.turtle.sudoku.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.turtle.sudoku.bean.ChatRequest;
import com.turtle.sudoku.bean.ChatResponse;
import com.turtle.sudoku.bean.CompleteRequest;
import com.turtle.sudoku.bean.CompleteResponse;
import com.turtle.sudoku.bean.JoinGameRequest;
import com.turtle.sudoku.bean.JoinGameResponse;
import com.turtle.sudoku.bean.RequestMessage;
import com.turtle.sudoku.bean.ResponseMessage;
import com.turtle.sudoku.bean.SocketRequest;
import com.turtle.sudoku.bean.StartGameRequest;
import com.turtle.sudoku.bean.StartGameResponse;
import com.turtle.sudoku.bean.UpdateStatusRequest;
import com.turtle.sudoku.bean.UpdateStatusResponse;
import com.turtle.sudoku.bean.UserStatus;
import com.turtle.sudoku.enums.MessageType;
import com.turtle.sudoku.game.service.IGameService;
import com.turtle.sudoku.model.GamesModel;
import com.turtle.sudoku.model.SudokuResultModel;
import com.turtle.sudoku.service.GamesService;
import com.turtle.sudoku.service.SudokuResultService;
import com.turtle.sudoku.service.SudokuService;
import com.turtle.sudoku.util.StringUtil;

import net.sf.json.JSONObject;

@Controller
public class PKController extends WsController {
	private static Logger logger = LoggerFactory.getLogger(PKController.class);
			
	@Autowired
	private GamesService gameService = null;
	
	@Autowired
	private SudokuResultService sudokuResultService = null;
	
	@Autowired
	private IGameService redisGameService = null;
	
	@MessageMapping("/updateStatus")
	public void updateStatus(UpdateStatusRequest request) {
		UpdateStatusResponse response = new UpdateStatusResponse();
		response.setMessageType(MessageType.Update.getValue());
		response.setProcess(request.getProcess());
		response.setUsername(request.getUsername());
		
		if (!StringUtil.isEmpty(request.getDetails())) {
			redisGameService.appendUserAction(request.getGameId(), request.getUsername(), request.getDetails());
		}
		
		String topic = String.format("/topic/game/%d", request.getGameId());
		doResponse(topic, response);
	}
	
	@MessageMapping("/join")
	public void joinGame(JoinGameRequest request) {
		JoinGameResponse response = new JoinGameResponse();
		response.setGameId(request.getGameId());
		response.setUsername(request.getUsername());
		response.setMessageType(MessageType.Join.getValue());
		
		List<UserStatus> userList = redisGameService.addUser(request.getGameId(), request.getUsername());
		response.setUserList(userList);
		
		String topic = String.format("/topic/game/%d", request.getGameId());
		doResponse(topic, response);
	}
	
	@MessageMapping("/welcome")
	// @SendTo("/topic/getResponse")
	public void say(RequestMessage message) {
		String topic = String.format("/topic/wf/%d", ((int) (Math.random() * 100)) % 3);
		ResponseMessage rsp = new ResponseMessage("welcome," + message.getName() + " !");
		JSONObject json = JSONObject.fromObject(rsp);
		System.out.println("Topic:" + topic + " Response:" + json.toString());
		
		doResponse(topic, rsp);
	}

	@MessageMapping("/chat")
	public void chat(ChatRequest request) {
		logger.debug("I'm chating, from {}, msg={}, gameId={}", 
				request.getUsername(), request.getMessage(), request.getGameId());
		System.out.println(request.getRequestType());
		ChatResponse response = new ChatResponse();
		response.setMessage(request.getMessage());
		response.setUsername(request.getUsername());
		response.setMessageType(request.getRequestType());
		String topic = String.format("/topic/game/%d", request.getGameId());
		doResponse(topic, response);
	}
	
	@MessageMapping("/start")
	public void start(StartGameRequest request) {
		logger.debug("{} start the game [{}]", request.getUsername(), request.getGameId());
		StartGameResponse response = new StartGameResponse();
		response.setMessageType(request.getRequestType());
		response.setTimestamp(request.getTimestamp());
		
		GamesModel game = gameService.findByPrimaryKey(request.getGameId());
		
		//避免重复开始
		if (game == null || "S".equals(game.getStatus())) {
			logger.warn("Game not exist or game has begun");
			return;
		}
		
		GamesModel g = new GamesModel();
		g.setId(game.getId());
		g.setStatus("S");
		g.setStartTime(System.currentTimeMillis() + 5 * 1000);//5秒后开始
		gameService.updateByPrimaryKeySelective(g);
		
		String topic = String.format("/topic/game/%d", request.getGameId());
		doResponse(topic, response);
	}

	@MessageMapping("/complete")
	public void complete(CompleteRequest request) {
		logger.debug("{} complete the game [{}]", request.getUsername(), request.getGameId());
		
		Map<String, Object> map = new HashMap<>();
		map.put("gameid", request.getGameId());
		map.put("username", request.getUsername());
		SudokuResultModel srm = sudokuResultService.selectByGame(map);
		
		CompleteResponse response = new CompleteResponse();
		response.setMessageType(request.getRequestType());
		response.setUsername(request.getUsername());
		if (srm != null) {
			int rank = srm.getRank();
			response.setRank(rank);
			response.setUseTime(srm.getUsetime());
			String result = String.format("(第%d名) 用时:%s", rank, formatUsetime(srm.getUsetime()/1000));
			response.setResult(result);
		} else {
			response.setResult("此人怀疑作弊");
		}
		
		String topic = String.format("/topic/game/%d", request.getGameId());
		doResponse(topic, response);
	}

	@RequestMapping("/wf")
	public String hello4() {

		return "sudoku/index";
	}
	
	private String formatUsetime(Integer usetime) {
		int minute = usetime / 60;
		int second = usetime % 60;
		return String.format("%d'%d''", minute, second);
	}
}

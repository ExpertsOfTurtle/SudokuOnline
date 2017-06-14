package com.turtle.sudoku.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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

import com.alibaba.fastjson.JSONObject;
import com.turtle.sudoku.bean.ChatRequest;
import com.turtle.sudoku.bean.CompleteRequest;
import com.turtle.sudoku.bean.CreateGameRequest;
import com.turtle.sudoku.bean.ErrorResponse;
import com.turtle.sudoku.enums.GameMode;
import com.turtle.sudoku.exception.SudokuException;
import com.turtle.sudoku.exception.ValidationException;
import com.turtle.sudoku.game.service.IRedisService;
import com.turtle.sudoku.model.GamesModel;
import com.turtle.sudoku.model.SudokuModel;
import com.turtle.sudoku.model.SudokuResultModel;
import com.turtle.sudoku.service.GamesService;
import com.turtle.sudoku.service.SudokuResultService;
import com.turtle.sudoku.service.SudokuService;
import com.turtle.sudoku.util.DateUtil;

@Controller
@EnableAutoConfiguration
@RequestMapping(value="/testredis")
public class TestRedisController {
	private static Logger logger = LoggerFactory.getLogger(TestRedisController.class);
	
	@Autowired
	private IRedisService redisService;
	
	@RequestMapping(value="/set/{key}")
	public @ResponseBody ResponseEntity<?> set(@PathVariable("key") String key) {
		logger.debug("set {}", key);
		
		List<ChatRequest> list = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ChatRequest obj = new ChatRequest();
			obj.setGameId((int)(Math.random()*100));
			obj.setUsername(String.format("user%d", i+1));
			list.add(obj);
		}
		
		redisService.setList(key, list);
		redisService.expire(key, 20);
		return ResponseEntity.ok("OK");
	}
	
	@RequestMapping(value="/get/{key}")
	public @ResponseBody ResponseEntity<?> get(@PathVariable("key") String key) {
		List<ChatRequest> list = redisService.getList(key, ChatRequest.class);
		
		if (list != null) {
			logger.debug("size={}", list.size());
		}
		
		return ResponseEntity.ok(list);
	}
}

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

import com.turtle.sudoku.model.SudokuModel;
import com.turtle.sudoku.service.SudokuService;

@RestController
@EnableAutoConfiguration
@RequestMapping(value="/game")
public class GameController {
	private static Logger logger = LoggerFactory.getLogger(GameController.class);
	
	@Autowired
	private SudokuService sudokuService = null;
	
	@RequestMapping(value="/create/{username}/username")
	public ResponseEntity<?> queryByProductIdList(@PathVariable("username") String username) {
		logger.debug("username={}", username);
		
		SudokuModel sudokuModel = sudokuService.findByPrimaryKey(1);
		
		return ResponseEntity.ok(sudokuModel);
	}
}

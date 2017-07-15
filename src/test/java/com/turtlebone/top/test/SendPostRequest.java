package com.turtlebone.top.test;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.turtle.sudoku.bean.SudokuActivity;
import com.turtle.sudoku.enums.SudokuLevel;
import com.turtle.sudoku.model.ActivityModel;
import com.turtle.sudoku.service.HttpService;
import com.turtle.sudoku.service.impl.HttpServiceImpl;

public class SendPostRequest {
	public static void main(String[] args) {
		HttpService service = new HttpServiceImpl();

		SudokuActivity sudokuActivity = new SudokuActivity();
		sudokuActivity.setDatetime(null);
		sudokuActivity.setGameId(0);
		sudokuActivity.setLevel(SudokuLevel.TEST);
		sudokuActivity.setProblemId(804);
		sudokuActivity.setRank(0);
		sudokuActivity.setUsername("SYS");
		sudokuActivity.setUsetime(11);
		String body = JSON.toJSONString(sudokuActivity);

		Map<String, String> header = new HashMap<>();
		header.put("Content-Type", "application/json");

		String url = "http://127.0.0.1:12003/core/activity/sudoku";
		ActivityModel activity = service.sendAndGetResponse(url, header, body,
				ActivityModel.class);
		String rs = JSON.toJSONString(activity);
		System.out.println(rs);
	}
}

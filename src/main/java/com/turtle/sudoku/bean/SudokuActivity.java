package com.turtle.sudoku.bean;


import com.turtle.sudoku.enums.SudokuLevel;

import lombok.Data;

@Data
public class SudokuActivity {
	private String username;
	private String datetime;
	private Integer usetime;
	private SudokuLevel level;
	private Integer gameId;
	private Integer rank;
	private Integer problemId;
}

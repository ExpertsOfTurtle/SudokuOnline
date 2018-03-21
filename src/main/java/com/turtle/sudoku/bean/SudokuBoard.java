package com.turtle.sudoku.bean;

import com.turtle.sudoku.util.StringUtil;

public class SudokuBoard {
	private static class SudokuCell {
		public String owner;
		public String value;
		/*
		 * 0 - 无效（例如连体数独中，棋盘外的单元格
		 * 1 - 空白，未填
		 * 2 - 题目固定
		 * 3 - 已经填写
		 * */
		public int status;
	}
	public static int startPoint[][] = {{6,6},{0,0},{0,12},{12,0},{12,12}};
	private SudokuCell[][] board;
	
	/*
	 * 0 - 普通九宫格数独
	 * 1 - 武士数独（五连体数独）
	 * */
	private int sudokuType;
	public SudokuBoard() {
		
	}
	public SudokuBoard(int type) {
		sudokuType = type;
		if (sudokuType == 0) {
			buildNormal();
		} else if (sudokuType == 1) {
			buildSamurai();
		}
	}
	public boolean updateBoard(String problem) {
		if (StringUtil.isEmpty(problem)) {
			return false;
		}
		String blocks[] = problem.split("\\|");
		if (blocks.length != 5) {
			return false;
		}
		for (int k = 0; k < blocks.length; k++) {
			String block = blocks[k].trim();
			if (block.length() != 81) {
				return false;
			}
			int x = startPoint[k][0];
			int y = startPoint[k][1];
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					char ch = block.charAt(i*9 +j);
					if (ch != '0') {
						board[x+i][y+j].status = 2;
						board[x+i][y+j].value = String.valueOf(ch);
					}
				}
			}
		}
		return true;
	}
	public boolean lock(int x, int y, String username) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j <board[i].length; j++) {
				if (board[i][j].owner.equals(username)) {
					return false;
				}
			}
		}
		if (StringUtil.isEmpty(board[x][y].owner)) {
			board[x][y].owner = username;
			return true;
		}
		return false;
	}
	public boolean unlock(int x, int y, String username) {
		if (username.equals(board[x][y].owner)) {
			board[x][y].owner = "";
			return true;
		}
		return false;
	}
	public boolean setValue(int x, int y, String value, String username){
		if (board[x][y].status == 0 ||board[x][y].status == 2) {
			//该单元格是无效，或者是题目固定，不允许修改
			return false;
		}
		if (!board[x][y].owner.equalsIgnoreCase(username)) {
			// 锁定该单元格的人才能修改值
			return false;
		}
		board[x][y].value = value;
		if ("".equals(value)) {
			board[x][y].status = 1;
		} else {
			board[x][y].status = 3;
		}
		return true;
	}
	private void buildNormal() {
		board = new SudokuCell[9][9];
		for (int i = 0; i < 9; i++) {
			board[i] = new SudokuCell[9];
		}
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				board[i][j].owner = "";
				board[i][j].value = "";
				board[i][j].status = 1;	//空白
			}
		}
	}
	private void buildSamurai() {
		board = new SudokuCell[21][21];
		for (int i = 0; i < 21; i++) {
			for (int j = 0; j < 21; j++) {
				board[i][j] = new SudokuCell();
				board[i][j].owner = "";
				board[i][j].value = "";
				board[i][j].status = 0;	//无效
			}
		}
		for (int k = 0; k < 5; k++) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					int x = startPoint[k][0];
					int y = startPoint[k][1];
					board[i+x][j+y].status = 1;	//空白
				}
			}
		}
	}
	public SudokuCell[][] getBoard() {
		return board;
	}
	public void setBoard(SudokuCell[][] board) {
		this.board = board;
	}
	public int getSudokuType() {
		return sudokuType;
	}
	public void setSudokuType(int sudokuType) {
		this.sudokuType = sudokuType;
	}
}

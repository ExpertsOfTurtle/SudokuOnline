package com.turtle.sudoku.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.turtle.sudoku.util.DateUtil;
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
	private String startDateTime;
	private long startMillis;
	private long endMillis;
	private String endDateTime;
	private Map<String, Boolean> userMap;
	public SudokuBoard() {
		startMillis = System.currentTimeMillis();
		startDateTime = DateUtil.format(startMillis, "yyyy-MM-dd HH:mm:ss");
		userMap = new HashMap<>();
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
		userMap.put(username, true);
		if (StringUtil.isEmpty(board[x][y].owner)) {
			board[x][y].owner = username;
		} else {
			return false;
		}
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j <board[i].length; j++) {
				if (board[i][j].owner.equals(username)) {
					board[i][j].owner = "";
				}
			}
		}
		if (StringUtil.isEmpty(board[x][y].owner)) {
			board[x][y].owner = username;
		}
		return true;
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
	public boolean verify() {
		if (sudokuType == 0) {
			return verifyNormal(0, 0);
		} else if (sudokuType == 1) {
			for (int i = 0; i < startPoint.length; i++) {
				int x = startPoint[i][0];
				int y = startPoint[i][1];
				if (!verifyNormal(x, y)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	private boolean verifyNormal(int dx, int dy) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				String v = board[i][j].value;
				if (v.length() != 1 || !StringUtils.isNumeric(v)) {
					return false;
				}
			}
		}
		//每一行
		for (int i = 0; i < 9; i++) {
			boolean b[] = new boolean[9];
			for (int j = 0; j < 9; j++) {
				int k = Integer.parseInt(board[i][j].value);
				b[k-1]=true;
			}
			for (i = 0; i < 9; i++) {
				if (!b[i]) {
					return false;
				}
			}
		}
		//每一列
		for (int i = 0; i < 9; i++) {
			boolean b[] = new boolean[9];
			for (int j = 0; j < 9; j++) {
				int k = Integer.parseInt(board[j][i].value);
				b[k-1]=true;
			}
			for (i = 0; i < 9; i++) {
				if (!b[i]) {
					return false;
				}
			}
		}
		//每一块
		for (int i = 0; i < 3; i+=3) {
			for (int j = 0; j <3; j+=3) {
				boolean b[] = new boolean[9];
				for (int x = 0; x < 3; x++) {
					for (int y = 0; y < 3; y++) {
						int k = Integer.parseInt(board[i+x][j+y].value);
						b[k-1]=true;
					}
				}
				for (i = 0; i < 9; i++) {
					if (!b[i]) {
						return false;
					}
				}
			}
		}
		return true;
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
	public Map<String, Boolean> getUserMap() {
		return userMap;
	}
	public void setUserMap(Map<String, Boolean> userMap) {
		this.userMap = userMap;
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
	public String getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}
	public long getStartMillis() {
		return startMillis;
	}
	public void setStartMillis(long startMillis) {
		this.startMillis = startMillis;
	}
	public long getEndMillis() {
		return endMillis;
	}
	public void setEndMillis(long endMillis) {
		this.endMillis = endMillis;
	}
	public String getEndDateTime() {
		return endDateTime;
	}
	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}
}

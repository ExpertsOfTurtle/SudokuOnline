
package com.turtle.sudoku.service;

import java.util.Map;

import com.turtle.sudoku.entity.SudokuResult;
import com.turtle.sudoku.model.SudokuResultModel;

public interface SudokuResultService{
	
	public int create(SudokuResultModel sudokuResultModel);
	
	public int createSelective(SudokuResultModel sudokuResultModel);
	
	public SudokuResultModel findByPrimaryKey(Integer id);
	
	public int updateByPrimaryKey(SudokuResultModel sudokuResultModel);
	
	public int updateByPrimaryKeySelective(SudokuResultModel sudokuResultModel);
	
	public int deleteByPrimaryKey(Integer id);
	

	public int selectCount(SudokuResultModel sudokuResultModel);
	
	public SudokuResultModel selectByGame(Map map);
	
	public int getRank(Integer gameId, Long timestamp);
}
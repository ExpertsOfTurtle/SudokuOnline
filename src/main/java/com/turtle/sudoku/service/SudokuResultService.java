
package com.turtle.sudoku.service;

import com.turtle.sudoku.model.SudokuResultModel;

public interface SudokuResultService{
	
	public int create(SudokuResultModel sudokuResultModel);
	
	public int createSelective(SudokuResultModel sudokuResultModel);
	
	public SudokuResultModel findByPrimaryKey(Integer id);
	
	public int updateByPrimaryKey(SudokuResultModel sudokuResultModel);
	
	public int updateByPrimaryKeySelective(SudokuResultModel sudokuResultModel);
	
	public int deleteByPrimaryKey(Integer id);
	

	public int selectCount(SudokuResultModel sudokuResultModel);
	
}
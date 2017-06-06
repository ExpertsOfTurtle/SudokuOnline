
package com.turtle.sudoku.service;

import com.turtle.sudoku.model.SudokuModel;

public interface SudokuService{
	
	public int create(SudokuModel sudokuModel);
	
	public int createSelective(SudokuModel sudokuModel);
	
	public SudokuModel findByPrimaryKey(Integer id);
	
	public int updateByPrimaryKey(SudokuModel sudokuModel);
	
	public int updateByPrimaryKeySelective(SudokuModel sudokuModel);
	
	public int deleteByPrimaryKey(Integer id);
	

	public int selectCount(SudokuModel sudokuModel);
	
}
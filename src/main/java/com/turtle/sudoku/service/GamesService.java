
package com.turtle.sudoku.service;

import com.turtle.sudoku.model.GamesModel;

public interface GamesService{
	
	public int create(GamesModel gamesModel);
	
	public int createSelective(GamesModel gamesModel);
	
	public GamesModel findByPrimaryKey(Integer id);
	
	public int updateByPrimaryKey(GamesModel gamesModel);
	
	public int updateByPrimaryKeySelective(GamesModel gamesModel);
	
	public int deleteByPrimaryKey(Integer id);
	

	public int selectCount(GamesModel gamesModel);
	
}
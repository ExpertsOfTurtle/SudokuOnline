package com.turtle.sudoku.repository;

import com.turtle.sudoku.entity.SudokuResult;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface SudokuResultRepository{

  	int deleteByPrimaryKey(Integer id);
	
	SudokuResult selectByPrimaryKey(Integer id);
	
	    
    int updateByPrimaryKey(SudokuResult sudokuResult);

    int updateByPrimaryKeySelective(SudokuResult sudokuResult);

  	int insert(SudokuResult sudokuResult);
  	
	int insertSelective(SudokuResult sudokuResult);


    int selectCount(SudokuResult sudokuResult);

    List<SudokuResult> selectPage(@Param("sudokuResult") SudokuResult sudokuResult, @Param("pageable") Pageable pageable);
	
}
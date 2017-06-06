package com.turtle.sudoku.repository;

import com.turtle.sudoku.entity.Sudoku;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface SudokuRepository{

  	int deleteByPrimaryKey(Integer id);
	
	Sudoku selectByPrimaryKey(Integer id);
	
	    
    int updateByPrimaryKey(Sudoku sudoku);

    int updateByPrimaryKeySelective(Sudoku sudoku);

  	int insert(Sudoku sudoku);
  	
	int insertSelective(Sudoku sudoku);


    int selectCount(Sudoku sudoku);

    List<Sudoku> selectPage(@Param("sudoku") Sudoku sudoku, @Param("pageable") Pageable pageable);
	
}
package com.turtle.sudoku.repository;

import com.turtle.sudoku.entity.Games;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface GamesRepository{

  	int deleteByPrimaryKey(Integer id);
	
	Games selectByPrimaryKey(Integer id);
	
	    
    int updateByPrimaryKey(Games games);

    int updateByPrimaryKeySelective(Games games);

  	int insert(Games games);
  	
	int insertSelective(Games games);


    int selectCount(Games games);

    List<Games> selectPage(@Param("games") Games games, @Param("pageable") Pageable pageable);
	
    List<Games> selectPendingGames(Long time);
    
    List<Games> selectRecentGames();
}

package com.turtle.sudoku.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turtle.sudoku.entity.Games;
import com.turtle.sudoku.repository.GamesRepository;
import com.turtle.sudoku.model.GamesModel;
import com.turtle.sudoku.service.GamesService;
import com.turtle.sudoku.util.BeanCopyUtils;


@Service
public class GamesServiceImpl implements GamesService {


	@Autowired
	private GamesRepository gamesRepo;
	
	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int deleteByPrimaryKey(Integer id) {
		return gamesRepo.deleteByPrimaryKey(id);
	}
	

    /*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public GamesModel findByPrimaryKey(Integer id) {
		Games games = gamesRepo.selectByPrimaryKey(id);
		return BeanCopyUtils.map(games, GamesModel.class);
	}
	
	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int updateByPrimaryKey(GamesModel gamesModel) {
		return gamesRepo.updateByPrimaryKey(BeanCopyUtils.map(gamesModel, Games.class));
	}
	
	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int updateByPrimaryKeySelective(GamesModel gamesModel) {
		return gamesRepo.updateByPrimaryKeySelective(BeanCopyUtils.map(gamesModel, Games.class));
	}
	

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int create(GamesModel gamesModel) {
		Games game = BeanCopyUtils.map(gamesModel, Games.class);
		int rt = gamesRepo.insert(game);
		gamesModel.setId(game.getId());
		return game.getId();
	}

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int createSelective(GamesModel gamesModel) {
		return gamesRepo.insertSelective(BeanCopyUtils.map(gamesModel, Games.class));
	}

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int selectCount(GamesModel gamesModel) {
		return gamesRepo.selectCount(BeanCopyUtils.map(gamesModel, Games.class));
	}


	@Override
	public List<GamesModel> selectPendingGames(Long time) {
		List<Games> list = gamesRepo.selectPendingGames(time);
		return BeanCopyUtils.mapList(list, GamesModel.class);
	}



}

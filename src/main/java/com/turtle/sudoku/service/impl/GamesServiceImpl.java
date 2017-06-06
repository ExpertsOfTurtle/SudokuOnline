
package com.turtle.sudoku.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.turtle.sudoku.entity.Games;
import com.turtle.sudoku.repository.GamesRepository;
import com.turtle.sudoku.model.GamesModel;
import com.turtle.sudoku.service.GamesService;
import com.vip.venus.core.beans.mapping.BeanMapper;

@Service
public class GamesServiceImpl implements GamesService {

	@Autowired
	private BeanMapper beanMapper;

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
		return beanMapper.map(games, GamesModel.class);
	}
	
	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int updateByPrimaryKey(GamesModel gamesModel) {
		return gamesRepo.updateByPrimaryKey(beanMapper.map(gamesModel, Games.class));
	}
	
	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int updateByPrimaryKeySelective(GamesModel gamesModel) {
		return gamesRepo.updateByPrimaryKeySelective(beanMapper.map(gamesModel, Games.class));
	}
	

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int create(GamesModel gamesModel) {
		return gamesRepo.insert(beanMapper.map(gamesModel, Games.class));
	}

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int createSelective(GamesModel gamesModel) {
		return gamesRepo.insertSelective(beanMapper.map(gamesModel, Games.class));
	}

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int selectCount(GamesModel gamesModel) {
		return gamesRepo.selectCount(beanMapper.map(gamesModel, Games.class));
	}



}

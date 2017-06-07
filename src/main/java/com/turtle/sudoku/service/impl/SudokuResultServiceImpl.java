
package com.turtle.sudoku.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.turtle.sudoku.entity.SudokuResult;
import com.turtle.sudoku.repository.SudokuResultRepository;
import com.turtle.sudoku.model.SudokuResultModel;
import com.turtle.sudoku.service.SudokuResultService;
import com.vip.venus.core.beans.mapping.BeanMapper;

@Service
public class SudokuResultServiceImpl implements SudokuResultService {

	@Autowired
	private BeanMapper beanMapper;

	@Autowired
	private SudokuResultRepository sudokuResultRepo;
	

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int deleteByPrimaryKey(Integer id) {
		return sudokuResultRepo.deleteByPrimaryKey(id);
	}
	

    /*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public SudokuResultModel findByPrimaryKey(Integer id) {
		SudokuResult sudokuResult = sudokuResultRepo.selectByPrimaryKey(id);
		return beanMapper.map(sudokuResult, SudokuResultModel.class);
	}
	
	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int updateByPrimaryKey(SudokuResultModel sudokuResultModel) {
		return sudokuResultRepo.updateByPrimaryKey(beanMapper.map(sudokuResultModel, SudokuResult.class));
	}
	
	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int updateByPrimaryKeySelective(SudokuResultModel sudokuResultModel) {
		return sudokuResultRepo.updateByPrimaryKeySelective(beanMapper.map(sudokuResultModel, SudokuResult.class));
	}
	

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int create(SudokuResultModel sudokuResultModel) {
		return sudokuResultRepo.insert(beanMapper.map(sudokuResultModel, SudokuResult.class));
	}

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int createSelective(SudokuResultModel sudokuResultModel) {
		return sudokuResultRepo.insertSelective(beanMapper.map(sudokuResultModel, SudokuResult.class));
	}

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int selectCount(SudokuResultModel sudokuResultModel) {
		return sudokuResultRepo.selectCount(beanMapper.map(sudokuResultModel, SudokuResult.class));
	}



}

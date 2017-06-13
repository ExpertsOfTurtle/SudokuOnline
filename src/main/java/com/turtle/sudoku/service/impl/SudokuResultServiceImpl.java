
package com.turtle.sudoku.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.turtle.sudoku.entity.SudokuResult;
import com.turtle.sudoku.repository.SudokuResultRepository;
import com.turtle.sudoku.model.SudokuResultModel;
import com.turtle.sudoku.service.SudokuResultService;
import com.turtle.sudoku.util.BeanCopyUtils;

@Service
public class SudokuResultServiceImpl implements SudokuResultService {

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
		return BeanCopyUtils.map(sudokuResult, SudokuResultModel.class);
	}
	
	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int updateByPrimaryKey(SudokuResultModel sudokuResultModel) {
		return sudokuResultRepo.updateByPrimaryKey(BeanCopyUtils.map(sudokuResultModel, SudokuResult.class));
	}
	
	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int updateByPrimaryKeySelective(SudokuResultModel sudokuResultModel) {
		return sudokuResultRepo.updateByPrimaryKeySelective(BeanCopyUtils.map(sudokuResultModel, SudokuResult.class));
	}
	

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int create(SudokuResultModel sudokuResultModel) {
		return sudokuResultRepo.insert(BeanCopyUtils.map(sudokuResultModel, SudokuResult.class));
	}

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int createSelective(SudokuResultModel sudokuResultModel) {
		return sudokuResultRepo.insertSelective(BeanCopyUtils.map(sudokuResultModel, SudokuResult.class));
	}

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int selectCount(SudokuResultModel sudokuResultModel) {
		return sudokuResultRepo.selectCount(BeanCopyUtils.map(sudokuResultModel, SudokuResult.class));
	}


	@Override
	public SudokuResultModel selectByGame(Map map) {
		SudokuResult sr = sudokuResultRepo.selectByGame(map);
		return BeanCopyUtils.map(sr, SudokuResultModel.class);
	}



}

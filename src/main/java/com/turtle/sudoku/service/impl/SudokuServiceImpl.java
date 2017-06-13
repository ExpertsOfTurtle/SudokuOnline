
package com.turtle.sudoku.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turtle.sudoku.entity.Sudoku;
import com.turtle.sudoku.repository.SudokuRepository;
import com.turtle.sudoku.model.SudokuModel;
import com.turtle.sudoku.service.SudokuService;
import com.turtle.sudoku.util.BeanCopyUtils;

@Service
public class SudokuServiceImpl implements SudokuService {

	@Autowired
	private SudokuRepository sudokuRepo;
	
	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int deleteByPrimaryKey(Integer id) {
		return sudokuRepo.deleteByPrimaryKey(id);
	}
	

    /*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public SudokuModel findByPrimaryKey(Integer id) {
		Sudoku sudoku = sudokuRepo.selectByPrimaryKey(id);
		return BeanCopyUtils.map(sudoku, SudokuModel.class);
	}
	
	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int updateByPrimaryKey(SudokuModel sudokuModel) {
		return sudokuRepo.updateByPrimaryKey(BeanCopyUtils.map(sudokuModel, Sudoku.class));
	}
	
	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int updateByPrimaryKeySelective(SudokuModel sudokuModel) {
		return sudokuRepo.updateByPrimaryKeySelective(BeanCopyUtils.map(sudokuModel, Sudoku.class));
	}
	

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int create(SudokuModel sudokuModel) {
		return sudokuRepo.insert(BeanCopyUtils.map(sudokuModel, Sudoku.class));
	}

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int createSelective(SudokuModel sudokuModel) {
		return sudokuRepo.insertSelective(BeanCopyUtils.map(sudokuModel, Sudoku.class));
	}

	/*
	 * @Transactional is not necessarry for the single atomic CRUD statement for better performance, 
	 * but you still have to take care of @Transactional for multi-statements scenario.
	 * if read only,please config as "@Transactional(readOnly = true)",otherwise "@Transactional"
	 */
	@Override
	public int selectCount(SudokuModel sudokuModel) {
		return sudokuRepo.selectCount(BeanCopyUtils.map(sudokuModel, Sudoku.class));
	}


	@Override
	public List<SudokuModel> selectByLevel(Integer level) {		
		List<Sudoku> list = sudokuRepo.selectByLevel(level);
		return BeanCopyUtils.mapList(list, SudokuModel.class);
	}


	@Override
	public SudokuModel selectByGameId(Integer gameId) {
		Sudoku sudoku = sudokuRepo.selectByGameId(gameId);
		return BeanCopyUtils.map(sudoku, SudokuModel.class);
	}


	@Override
	public int updateTimeAndResult(SudokuModel sudoku) {
		return sudokuRepo.updateTimeAndResult(BeanCopyUtils.map(sudoku, Sudoku.class));
	}



}

package com.turtle.sudoku.model;
import java.io.Serializable;

public class SudokuModel implements Serializable{
	
	private Integer id;
	private String problem;
	private String level;
		
	public void setId(Integer id){
		this.id = id;
	}
	
	public Integer getId(){
		return this.id;
	}
		
	public void setProblem(String problem){
		this.problem = problem;
	}
	
	public String getProblem(){
		return this.problem;
	}
		
	public void setLevel(String level){
		this.level = level;
	}
	
	public String getLevel(){
		return this.level;
	}
		
		
}
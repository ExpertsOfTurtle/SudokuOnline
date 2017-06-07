package com.turtle.sudoku.entity;


public class Sudoku{
	
	private Integer id;
	private String problem;
	private Integer level;
		
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
		
	public void setLevel(Integer level){
		this.level = level;
	}
	
	public Integer getLevel(){
		return this.level;
	}
		
		
}

















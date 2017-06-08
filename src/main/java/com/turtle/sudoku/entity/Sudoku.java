package com.turtle.sudoku.entity;


public class Sudoku{
	
	private Integer id;
	private String problem;
	private Integer level;
	private Long lastupdatetime;
	private Integer bestresult;
		
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
		
	public void setLastupdatetime(Long lastupdatetime){
		this.lastupdatetime = lastupdatetime;
	}
	
	public Long getLastupdatetime(){
		return this.lastupdatetime;
	}
		
	public void setBestresult(Integer bestresult){
		this.bestresult = bestresult;
	}
	
	public Integer getBestresult(){
		return this.bestresult;
	}
		
		
}

















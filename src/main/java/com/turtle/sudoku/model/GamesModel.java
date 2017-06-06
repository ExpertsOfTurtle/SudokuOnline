package com.turtle.sudoku.model;
import java.io.Serializable;

public class GamesModel implements Serializable{
	
	private Integer id;
	private String creator;
	private String createTime;
	private String startTime;
	private Integer capacity;
	private String title;
	private Integer level;
		
	public void setId(Integer id){
		this.id = id;
	}
	
	public Integer getId(){
		return this.id;
	}
		
	public void setCreator(String creator){
		this.creator = creator;
	}
	
	public String getCreator(){
		return this.creator;
	}
		
	public void setCreateTime(String createTime){
		this.createTime = createTime;
	}
	
	public String getCreateTime(){
		return this.createTime;
	}
		
	public void setStartTime(String startTime){
		this.startTime = startTime;
	}
	
	public String getStartTime(){
		return this.startTime;
	}
		
	public void setCapacity(Integer capacity){
		this.capacity = capacity;
	}
	
	public Integer getCapacity(){
		return this.capacity;
	}
		
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return this.title;
	}
		
	public void setLevel(Integer level){
		this.level = level;
	}
	
	public Integer getLevel(){
		return this.level;
	}
		
		
}
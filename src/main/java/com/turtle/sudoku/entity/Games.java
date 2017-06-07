package com.turtle.sudoku.entity;


public class Games{
	
	private Integer id;
	private String creator;
	private String createTime;
	private Long startTime;
	private Long endTime;
	private Integer capacity;
	private String title;
	private Integer level;
	private String status;
	private String datetime;
		
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
		
	public void setStartTime(Long startTime){
		this.startTime = startTime;
	}
	
	public Long getStartTime(){
		return this.startTime;
	}
		
	public void setEndTime(Long endTime){
		this.endTime = endTime;
	}
	
	public Long getEndTime(){
		return this.endTime;
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
		
	public void setStatus(String status){
		this.status = status;
	}
	
	public String getStatus(){
		return this.status;
	}
		
	public void setDatetime(String datetime){
		this.datetime = datetime;
	}
	
	public String getDatetime(){
		return this.datetime;
	}
		
		
}

















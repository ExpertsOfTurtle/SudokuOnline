package com.turtle.sudoku.model;
import java.io.Serializable;

public class SudokuResultModel implements Serializable{
	
	private Integer id;
	private String username;
	private Integer usetime;
	private Integer gameid;
	private Integer rank;
	private Long timestamp;
	private String datetime;
	private Integer level;
	private String gameMode;
	private String details;
		
	public void setId(Integer id){
		this.id = id;
	}
	
	public Integer getId(){
		return this.id;
	}
		
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getUsername(){
		return this.username;
	}
		
	public void setUsetime(Integer usetime){
		this.usetime = usetime;
	}
	
	public Integer getUsetime(){
		return this.usetime;
	}
		
	public void setGameid(Integer gameid){
		this.gameid = gameid;
	}
	
	public Integer getGameid(){
		return this.gameid;
	}
		
	public void setRank(Integer rank){
		this.rank = rank;
	}
	
	public Integer getRank(){
		return this.rank;
	}
		
	public void setTimestamp(Long timestamp){
		this.timestamp = timestamp;
	}
	
	public Long getTimestamp(){
		return this.timestamp;
	}
		
	public void setDatetime(String datetime){
		this.datetime = datetime;
	}
	
	public String getDatetime(){
		return this.datetime;
	}
		
	public void setLevel(Integer level){
		this.level = level;
	}
	
	public Integer getLevel(){
		return this.level;
	}
		
	public void setGameMode(String gameMode){
		this.gameMode = gameMode;
	}
	
	public String getGameMode(){
		return this.gameMode;
	}
		
	public void setDetails(String details){
		this.details = details;
	}
	
	public String getDetails(){
		return this.details;
	}
		
		
}
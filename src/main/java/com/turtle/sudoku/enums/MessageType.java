package com.turtle.sudoku.enums;

public enum MessageType {
	
	Start("Start"),
	Chat("Chat"),
	Join("Join"),
	Quit("Quit"),
	Update("Update"),
	Lock("Lock"),
	Unlock("Unlock")
	;
	
	private String value;
	
	MessageType(String val) {
		this.value = val;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}

package com.turtle.sudoku.enums;

public enum RequestType {
	
	Join("Join"),
	Quit("Quit"),
	Update("Update"),
	Lock("Lock"),
	Unlock("Unlock")
	;
	
	private String value;
	
	RequestType(String val) {
		this.value = val;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}

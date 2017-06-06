package com.turtle.sudoku.enums;

public enum RequestType {
	
	Lock("Lock"),
	Update("Update"),
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

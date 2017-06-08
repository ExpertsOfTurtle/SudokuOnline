package com.turtle.sudoku.enums;

public enum GameMode {
	PK("PK"),
	Cooperation("Cooperation")
	;
	private String value;

	GameMode(String val) {
		this.value = val;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

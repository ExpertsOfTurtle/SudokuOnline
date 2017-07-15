package com.turtle.sudoku.enums;

public enum SudokuLevel {
	TEST(0, "测试"),
	SUPER_EASY(1, "初心级"),
	VERY_EASY(2, "入门级"),
	EASY(3, "初级"),
	MEDIUM(4, "中级"),
	HARD(5, "高级"),
	VERY_HARD(6, "超高级"),
	INSANE(7, "轻度骨灰级"),
	VERY_INSANE(8, "中度骨灰级"),
	SUPER_INSANE(9, "重度骨灰级")
	;
	
	private Integer value;
	private String description;
	
	SudokuLevel(Integer level, String description) {
		this.value = level;
		this.description = description;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}

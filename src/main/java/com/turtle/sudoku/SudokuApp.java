package com.turtle.sudoku;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
@SpringBootApplication(scanBasePackages = "com.turtle.sudoku")
@MapperScan("com.turtle.sudoku.repository") 
public class SudokuApp {
	public static void main(String[] args) {
		SpringApplication.run(SudokuApp.class, args);
	}
}

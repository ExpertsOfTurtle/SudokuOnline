package com.turtle.sudoku.service;

import java.util.Map;

public interface HttpService {
	public <T>T sendAndGetResponse(String url, Map<String, String> header, String body, Class<T> clz);
}

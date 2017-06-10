package com.turtle.sudoku.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.turtle.sudoku.bean.RequestMessage;
import com.turtle.sudoku.bean.ResponseMessage;
import com.turtle.sudoku.bean.SocketRequest;

import net.sf.json.JSONObject;

//@Controller
public class WsController {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	protected void doResponse(String topic, Object object) {
		JSONObject json = JSONObject.fromObject(object);
		messagingTemplate.convertAndSend(topic, json.toString());
	}

	
}

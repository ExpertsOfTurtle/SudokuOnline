package com.turtle.sudoku.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.turtle.sudoku.bean.JoinGameRequest;
import com.turtle.sudoku.bean.JoinGameResponse;
import com.turtle.sudoku.bean.RequestMessage;
import com.turtle.sudoku.bean.ResponseMessage;
import com.turtle.sudoku.bean.SocketRequest;
import com.turtle.sudoku.enums.MessageType;

import net.sf.json.JSONObject;

@Controller
public class PKController extends WsController {
	@MessageMapping("/join")
	public void joinGame(JoinGameRequest request) {
		JoinGameResponse response = new JoinGameResponse();
		response.setGameId(request.getGameId());
		response.setUsername(request.getUsername());
		response.setMessageType(MessageType.Join.getValue());
		String topic = String.format("/topic/joingame/%d", request.getGameId());
		doResponse(topic, response);
	}
	
	@MessageMapping("/welcome")
	// @SendTo("/topic/getResponse")
	public void say(RequestMessage message) {
		String topic = String.format("/topic/wf/%d", ((int) (Math.random() * 100)) % 3);
		ResponseMessage rsp = new ResponseMessage("welcome," + message.getName() + " !");
		JSONObject json = JSONObject.fromObject(rsp);
		System.out.println("Topic:" + topic + " Response:" + json.toString());
		
		doResponse(topic, rsp);
	}

	@MessageMapping("/update")
	public ResponseMessage update(SocketRequest request) {
		System.out.println(request.getRequestType());
		return new ResponseMessage("...." + request.getRequestType());
	}

	@RequestMapping("/wf")
	public String hello4() {

		return "sudoku/index";
	}
}

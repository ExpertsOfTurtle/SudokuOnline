package com.turtle.sudoku.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.turtle.sudoku.bean.RequestMessage;
import com.turtle.sudoku.bean.ResponseMessage;
import com.turtle.sudoku.bean.SocketRequest;

@Controller
public class WsController {
    @MessageMapping("/welcome")
    @SendTo("/topic/getResponse")
    public ResponseMessage say(RequestMessage message) {
        System.out.println(message.getName() + "," + message.getMsg());
        return new ResponseMessage("welcome," + message.getName() + " !");
    }
    
    @MessageMapping("/update")
    @SendTo("/topic/getResponse")
    public ResponseMessage update(SocketRequest request) {
        System.out.println(request.getRequestType());
        return new ResponseMessage("...." + request.getRequestType());
    }
    
    @RequestMapping("/wf" )
    public String hello4(){
         
         return "sudoku/index";
   }
}

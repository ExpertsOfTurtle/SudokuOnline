package com.turtle.sudoku.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.turtle.sudoku.bean.RequestMessage;
import com.turtle.sudoku.bean.ResponseMessage;

/**
 * Created by sang on 16-12-22.
 */
@Controller
public class WsController {
    @MessageMapping("/welcome")
    @SendTo("/topic/getResponse")
    public ResponseMessage say(RequestMessage message) {
        System.out.println(message.getName() + "," + message.getMsg());
        return new ResponseMessage("welcome," + message.getName() + " !");
    }
    
    @RequestMapping("/wf" )
    public String hello4(){
         
         return "com/turtle/demo/index";
   }
}

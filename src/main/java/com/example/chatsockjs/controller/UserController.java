package com.example.chatsockjs.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.chatsockjs.dto.UserDto;

@Controller
public class UserController {

	public static final List<UserDto> USERS = new ArrayList<>();

	@MessageMapping("/user.add")
	@SendTo("/topic/contacts")
	public UserDto addUser(@Payload UserDto dto, SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("username", dto.getUsername());
		dto.setType("LOGIN");
		USERS.add(dto);
		return dto;
	}

	@GetMapping("/contacts")
	@ResponseBody
	public List<UserDto> listUser(@RequestHeader("username") String username) {
		return USERS.stream().filter(t -> t != null && !t.getUsername().equals(username)).collect(Collectors.toList());
	}
}

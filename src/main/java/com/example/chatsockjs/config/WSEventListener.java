package com.example.chatsockjs.config;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.chatsockjs.controller.HomeController;
import com.example.chatsockjs.controller.UserController;
import com.example.chatsockjs.dto.UserDto;

@Component
public class WSEventListener {

	private static final Logger logger = LoggerFactory.getLogger(WSEventListener.class);

	@Autowired
	private SimpMessagingTemplate messageTemplate;

	@EventListener
	public void handleWSConnectListener(SessionConnectedEvent event) {
	}

	@EventListener
	public void handleWSDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String username = (String) headerAccessor.getSessionAttributes().get("username");
		if (StringUtils.hasText(username)) {
			UserController.USERS.removeIf((user) -> user != null && user.getUsername().equals(username));
			UserDto userDto = new UserDto();
			userDto.setType("LOGOUT");
			userDto.setUsername(username);
			this.messageTemplate.convertAndSend("/topic/contacts", userDto);
		}
	}
}

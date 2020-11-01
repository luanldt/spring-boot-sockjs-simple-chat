package com.example.chatsockjs.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.chatsockjs.dto.ChatMessageDto;

@Controller
public class MessageController {
	private static Map<String, List<ChatMessageDto>> DB = new HashMap<>();
	private static LongAdder SEQUENCE_MESSAGE = new LongAdder();

	@MessageMapping("/message.send")
	@SendTo("/topic/messages")
	public ChatMessageDto sendMessage(@Payload ChatMessageDto dto, SimpMessageHeaderAccessor headerAccersor) {
		String senderUser = (String) headerAccersor.getSessionAttributes().get("username");
		if (StringUtils.hasText(senderUser)) {
			String receiverUser = dto.getReceiver();
			dto.setSender(senderUser);
			SEQUENCE_MESSAGE.increment();
			dto.setId(SEQUENCE_MESSAGE.longValue());
			String key = getKey(senderUser, receiverUser);
			DB.computeIfAbsent(key, (k) -> new ArrayList<>());
			DB.get(key).add(dto);
		}
		return dto;
	}

	@GetMapping("/messages")
	@ResponseBody
	public List<ChatMessageDto> listMessage(@RequestHeader("username") String username,
			@RequestParam("receiver") String receiver) {
		String key = getKey(username, receiver);
		DB.computeIfAbsent(key, (k) -> new ArrayList<>());
		List<ChatMessageDto> messages = DB.get(key);
		return messages;
	}

	private String getKey(String senderUser, String receiverUser) {
		String key = senderUser.hashCode() > receiverUser.hashCode() ? senderUser + receiverUser
				: receiverUser + senderUser;
		return key;
	}
}

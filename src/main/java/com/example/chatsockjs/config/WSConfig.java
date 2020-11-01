package com.example.chatsockjs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WSConfig implements WebSocketMessageBrokerConfigurer {

	/**
	 * Register WS end point where client use to connect to WS server.
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").withSockJS().setWebSocketEnabled(false); // withSockJS() enable fallback SockJS
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// message have target start with /app will consume process message websocket
		registry.setApplicationDestinationPrefixes("/app");
		// message have target start with /top will route to agency, agency push message
		// to all client subcribe to topic
		registry.enableSimpleBroker("/topic");
	}

}

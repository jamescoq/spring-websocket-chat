package cz.encode.chat.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import cz.encode.chat.server.interceptor.ConnectionChannelInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@ComponentScan(basePackages = { "cz.encode.chat.server.web" })
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Bean
	public ConnectionChannelInterceptor connectionChannelInterceptor() {
		return new ConnectionChannelInterceptor();
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/queue/", "/user");
		registry.setApplicationDestinationPrefixes("/app");
	}

	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/chat").withSockJS();
	}
	
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.setInterceptors(connectionChannelInterceptor());
	}
	
	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		registration.setInterceptors(connectionChannelInterceptor());
	}

}



package cz.encode.chat.server.interceptor;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

import cz.encode.chat.server.cache.UserSubscriptionCache;

public class ConnectionChannelInterceptor extends ChannelInterceptorAdapter {

	private static Logger log = java.util.logging.Logger.getLogger("MessageController");
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	private UserSubscriptionCache userSubscriptionCache;

	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);

		if (stompHeaderAccessor.getCommand() == null) {
			return;
		}

		switch (stompHeaderAccessor.getCommand()) {
		case CONNECT:
			log.info("User connect called, sessId " + stompHeaderAccessor.getSessionId() + " subsId " + stompHeaderAccessor.getSubscriptionId());
			userSubscriptionCache.addUser(stompHeaderAccessor.getUser().getName());
			break;
		case DISCONNECT:
			log.info("User disconnected called, sessId " + stompHeaderAccessor.getSessionId() + " subsId " + stompHeaderAccessor.getSubscriptionId());
			userSubscriptionCache.deactivateUser(stompHeaderAccessor.getUser().getName());
			simpMessagingTemplate.convertAndSend("/queue/users", userSubscriptionCache.getActiveUsers());
			break;
		case SUBSCRIBE:
			log.info("User subscribe called, sessId " + stompHeaderAccessor.getSessionId() + " subsId " + stompHeaderAccessor.getSubscriptionId());
			String destination = stompHeaderAccessor.getDestination();
			if (destination.contains("±") && destination.contains("/user/")) {
				String subscriptionId = destination.replace("/user/", "").replace("/queue/private", "").trim();
				userSubscriptionCache.addUserSubscription(stompHeaderAccessor.getUser().getName(), subscriptionId);
			}
			break;
		default:
			break;
		}
	}
}

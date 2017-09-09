package cz.encode.chat.server.web;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.encode.chat.server.cache.UserSubscriptionCache;
import cz.encode.chat.server.dto.RequestMessage;
import cz.encode.chat.server.dto.ResponseMessage;
import cz.encode.chat.server.dto.UserDto;
import cz.encode.chat.server.service.MessageService;

@Controller
public class MessageController {

	private static final String GROUP_SESSION = "group";
	private static Logger log = java.util.logging.Logger.getLogger("MessageController");

	@Autowired
	private MessageService messageService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private UserSubscriptionCache userSubscriptionCache;

	@MessageMapping("/public")
	@SendTo("/queue/public")
	public ResponseMessage publicMessage(RequestMessage message, Principal principal) {
		log.info("Request received for mapping: /public");
		log.info("User : " + principal.getName() + ", content: " + message.getContent());

		return messageService.saveMessage(message, principal.getName(), GROUP_SESSION);
	}

	@MessageMapping("/private/{sessionId}")
	public void privateMessage(@DestinationVariable("sessionId") String sessionId, RequestMessage requestMessage,
			Principal principal) {
		log.info("Request received for mapping: /private");
		log.info("User : " + principal.getName() + ", content: " + requestMessage.getContent());
		ResponseMessage responseMessage = messageService.saveMessage(requestMessage, principal.getName(), sessionId);
		messagingTemplate.convertAndSendToUser(sessionId, "/queue/private", responseMessage);
	}

	@MessageMapping("/session/{sessionId}")
	public void newSessionInfo(@DestinationVariable("sessionId") String sessionId, Principal principal) {
		log.info("Request received for mapping: /session");
		log.info("User : " + principal.getName() + ", new session: " + sessionId);

		String receiver = sessionId.split("±")[0];

		messagingTemplate.convertAndSendToUser(receiver, "/queue/private", sessionId);
	}

	@SubscribeMapping("/history/{sessionId}")
	public List<ResponseMessage> clientHistory(@DestinationVariable("sessionId") String sessionId, Principal principal) {
		log.info("Client history subscribtion received for user " + principal.getName());

		return messageService.getHistory(sessionId);
	}

	@SubscribeMapping("/users")
	@SendTo("/queue/users")
	public Map<String, UserDto> getUsers() {
		log.info("Connected users list send.");
		return userSubscriptionCache.getActiveUsers();
	}

}

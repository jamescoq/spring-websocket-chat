package cz.encode.chat.server.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cz.encode.chat.server.dto.RequestMessage;
import cz.encode.chat.server.dto.ResponseMessage;
import cz.encode.chat.server.dto.UserDto;
import cz.encode.chat.server.entity.Message;
import cz.encode.chat.server.repository.MessageRepository;

@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private SimpUserRegistry simpUserRegistry;

	private SimpleDateFormat timeFormat;

	@PostConstruct
	public void init() {
		timeFormat = new SimpleDateFormat("HH:mm:ss");
	}

	@Override
	public ResponseMessage saveMessage(RequestMessage message, String authorName, String sessionId) {
		Assert.notNull(authorName, "Name must not be null.");
		Assert.notNull(message, "Message must not be null.");
		Assert.notNull(sessionId, "SubscriptionId must not be null.");

		Message messageEntity = messageRepository
				.save(new Message(message.getContent(), authorName, message.getReceiver(), message.getRestricted(), sessionId));

		return new ResponseMessage(timeFormat.format(messageEntity.getDate()), messageEntity.getContent(),
				getAuthorName(authorName));
	}

	@Override
	public List<ResponseMessage> getHistory(String sessionId) {
		List<Message> messages = messageRepository.findBySessionId(sessionId);
		List<ResponseMessage> responseMessages = new ArrayList<ResponseMessage>();

		for (Message message : messages) {
			responseMessages.add(new ResponseMessage(timeFormat.format(message.getDate()), message.getContent(),
					getAuthorName(message.getAuthor())));
		}

		return responseMessages;
	}

	@Override
	public List<UserDto> getSubscriptions() {
		Set<SimpUser> simpUsers = simpUserRegistry.getUsers();
		List<UserDto> users = new ArrayList<UserDto>();
		String userDestinationPrefix = "/user/";

		for (SimpUser simpUser : simpUsers) {
			UserDto userDto = new UserDto();
			userDto.setName(simpUser.getName());
			for (SimpSession simpSession : simpUser.getSessions()) {
				List<String> subscriptions = new ArrayList<>();
				for (SimpSubscription simpSubscription : simpSession.getSubscriptions()) {
					String destination = simpSubscription.getDestination();
					if (destination.contains(userDestinationPrefix) && destination.contains("±")) {
						subscriptions.add(destination);
					}
				}
				userDto.setSubscriptions(subscriptions);
			}
			users.add(userDto);
		}

		return users;
	}

	private String getAuthorName(String authorName) {
		return authorName.substring(0, authorName.indexOf("_"));
	}
}

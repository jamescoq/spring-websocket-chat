package cz.encode.chat.server.service;

import java.util.List;

import cz.encode.chat.server.dto.RequestMessage;
import cz.encode.chat.server.dto.ResponseMessage;
import cz.encode.chat.server.dto.UserDto;

public interface MessageService {

	ResponseMessage saveMessage(RequestMessage message, String name,  String sessionId);

	List<ResponseMessage> getHistory(String name);

	List<UserDto> getSubscriptions();

}

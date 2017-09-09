package cz.encode.chat.server.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cz.encode.chat.server.dto.UserDto;

public class UserSubscriptionCache implements Serializable{

	private Map<String, UserDto> users = new ConcurrentHashMap<>();
	
	public Map<String, UserDto> getActiveUsers() {
		Map<String, UserDto> userCopy = new HashMap<>();
		for (UserDto userDto : users.values()) {
			if(userDto.getActive()) {
				userCopy.put(userDto.getUserId(), userDto);
			}
		}
		
		return userCopy;
	}
	
	public void addUserSubscription(String userId, String subscription) {
		if(userExists(userId) && !users.get(userId).getSubscriptions().contains(subscription)) {
			users.get(userId).getSubscriptions().add(subscription);
		}
	}
	
	public void addUser(String userId) {
		if(!userExists(userId)) {
			users.put(userId, createUserDto(userId));
		} else {
			users.get(userId).setActive(true);
		}
		
	}

	public void addUser(String userId, List<String> subscription) {
		if(!userExists(userId)) {
			users.put(userId, createUserDto(userId, subscription));
		}
	}
	
	public void deactivateUser(String userId) {
		if(userExists(userId)) {
			users.get(userId).setActive(false);
		}
	}

	private Boolean userExists(String userId) {
		return users.containsKey(userId);
	}

	private UserDto createUserDto(String userId) {
		return createUserDto(userId, null);
	}
	
	private UserDto createUserDto(String userId, List<String> subscription) {
		String userName = userId.split("_")[0];
		return (subscription == null) ? new UserDto(userId, userName): new UserDto(userId, userName, subscription);
	}
}

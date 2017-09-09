package cz.encode.chat.server.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserDto implements Serializable {

	private String userId;
	private String name;
	private List<String> subscriptions;
	private Boolean active;
	private String color;

	public UserDto() {
	}

	public UserDto(String userId, String userName) {
		this.userId = userId;
		this.name = userName;
		this.active = true;
	}

	public UserDto(String userId, String userName, List<String> subscription) {
		this.userId = userId;
		this.name = userName;
		this.subscriptions = subscription;
		this.active = true;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getSubscriptions() {
		if(subscriptions == null) {
			subscriptions = new ArrayList<>();
		}
		return subscriptions;
	}

	public void setSubscriptions(List<String> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}

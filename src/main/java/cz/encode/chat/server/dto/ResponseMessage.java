package cz.encode.chat.server.dto;

import java.io.Serializable;

public class ResponseMessage implements Serializable {

	private String date;
	private String content;
	private String user;

	public ResponseMessage() {
	}

	public ResponseMessage(String date, String content, String user) {
		this.date = date;
		this.content = content;
		this.user = user;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}

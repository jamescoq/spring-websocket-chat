package cz.encode.chat.server.dto;

public class RequestMessage {

	private String content;

	private String receiver;

	private Boolean restricted;

	public RequestMessage() {
	}

	public RequestMessage(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Boolean getRestricted() {
		return restricted;
	}

	public void setRestricted(Boolean restricted) {
		this.restricted = restricted;
	}


}

package cz.encode.chat.server.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Message implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Date date;
	private String content;
	private String author;
	private Boolean restricted;
	private String receiver;
	private String sessionId;

	public Message() {
	}

	public Message(String content, String author, String receiver, Boolean restricted, String sessionId) {
		this.content = content;
		this.author = author;
		this.receiver = receiver;
		this.restricted = restricted;
		this.date = new Date();
		this.sessionId = sessionId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Boolean getRestricted() {
		return restricted;
	}

	public void setRestricted(Boolean restricted) {
		this.restricted = restricted;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}

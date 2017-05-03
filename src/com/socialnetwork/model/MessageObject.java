package com.socialnetwork.model;

public class MessageObject {
	protected String id;
	protected String message;

	public MessageObject() {
	}

	public MessageObject(String id, String message) {
		this.id = id;
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

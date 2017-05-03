package com.socialnetwork.model;

import java.util.ArrayList;
import java.util.List;

public class UserObject extends Object {
	private String id;
	private String name;
	private String gender;
	private List<MessageObject> message = new ArrayList<MessageObject>();

	public UserObject() {
	}

	public UserObject(String id, String name, String gender) {
		this.id = id;
		this.name = name;
		this.gender = gender;
	}

	public UserObject(String id, String name, String gender, String content) {
		this.id = id;
		this.name = name;
		this.gender = gender;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public boolean equals(Object obj) {
		UserObject user = (UserObject) obj;
		return (this.id).equals(user.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public List<MessageObject> getMessage() {
		return message;
	}

	public void setMessage(List<MessageObject> message) {
		this.message = message;
	}

	public void addMessage(String id, String mes) {
		message.add(new MessageObject(id, mes));
	}
}

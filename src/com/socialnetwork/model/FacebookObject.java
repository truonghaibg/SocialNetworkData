package com.socialnetwork.model;

import java.util.Map;

public class FacebookObject {
	private String id;
	private String name;
	private String gender;
	private String messageId;
	private String message;
	private String fullMessage;
	private Map<String, Integer> oneGram;
	private Map<String, Integer> twoGram;
	private Map<String, Integer> threeGram;

	public Integer getTotalWord() {
		Integer dem = 0;
		if (oneGram == null || oneGram.isEmpty()) {
			return dem;
		}
		for (Map.Entry<String, Integer> entry : oneGram.entrySet()) {
			Integer value = entry.getValue();
			dem = dem + value;
		}
		return dem;
	}

	public FacebookObject(String id, String name, String gender, String messageId, String message) {
		this.id = id;
		this.name = name;
		this.gender = gender;
		this.messageId = messageId;
		this.message = message;
	}

	public void mergedMessage(String str) {
		this.fullMessage = this.fullMessage + " " + str;
	}

	public FacebookObject(String gender, String messageId, String message) {
		this.gender = gender;
		this.messageId = messageId;
		this.message = message;
	}

	public FacebookObject() {

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

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Integer> getCountByWords() {
		return oneGram;
	}

	public void setCountByWords(Map<String, Integer> countByWords) {
		this.oneGram = countByWords;
	}

	public Map<String, Integer> getTwoGram() {
		return twoGram;
	}

	public void setTwoGram(Map<String, Integer> twoGram) {
		this.twoGram = twoGram;
	}

	public Map<String, Integer> getThreeGram() {
		return threeGram;
	}

	public void setThreeGram(Map<String, Integer> threeGram) {
		this.threeGram = threeGram;
	}

	public String getFullMessage() {
		return fullMessage;
	}

	public void setFullMessage(String fullMessage) {
		this.fullMessage = fullMessage;
	}

}

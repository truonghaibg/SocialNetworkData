package com.common;

public class CustomThread implements Runnable {
	private String status;
	private String newStatus;

	public CustomThread() {

	}

	public CustomThread(String status) {
		this.status = status;
		newStatus = null;
	}

	@Override
	public void run() {
		// VietTokenizer viet = new VietTokenizer();
		// newStatus = viet.segment(status);
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.newStatus;
	}
}

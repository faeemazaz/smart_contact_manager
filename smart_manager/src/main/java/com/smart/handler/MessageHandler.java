package com.smart.handler;

public class MessageHandler {
	private String content;
	private String type;

	public MessageHandler(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}

	public MessageHandler() {
		super();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}

package com.stf.oauth.admin.commons.bean;

import java.io.Serializable;

/**
 * @author team ssoinnova4j
 *
 * Clase que representa los mensajes o notificaciones que se comparten
 * desde los controller hasta las paginas
 *
 */
public class Notification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Type {
		MODAL, ALERT, CONFIRM
	}

	public enum Status {
		SUCCES, WARNING, ERROR
	}

	private String type = new String();
	private String status = new String();
	private String title = new String();
	private String message = new String();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message1) {
		message = message1;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void alert(String type, String status, String message) {
		this.type = type;
		this.status = status;
		this.message = message;
	}

	public void confirmDialog(String type, String status, String message, String title) {
		this.type = type;
		this.status = status;
		this.title = title;
		this.message = message;
	}
}

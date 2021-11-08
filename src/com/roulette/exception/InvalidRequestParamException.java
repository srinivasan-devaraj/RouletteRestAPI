package com.roulette.exception;

import org.apache.http.HttpStatus;
import lombok.Getter;

/**
 * Exception Class used to store the custom message.
 * @author srini
 */
@Getter
public class InvalidRequestParamException extends Exception {

	private static final long serialVersionUID = 1L;
	private int errorCode = HttpStatus.SC_BAD_REQUEST;
	private String paramName;
	
	public InvalidRequestParamException(String paramName) {
		super(paramName);
		this.paramName = paramName;
	}
}

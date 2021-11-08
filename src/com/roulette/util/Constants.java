package com.roulette.util;

import org.apache.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Used to store all the constants in single place
 * @author srini
 */
public class Constants {

	public static final String GAME_OPEN = "open";
	public static final String GAME_CLOSE = "close";
	public static final String GAME_THROWN = "thrown";
	public static final String GAME_END = "end";
	public static final String GAME_UNKNOWN = "unknown";
	
	
	public static final String RESPONSE_MESSAGE = "message";
	public static final String RESPONSE_CODE = "code";
	public static final String RESPONSE_SUCCESS = "success";
	
	public static final String BET_DEFAULT_STATUS = "in-progress";
	public static final String BET_STATUS_WON = "won";
	public static final String BET_STATUS_LOSE = "lose";
	
	/**
	 * Enum provides the application-specific custom response code and message
	 */
	@Getter
	@AllArgsConstructor
	public enum CustomResponse{
		SUCCESS(HttpStatus.SC_OK, RESPONSE_SUCCESS),
		INVALID_REQUEST_PARAM(HttpStatus.SC_BAD_REQUEST, "Invalid request parameters, make sure the request params are valid"),
		INTERNAL_SERVER_ERROR(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Internal server error, please try again the request after sometime"),
		RESOURCE_EXISTS(2005, "Resource already exists"),
		INVALID_RESOURCE(2006, "Invalid resource id"),
		UNAUTH_ACCESS(2007, "Unauthorized access"),
		GAME_NOT_CLOSED(3005, "Game not in closed state"),
		GAME_NOT_OPNED(3006, "Game not in open state"),
		USER_NOT_IN_CASINO(3007, "User not in the corresponding casino"),
		USER_NOT_IN_ANY_CASINO(3008, "User not in any corresponding casino"),
		INSUFFICIENT_BALANCE(3009, "In-sufficient balance");
		
		private int code;
		private String message;
	}
	
	/**
	 * Validates the given email id is in valid format or not
	 * @param email
	 * @return
	 */
	public static boolean isValid(String email) {
	   String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	   return email.matches(regex);
	}
}

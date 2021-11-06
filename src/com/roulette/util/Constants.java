package com.roulette.util;

public class Constants {

	public static final String GAME_OPEN = "open";
	public static final String GAME_CLOSE = "close";
	public static final String GAME_THROWN = "thrown";
	public static final String GAME_END = "end";
	public static final String GAME_UNKNOWN = "unknown";
	
	public static final String RESPONSE_MESSAGE = "message";
	public static final String RESPONSE_CODE = "response-code";
	public static final String RESPONSE_SUCCESS = "success";
	
	public static boolean isValid(String email) {
	   String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	   return email.matches(regex);
	}
}

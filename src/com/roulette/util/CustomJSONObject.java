package com.roulette.util;

import java.util.Map;

import org.json.simple.JSONObject;

import com.roulette.util.Constants.CustomResponse;

/**
 * Extended JSONObject to make the methods more convinent to use and to returns the instance for all the most used methods.
 * @author srini
 */
public class CustomJSONObject extends JSONObject{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Default constructors which by default sets the response code and message as success
	 */
	public CustomJSONObject(){
		super();
		loadDefaultResponse();
	}
	
	/**
	 * Boolean used to avoid the default response content
	 * @param isCodeAndMessageNeeded
	 */
	public CustomJSONObject(boolean isCodeAndMessageNeeded){
		super();
		if(isCodeAndMessageNeeded) {
			loadDefaultResponse();
		}
	}
	/**
	 * Loads default response code and message in the newly created JSONObject
	 */
	public void loadDefaultResponse() {
		super.put(Constants.RESPONSE_MESSAGE, CustomResponse.SUCCESS.getMessage());
		super.put(Constants.RESPONSE_CODE, CustomResponse.SUCCESS.getCode());
	}
	
	/**
	 * Default put modified to return the instance reference.
	 */
	public CustomJSONObject put(Object key, Object value) {
		super.put(key, value);
		return this;
	}
	
	/**
	 * Used to sets the response message
	 * @param value
	 * @return
	 */
	public CustomJSONObject message(Object value) {
		super.put(Constants.RESPONSE_MESSAGE, value);
		return this;
	}
	
	/**
	 * Used to set the response code
	 * @param value
	 * @return
	 */
	public CustomJSONObject responseCode(Object value) {
		super.put(Constants.RESPONSE_CODE, value);
		return this;
	}
	
	/**
	 * Used to set the custom response code and message from the Enum
	 * @param customResponse
	 * @return
	 */
	public CustomJSONObject respond(CustomResponse customResponse) {
		super.put(Constants.RESPONSE_MESSAGE, customResponse.getMessage());
		super.put(Constants.RESPONSE_CODE, customResponse.getCode());
		return this;
	}
	
	/**
	 * Default putAll() modified to return the instance reference.
	 * @param obj
	 * @return
	 */
	public CustomJSONObject putAllObj(Map obj) {
		super.putAll(obj);
		return this;
	}
	
}

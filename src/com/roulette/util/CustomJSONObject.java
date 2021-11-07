package com.roulette.util;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;

import com.roulette.util.Constants.CustomResponse;

public class CustomJSONObject extends JSONObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CustomJSONObject(){
		super();
		loadDefaultResponse();
	}
	
	public CustomJSONObject(boolean isCodeAndMessageNeeded){
		super();
		if(isCodeAndMessageNeeded) {
			loadDefaultResponse();
		}
	}
	
	public void loadDefaultResponse() {
		super.put(Constants.RESPONSE_MESSAGE, CustomResponse.SUCCESS.getMessage());
		super.put(Constants.RESPONSE_CODE, CustomResponse.SUCCESS.getCode());
	}
	
	public CustomJSONObject put(Object key, Object value) {
		super.put(key, value);
		return this;
	}
	
	public CustomJSONObject message(Object value) {
		super.put(Constants.RESPONSE_MESSAGE, value);
		return this;
	}
	
	public CustomJSONObject responseCode(Object value) {
		super.put(Constants.RESPONSE_CODE, value);
		return this;
	}
	
	public CustomJSONObject respond(CustomResponse customResponse) {
		super.put(Constants.RESPONSE_MESSAGE, customResponse.getMessage());
		super.put(Constants.RESPONSE_CODE, customResponse.getCode());
		return this;
	}
	
	public CustomJSONObject putAllObj(Map obj) {
		super.putAll(obj);
		return this;
	}
	
}

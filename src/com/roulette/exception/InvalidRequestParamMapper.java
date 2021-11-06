package com.roulette.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.json.simple.JSONObject;

import com.roulette.util.Constants;

@Provider
public class InvalidRequestParamMapper implements ExceptionMapper<com.roulette.exception.InvalidRequestParamException>{

	@Override
	public Response toResponse(InvalidRequestParamException ex) {
		JSONObject response = new JSONObject();
		response.put(Constants.RESPONSE_MESSAGE, "Make sure the request param ["+ex.getParamName()+"] is valid");
		response.put(Constants.RESPONSE_CODE, ex.getErrorCode());
		
		return Response.status(ex.getErrorCode()).entity(response).type(MediaType.APPLICATION_JSON)
				.build();
	}

}

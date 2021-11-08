package com.roulette.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.roulette.util.CustomJSONObject;

/**
 * Class used to handle all the InvalidRequestParamException here and send response to the user to know that there is an issue in the request param
 * @author srini
 */
@Provider
public class InvalidRequestParamMapper implements ExceptionMapper<com.roulette.exception.InvalidRequestParamException>{

	@Override
	public Response toResponse(InvalidRequestParamException ex) {
		CustomJSONObject response = new CustomJSONObject()
				.message("Make sure the request param ["+ex.getParamName()+"] is valid")
				.responseCode(ex.getErrorCode());
		
		return Response.status(ex.getErrorCode()).entity(response).type(MediaType.APPLICATION_JSON)
				.build();
	}

}

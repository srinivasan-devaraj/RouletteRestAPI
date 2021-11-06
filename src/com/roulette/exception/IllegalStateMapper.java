package com.roulette.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpStatus;
import org.glassfish.jersey.server.monitoring.ExceptionMapperMXBean;
import org.json.simple.JSONObject;

import com.roulette.util.Constants;

@Provider
public class IllegalStateMapper implements ExceptionMapper<java.lang.IllegalStateException> {

	@Override
	public Response toResponse(IllegalStateException ex) {
		JSONObject response = new JSONObject();
		response.put(Constants.RESPONSE_MESSAGE, "Internal server error, make sure the request params are valid");
		response.put(Constants.RESPONSE_CODE, HttpStatus.SC_INTERNAL_SERVER_ERROR);
		
		return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON)
				.build();
	}

}

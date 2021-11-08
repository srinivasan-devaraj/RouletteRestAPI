package com.roulette.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpStatus;
import com.roulette.util.Constants.CustomResponse;
import com.roulette.util.CustomJSONObject;

/**
 * Class used to handle all the IllegalStateException here and send some generic response to the user to know something went wrong in the server.
 * @author srini
 */
@Provider
public class IllegalStateMapper implements ExceptionMapper<java.lang.IllegalStateException> {

	@Override
	public Response toResponse(IllegalStateException ex) {
		CustomJSONObject response = new CustomJSONObject().respond(CustomResponse.INVALID_REQUEST_PARAM);
		return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON)
				.build();
	}

}

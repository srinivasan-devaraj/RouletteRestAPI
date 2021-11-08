package com.roulette.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.roulette.util.CustomJSONObject;
import com.roulette.util.Constants.CustomResponse;

/**
 * Class used to handle all the unhandled exception here and send some generic response to the user to know something went wrong in the server.
 * @author srini
 */

@Provider
public class AllExceptionMapper implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		CustomJSONObject response = new CustomJSONObject().respond(CustomResponse.INTERNAL_SERVER_ERROR);
		// Log the trace
		exception.printStackTrace();
		return Response.status(CustomResponse.INTERNAL_SERVER_ERROR.getCode()).entity(response).type(MediaType.APPLICATION_JSON).build();
	}

}

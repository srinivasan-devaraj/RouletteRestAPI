package com.roulette.exception;

import java.sql.SQLException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.roulette.util.Constants.CustomResponse;
import com.roulette.util.CustomJSONObject;

/**
 * Class used to handle all the SQLException here and send some generic response to the user to know something went wrong in the server.
 * @author srini
 */
@Provider
public class SQLExceptionMapper implements ExceptionMapper<SQLException>{

	@Override
	public Response toResponse(SQLException ex) {
		CustomJSONObject response = new CustomJSONObject().respond(CustomResponse.INTERNAL_SERVER_ERROR);
		// Log the trace
		return Response.status(CustomResponse.INTERNAL_SERVER_ERROR.getCode()).entity(response).type(MediaType.APPLICATION_JSON).build();
	}

}

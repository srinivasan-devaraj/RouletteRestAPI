package com.roulette.exception;

import java.sql.SQLException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.roulette.util.Constants.CustomResponse;
import com.roulette.util.CustomJSONObject;

@Provider
public class SQLExceptionMapper implements ExceptionMapper<SQLException>{

	@Override
	public Response toResponse(SQLException ex) {
		CustomJSONObject response = new CustomJSONObject().respond(CustomResponse.INTERNAL_SERVER_ERROR);
		// TODO log the trace
		return Response.status(CustomResponse.INTERNAL_SERVER_ERROR.getCode()).entity(response).type(MediaType.APPLICATION_JSON).build();
	}

}

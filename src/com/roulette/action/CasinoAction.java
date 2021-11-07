package com.roulette.action;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;

import com.roulette.dao.CasinoDAO;
import com.roulette.dao.DealerDAO;
import com.roulette.exception.InvalidRequestParamException;
import com.roulette.util.CasinoUtil;
import com.roulette.util.Constants;
import com.roulette.util.CustomJSONObject;
import com.roulette.util.Constants.CustomResponse;

@Path("/casino")
public class CasinoAction {
	
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject register(@FormParam("name") String name, @FormParam("email_id") String email_id) throws SQLException, InvalidRequestParamException {
		if(name == null || name.isEmpty()) {
			throw new InvalidRequestParamException("name");
		}else if(email_id == null || !Constants.isValid(email_id)) {
			throw new InvalidRequestParamException("email_id");
		}
		CustomJSONObject response = new CustomJSONObject();
		CasinoDAO casionDao = new CasinoDAO(name, email_id);
		if(CasinoUtil.hasCasino(email_id)) {
			response.respond(CustomResponse.RESOURCE_EXISTS).put("email_id", email_id);
		}else {
			CasinoUtil.addCasino(name, email_id);
			casionDao.setId(CasinoUtil.getCasinoID(email_id));
			response.put("casino", casionDao);
		}
		return response;
	}
	
	@POST
	@Path("/{casino_id}/recharge")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject recharge(@PathParam("casino_id") Long casino_id, @FormParam("amount") Long amount) throws SQLException, InvalidRequestParamException {
		if(casino_id == null) {
			throw new InvalidRequestParamException("casino_id");
		}else if(amount == null || amount < 0 ) {
			throw new InvalidRequestParamException("amount");
		}
		CustomJSONObject response = new CustomJSONObject().put("casino_id", casino_id);
		if(CasinoUtil.isValidCasino(casino_id)) {
			CasinoUtil.updateBalance(casino_id, amount);
			response.put("balance", CasinoUtil.getBalanceAmount(casino_id));
		}else {
			response.respond(CustomResponse.INVALID_RESOURCE);
		}
		return response;

	}
	
	@POST
	@Path("/{casino_id}/dealer")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAllDealers(@PathParam("casino_id") Long casino_id) throws SQLException, InvalidRequestParamException {
		if(casino_id == null) {
			throw new InvalidRequestParamException("casino_id");
		}
		CustomJSONObject response = new CustomJSONObject().put("casino_id", casino_id);
		if(CasinoUtil.isValidCasino(casino_id)) {
			response.put("dealer", CasinoUtil.getAllDealers(casino_id));
		}else {
			response.respond(CustomResponse.INVALID_RESOURCE);
		}
		return response;
	}

}

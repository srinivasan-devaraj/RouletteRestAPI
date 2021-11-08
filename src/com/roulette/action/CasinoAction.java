package com.roulette.action;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.roulette.entity.Casino;
import com.roulette.exception.InvalidRequestParamException;
import com.roulette.util.CasinoUtil;
import com.roulette.util.Constants;
import com.roulette.util.CustomJSONObject;
import com.roulette.util.Constants.CustomResponse;

/**
 * Class used to handle all the /casino request
 * @author srini
 */
@Path("/casino")
public class CasinoAction {
	/**
	 * Method used to register the user into the database after validating the request params
	 * @param name
	 * @param email_id
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject register(@FormParam("name") String name, @FormParam("email_id") String email_id) throws InvalidRequestParamException {
		if(name == null || name.isEmpty()) {
			throw new InvalidRequestParamException("name");
		}else if(email_id == null || !Constants.isValid(email_id)) {
			throw new InvalidRequestParamException("email_id");
		}
		CustomJSONObject response = new CustomJSONObject();
		Casino casionDao = new Casino();
		casionDao.setName(name);
		casionDao.setEmail_id(email_id);
		if(CasinoUtil.hasCasino(email_id)) {
			response.respond(CustomResponse.RESOURCE_EXISTS).put("email_id", email_id);
		}else {
			CasinoUtil.addCasino(casionDao);
			casionDao.setId(CasinoUtil.getCasinoID(email_id));
			response.put("casino", casionDao);
		}
		return response;
	}
	
	/**
	 * Method used to recharge/load amount into the user wallet
	 * @param casino_id
	 * @param amount
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@POST
	@Path("/{casino_id}/recharge")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject recharge(@PathParam("casino_id") Long casino_id, @FormParam("amount") Long amount) throws InvalidRequestParamException {
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
	/**
	 * Method used to list all the dealers registered in the corresponding casino
	 * @param casino_id
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@POST
	@Path("/{casino_id}/dealer")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAllDealers(@PathParam("casino_id") Long casino_id) throws InvalidRequestParamException {
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

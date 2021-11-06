package com.roulette.action;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;

import com.roulette.dao.CasinoDAO;
import com.roulette.dao.DealerDAO;
import com.roulette.exception.InvalidRequestParamException;
import com.roulette.util.CasinoUtil;
import com.roulette.util.Constants;

@Path("/casino")
public class CasinoAction {
	
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public CasinoDAO register(@FormParam("name") String name, @FormParam("email_id") String email_id) throws SQLException, InvalidRequestParamException {
		if(name == null || name.isEmpty()) {
			throw new InvalidRequestParamException("name");
		}else if(email_id == null || !Constants.isValid(email_id)) {
			throw new InvalidRequestParamException("email_id");
		}
		CasinoDAO casionDao = new CasinoDAO(name, email_id);
		casionDao.setId(CasinoUtil.addCasino(name, email_id));
		return casionDao;
	}
	
	@POST
	@Path("/recharge")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject recharge(@FormParam("casino_id") Long id, @FormParam("amount") Long amount) throws SQLException, InvalidRequestParamException {
		if(id == null) {
			throw new InvalidRequestParamException("casino_id");
		}else if(amount == null) {
			throw new InvalidRequestParamException("amount");
		}
		JSONObject response = new JSONObject();
		response.put(Constants.RESPONSE_CODE, HttpStatus.SC_OK);
		response.put(Constants.RESPONSE_MESSAGE, Constants.RESPONSE_SUCCESS);
		CasinoUtil.updateBalance(id, amount);
		response.put("balance", CasinoUtil.getBalanceAmount(id));
		response.put("casino_id", id);
		return response;
	}
	
	@POST
	@Path("/dealer")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DealerDAO> getAllDealers(@FormParam("casino_id") Long casino_id) throws SQLException, InvalidRequestParamException {
		if(casino_id == null) {
			throw new InvalidRequestParamException("casino_id");
		}
		return CasinoUtil.getAllDealers(casino_id);
	}

}

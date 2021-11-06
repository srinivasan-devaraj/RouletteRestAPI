package com.roulette.action;

import java.sql.SQLException;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.roulette.dao.DealerDAO;
import com.roulette.exception.InvalidRequestParamException;
import com.roulette.util.CasinoUtil;
import com.roulette.util.Constants;
import com.roulette.util.DealerUtil;
import com.roulette.util.GameUtil;

@Path("/dealer")
public class DealerAction {
	
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public DealerDAO register(@FormParam("name") String name, @FormParam("email_id") String email_id,  @FormParam("casino_id") Long casino_id) throws SQLException, InvalidRequestParamException {
		if(name == null || name.isEmpty()) {
			throw new InvalidRequestParamException("name");
		}else if(email_id == null || !Constants.isValid(email_id)) {
			throw new InvalidRequestParamException("email_id");
		}else if(casino_id == null) {
			throw new InvalidRequestParamException("casino_id");
		}
		DealerDAO dealerDao = new DealerDAO(name, email_id, casino_id);
		dealerDao.setId(DealerUtil.addDealer(name, email_id, casino_id));
		return dealerDao;
	}
	// TODO PUT request
	@POST
	@Path("/start")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject startGame(@FormParam("dealer_id") Long dealerId) throws SQLException, InvalidRequestParamException {
		if(dealerId == null) {
			throw new InvalidRequestParamException("dealer_id");
		}
		JSONObject response = GameUtil.startGame(dealerId);
		return response;
	}
	
	@POST
	@Path("/close")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject closeGame(@FormParam("dealer_id") Long dealerId, @FormParam("game_id") Long gameId) throws SQLException, InvalidRequestParamException {
		if(dealerId == null) {
			throw new InvalidRequestParamException("dealer_id");
		}else if(gameId == null) {
			throw new InvalidRequestParamException("game_id");
		}
		JSONObject response = new JSONObject();
		if(GameUtil.isDealerOwnThisGame(dealerId, gameId)) {
			GameUtil.updateGame(gameId, Constants.GAME_CLOSE);
			response.put("game_id", gameId);
			response.put("status", Constants.GAME_CLOSE);
		}else {
			response.put("message", "No suitable game for this dealer");
		}
		return response;
	}
	
	@POST
	@Path("/throw")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject throwBall(@FormParam("dealer_id") Long dealerId, @FormParam("game_id") Long gameId) throws SQLException, InvalidRequestParamException {
		if(dealerId == null) {
			throw new InvalidRequestParamException("dealer_id");
		}else if(gameId == null) {
			throw new InvalidRequestParamException("game_id");
		}
		Long casinoId = CasinoUtil.getCasinoID(gameId);
		JSONObject response = GameUtil.throwBall(dealerId, casinoId, gameId);
		return response;
	}
}

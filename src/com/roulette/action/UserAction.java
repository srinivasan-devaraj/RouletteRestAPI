package com.roulette.action;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.roulette.dao.UserDAO;
import com.roulette.exception.InvalidRequestParamException;
import com.roulette.util.CasinoUtil;
import com.roulette.util.Constants;
import com.roulette.util.GameUtil;
import com.roulette.util.UserUtil;

import java.sql.SQLException;


@Path("/user")
public class UserAction {
	
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public UserDAO register(@FormParam("name") String name, @FormParam("email_id") String email_id) throws SQLException, InvalidRequestParamException {
		if(name == null || name.isEmpty()) {
			throw new InvalidRequestParamException("name");
		}else if(email_id == null || !Constants.isValid(email_id)) {
			throw new InvalidRequestParamException("email_id");
		}
		UserDAO userdao = new UserDAO(name, email_id);
		userdao.setId(UserUtil.addUser(name, email_id));
		return userdao;
	}
	@POST
	@Path("/bet")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject betOnGame(@FormParam("user_id") Long userId, @FormParam("game_id") Long gameId, @FormParam("bet_amount") Long amount, @FormParam("bet_number") int number) throws SQLException, InvalidRequestParamException {
		if(userId == null) {
			throw new InvalidRequestParamException("user_id");
		}else if(gameId == null) {
			throw new InvalidRequestParamException("game_id");
		}else if(amount == null) {
			throw new InvalidRequestParamException("bet_amount");
		}else if(number < 1 || number > 36 ) {
			throw new InvalidRequestParamException("bet_number");
		}
		Long casinoId = CasinoUtil.getCasinoID(gameId);
		JSONObject response = UserUtil.betOnGame(userId, gameId, casinoId, amount, number);
		return response;
	}
	
	@GET
	@Path("/games")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray listGames() throws SQLException {
		JSONArray response = GameUtil.getAllGames(null);
		return response;
	}
	
	@POST
	@Path("/games")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray listGames(@FormParam("casino_id") Long casinoId) throws SQLException, InvalidRequestParamException {
		if(casinoId == null) {
			throw new InvalidRequestParamException("casino_id");
		}
		JSONArray response = GameUtil.getAllGames(casinoId);
		return response;
	}
	
	@POST
	@Path("/cashout")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject cashOut(@FormParam("user_id") Long userId, @FormParam("amount") Long amount) throws SQLException, InvalidRequestParamException {
		if(userId == null) {
			throw new InvalidRequestParamException("user_id");
		}else if(amount == null) {
			throw new InvalidRequestParamException("amount");
		}
		UserUtil.updateBalance(userId, amount, false);
		JSONObject response = new JSONObject();
		response.put("amount", amount);
		response.put("balance", UserUtil.getBalanceAmount(userId));
		response.put("message", "Withdrawn successfully");
		return response;
	}
	
	@POST
	@Path("/recharge")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject recharge(@FormParam("user_id") Long userId, @FormParam("amount") Long amount) throws SQLException, InvalidRequestParamException {
		if(userId == null) {
			throw new InvalidRequestParamException("user_id");
		}else if(amount == null) {
			throw new InvalidRequestParamException("amount");
		}
		UserUtil.updateBalance(userId, amount, true);
		JSONObject response = new JSONObject();
		response.put("amount", amount);
		response.put("balance", UserUtil.getBalanceAmount(userId));
		response.put("message", "Amount loaded successfully");
		return response;
	}
}

package com.roulette.action;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.roulette.entity.User;
import com.roulette.exception.InvalidRequestParamException;
import com.roulette.util.CasinoUtil;
import com.roulette.util.Constants;
import com.roulette.util.Constants.CustomResponse;
import com.roulette.util.CustomJSONObject;
import com.roulette.util.GameUtil;
import com.roulette.util.UserUtil;

/**
 * Class used to handle all the /user request
 * @author srini
 */
@Path("/user")
public class UserAction {

	/**
	 * Method used to register the user into the application/server
	 * @param name
	 * @param email_id
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject register(@FormParam("name") String name, @FormParam("email_id") String email_id) throws InvalidRequestParamException {
		if (name == null || name.isEmpty()) {
			throw new InvalidRequestParamException("name");
		} else if (email_id == null || !Constants.isValid(email_id)) {
			throw new InvalidRequestParamException("email_id");
		}
		CustomJSONObject response = new CustomJSONObject();
		if (UserUtil.hasUser(email_id)) {
			response.respond(CustomResponse.RESOURCE_EXISTS).put("email_id", email_id);
		} else {
			User userdao = new User();
			userdao.setName(name);
			userdao.setEmail_id(email_id);
			UserUtil.addUser(userdao);
			userdao.setId(UserUtil.getUserID(email_id));
			response.put("user", userdao);
		}
		return response;
	}
	
	/**
	 * Method used to bet the specific amount by guessing the number by the user
	 * @param userId
	 * @param gameId
	 * @param amount
	 * @param number
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@POST
	@Path("/{user_id}/bet")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject betOnGame(@PathParam("user_id") Long userId, @FormParam("game_id") Long gameId, @FormParam("bet_amount") Long amount, @FormParam("bet_number") int number)
			throws InvalidRequestParamException {
		if (userId == null || !UserUtil.isValidUser(userId)) {
			throw new InvalidRequestParamException("user_id");
		} else if (gameId == null) {
			throw new InvalidRequestParamException("game_id");
		} else if (amount == null) {
			throw new InvalidRequestParamException("bet_amount");
		} else if (number < 1 || number > 36) {
			throw new InvalidRequestParamException("bet_number");
		}
		CustomJSONObject response = new CustomJSONObject();
		if (!UserUtil.isValidUser(userId)) {
			return response.respond(CustomResponse.UNAUTH_ACCESS).put("user_id", userId);
		}
		Long casinoId = CasinoUtil.getCasinoID(gameId);
		if (casinoId.equals(UserUtil.getCurrentCasinoId(userId))) {
			response = UserUtil.betOnGame(userId, gameId, casinoId, amount, number);
		} else {
			response.respond(CustomResponse.USER_NOT_IN_CASINO).put("casino_id", casinoId);
		}
		return response;
	}
	
	/**
	 * Method used to list all the available games in the corresponding user entered casino
	 * @param userId
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@GET
	@Path("/{user_id}/games")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject listGames(@PathParam("user_id") Long userId) throws InvalidRequestParamException {
		if (userId == null || !UserUtil.isValidUser(userId)) {
			throw new InvalidRequestParamException("user_id");
		}
		Long casinoId = UserUtil.getCurrentCasinoId(userId);
		CustomJSONObject response = new CustomJSONObject();
		if (casinoId.equals(0L)) {
			response.respond(CustomResponse.USER_NOT_IN_ANY_CASINO);
		} else {
			response.put("games", GameUtil.getAllGames(casinoId));
		}
		return response;
	}
	
	/**
	 * Method used to cashout the amount from the user account wallet
	 * @param userId
	 * @param amount
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@POST
	@Path("/{user_id}/cashout")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject cashOut(@PathParam("user_id") Long userId, @FormParam("amount") Long amount) throws InvalidRequestParamException {
		if (userId == null || !UserUtil.isValidUser(userId)) {
			throw new InvalidRequestParamException("user_id");
		} else if (amount == null || amount < 0) {
			throw new InvalidRequestParamException("amount");
		}
		CustomJSONObject response = new CustomJSONObject().put("amount", amount)
				.putAllObj(UserUtil.updateBalance(userId, amount, false))
				.put("balance", UserUtil.getBalanceAmount(userId));
		return response;
	}
	
	/**
	 * Method used to recharge the specific amount into the user wallet
	 * @param userId
	 * @param amount
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@POST
	@Path("/{user_id}/recharge")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject recharge(@PathParam("user_id") Long userId, @FormParam("amount") Long amount) throws InvalidRequestParamException {
		if (userId == null || !UserUtil.isValidUser(userId)) {
			throw new InvalidRequestParamException("user_id");
		} else if (amount == null) {
			throw new InvalidRequestParamException("amount");
		}
		UserUtil.updateBalance(userId, amount, true);
		CustomJSONObject response = new CustomJSONObject().put("amount", amount)
				.put("balance", UserUtil.getBalanceAmount(userId)).message("Amount loaded successfully");
		return response;
	}
	
	/**
	 * Method used to get all the casino's available in the application
	 * @param userId
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@GET
	@Path("/{user_id}/casino")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject casinoDetails(@PathParam("user_id") Long userId) throws InvalidRequestParamException {
		if (userId == null || !UserUtil.isValidUser(userId)) {
			throw new InvalidRequestParamException("user_id");
		}
		CustomJSONObject response = new CustomJSONObject().put("casino", CasinoUtil.getAllCasino());
		return response;
	}
	
	/**
	 * Method used by the user to the enter the specified casino
	 * @param userId
	 * @param casinoId
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@PUT
	@Path("/{user_id}/casino")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject enterCasino(@PathParam("user_id") Long userId, @FormParam("casino_id") Long casinoId) throws InvalidRequestParamException {
		if (userId == null || !UserUtil.isValidUser(userId)) {
			throw new InvalidRequestParamException("user_id");
		} else if (casinoId == null) {
			throw new InvalidRequestParamException("casino_id");
		}
		CustomJSONObject response = new CustomJSONObject();
		if (!CasinoUtil.isValidCasino(casinoId)) {
			response.respond(CustomResponse.INVALID_RESOURCE).put("casino_id", casinoId);
		} else {
			UserUtil.updateCasino(userId, casinoId);
			response.put("user_id", userId).put("casino_id", casinoId);
		}
		return response;
	}
}

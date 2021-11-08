package com.roulette.action;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.roulette.exception.InvalidRequestParamException;
import com.roulette.util.CasinoUtil;
import com.roulette.util.Constants;
import com.roulette.util.Constants.CustomResponse;
import com.roulette.util.CustomJSONObject;
import com.roulette.util.DealerUtil;
import com.roulette.util.GameUtil;

/**
 * Class used to handle all the /dealer request
 * @author srini
 */
@Path("/dealer")
public class DealerAction {
	
	/**
	 * Method used to register the dealer in the corresponding casino
	 * @param name
	 * @param email_id
	 * @param casino_id
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject register(@FormParam("name") String name, @FormParam("email_id") String email_id, @FormParam("casino_id") Long casino_id) throws InvalidRequestParamException {
		if (name == null || name.isEmpty()) {
			throw new InvalidRequestParamException("name");
		} else if (email_id == null || !Constants.isValid(email_id)) {
			throw new InvalidRequestParamException("email_id");
		} else if (casino_id == null) {
			throw new InvalidRequestParamException("casino_id");
		}
		CustomJSONObject response = new CustomJSONObject();
		if (!CasinoUtil.isValidCasino(casino_id)) {
			return response.respond(CustomResponse.INVALID_RESOURCE).put("casino_id", casino_id);
		}
		if (DealerUtil.hasDealer(casino_id, email_id)) {
			response.respond(CustomResponse.RESOURCE_EXISTS).put("email_id", email_id).put("casino_id", casino_id);
		} else {
			DealerUtil.addDealer(name, email_id, casino_id);
			response.put("dealer", new CustomJSONObject(false)
									.put("id", DealerUtil.getDealerID(casino_id, email_id))
									.put("name", name)
									.put("email_id", email_id)
									.put("casino_id", casino_id)
					);
		}
		return response;
	}
	
	/**
	 * Method used to start/open the game by the dealer
	 * @param dealerId
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@PUT
	@Path("/{dealer_id}/start")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject startGame(@PathParam("dealer_id") Long dealerId) throws InvalidRequestParamException {
		if (dealerId == null) {
			throw new InvalidRequestParamException("dealer_id");
		}
		CustomJSONObject response = new CustomJSONObject();
		if (DealerUtil.isValidDealer(dealerId)) {
			response.put("game", GameUtil.startGame(dealerId));
		} else {
			response.respond(CustomResponse.INVALID_RESOURCE).put("dealer_id", dealerId);
		}
		return response;
	}
	
	/**
	 * Method used to close/end the game by the dealer
	 * @param dealerId
	 * @param gameId
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@POST
	@Path("/{dealer_id}/close")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject closeGame(@PathParam("dealer_id") Long dealerId, @FormParam("game_id") Long gameId)
			throws InvalidRequestParamException {
		if (dealerId == null) {
			throw new InvalidRequestParamException("dealer_id");
		} else if (gameId == null) {
			throw new InvalidRequestParamException("game_id");
		}
		CustomJSONObject response = new CustomJSONObject();
		if (!DealerUtil.isValidDealer(dealerId)) {
			return response.respond(CustomResponse.INVALID_RESOURCE).put("dealer_id", dealerId);
		}
		if (GameUtil.isDealerOwnThisGame(dealerId, gameId)) {
			GameUtil.updateGame(gameId, Constants.GAME_CLOSE);
			response.put("game_id", gameId).put("game_status", Constants.GAME_CLOSE);
		} else {
			response.respond(CustomResponse.UNAUTH_ACCESS).put("game_id", gameId);
		}
		return response;
	}
	
	/**
	 * Method used to throw the ball by the dealer
	 * @param dealerId
	 * @param gameId
	 * @return
	 * @throws InvalidRequestParamException
	 */
	@POST
	@Path("/{dealer_id}/throw")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject throwBall(@PathParam("dealer_id") Long dealerId, @FormParam("game_id") Long gameId)
			throws InvalidRequestParamException {
		if (dealerId == null) {
			throw new InvalidRequestParamException("dealer_id");
		} else if (gameId == null) {
			throw new InvalidRequestParamException("game_id");
		}
		CustomJSONObject response = new CustomJSONObject();
		if (!DealerUtil.isValidDealer(dealerId)) {
			return response.respond(CustomResponse.INVALID_RESOURCE).put("dealer_id", dealerId);
		}
		if (!GameUtil.isDealerOwnThisGame(dealerId, gameId)) {
			return response.respond(CustomResponse.UNAUTH_ACCESS).put("game_id", gameId);
		}
		Long casinoId = CasinoUtil.getCasinoID(gameId);
		response.putAll(GameUtil.throwBall(dealerId, casinoId, gameId));
		return response;
	}
}

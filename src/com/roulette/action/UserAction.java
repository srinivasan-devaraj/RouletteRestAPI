package com.roulette.action;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.roulette.dao.UserDAO;
import com.roulette.util.DBUtil;

import java.sql.SQLException;


@Path("/user")
public class UserAction {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String AllUsers() {
		//Connection conn = ConnectionPool.getConnection(); 
		String respose = "<hello>Welcome to Jersey :)</hello";
		return respose;
	}
	
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public UserDAO register(@FormParam("name") String name, @FormParam("email_id") String email_id) throws SQLException {
		UserDAO userdao = new UserDAO(name, email_id);
		userdao.setId(DBUtil.addUser(name, email_id));
		return userdao;
	}
}

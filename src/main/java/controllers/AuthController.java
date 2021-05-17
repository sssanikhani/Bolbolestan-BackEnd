package controllers;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import controllers.responses.Responses;
import models.database.DataBase;
import models.statics.Exceptions;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@PostMapping("/login")
	public HashMap<String, Object> login(
		@RequestBody HashMap<String, Object> requestBody,
		HttpServletResponse response
	) {
		if (!(requestBody.get("id") instanceof String)) {
			response.setStatus(400);
			return Responses.BadRequest;
		}
		if (!(requestBody.get("password") instanceof String)) {
			response.setStatus(400);
			return Responses.BadRequest;
		}

		String id = (String) requestBody.get("id");
		String password = (String) requestBody.get("password");

		if (id == null || password == null) {
			response.setStatus(400);
			return Responses.BadRequest;
		}

		try {
			DataBase.AuthManager.login(id);
		} catch (Exceptions.StudentNotFound e) {
            response.setStatus(401);
			return Responses.UnAuthorized;
		}
		return Responses.OK;
	}

	@PostMapping("/logout")
	public HashMap<String, Object> logout() {
		DataBase.AuthManager.logout();
		return Responses.OK;
	}
}

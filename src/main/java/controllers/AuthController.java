package controllers;

import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import controllers.responses.Responses;
import models.logic.DataBase;
import models.statics.Exceptions;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@PostMapping("/login")
	public HashMap<String, Object> login(
		@RequestBody HashMap<String, Object> requestBody,
		HttpServletResponse response
	) {
        String id = (String) requestBody.get("id");
        try {
            DataBase.AuthManager.login(id);
        } catch(Exceptions.StudentNotFound e) {
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

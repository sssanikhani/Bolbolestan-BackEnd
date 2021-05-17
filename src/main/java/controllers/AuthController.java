package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import controllers.responses.Responses;
import models.database.DataBase;
import models.database.repositories.StudentRepository;
import models.entities.Student;
import models.statics.Constants;
import models.statics.Exceptions;
import models.utils.Utils;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@PostMapping("/signup")
	public HashMap<String, Object> signup(
		@RequestBody HashMap<String, Object> requestBody,
		HttpServletResponse response
	) {
		String[] requiredFields = {
			"id", 
			"email", 
			"password",
			"name",
			"secondName",
			"birthDate",
			"faculty",
			"field",
			"level",
			"status",
			"img"
		};
		for (String key : requiredFields) {
			if (!(requestBody.get(key) instanceof String)) {
				response.setStatus(400);
				return Responses.BadRequest;
			}
		}

		String id = (String) requestBody.get("id");
		String email = (String) requestBody.get("email");
		boolean existId = StudentRepository.existsId(id);
		boolean existEmail = StudentRepository.existsEmail(email);
		if (existId || existEmail) {
			response.setStatus(409);
			return Responses.AlreadyExists;
		}

		Student newStudent = new Student();
		newStudent.setId(id);
		newStudent.setEmail(email);
		newStudent.setPassword((String) requestBody.get("password"));
		newStudent.setName((String) requestBody.get("name"));
		newStudent.setSecondName((String) requestBody.get("secondName"));
		newStudent.setBirthDate((String) requestBody.get("birthDate"));
		newStudent.setFaculty((String) requestBody.get("faculty"));
		newStudent.setField((String) requestBody.get("field"));
		newStudent.setLevel((String) requestBody.get("level"));
		newStudent.setStatus((String) requestBody.get("status"));
		newStudent.setImg((String) requestBody.get("img"));
		
		ArrayList<Student> list = new ArrayList<>();
		list.add(newStudent);
		StudentRepository.bulkUpdate(list);

		String jwtToken = Utils.createJWT(id, Constants.ONE_DAY);

		HashMap<String, Object> res = new HashMap<>();
		res.put("access", jwtToken);
		response.setStatus(201);
		return res;
	}

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

}

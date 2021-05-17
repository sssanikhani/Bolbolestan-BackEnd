package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import controllers.responses.Responses;
import models.database.DataBase;
import models.database.repositories.OfferingRepository;
import models.entities.Offering;
import models.serializers.OfferingSerializer;

@RestController
@RequestMapping("/offerings")
public class OfferingsController {

	@GetMapping("")
	public Object getAll(HttpServletResponse response) {
		if (!DataBase.AuthManager.isLoggedIn()) {
			response.setStatus(401);
			return Responses.UnAuthorized;
		}
		ArrayList<Offering> list = OfferingRepository.getAll();
		ArrayList<HashMap<String, Object>> result = OfferingSerializer.serializeList(list);
		return result;
	}

	@GetMapping("/search")
	public Object search(@RequestParam("q") String query, HttpServletResponse response) {
		if (!DataBase.AuthManager.isLoggedIn()) {
			response.setStatus(401);
			return Responses.UnAuthorized;
		}
		ArrayList<Offering> filtered = OfferingRepository.search(query);
		ArrayList<HashMap<String, Object>> result = OfferingSerializer.serializeList(filtered);
		return result;
	}
}

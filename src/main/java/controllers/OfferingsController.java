package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import models.database.repositories.OfferingRepository;
import models.entities.Offering;
import models.serializers.OfferingSerializer;

@RestController
@RequestMapping("/offerings")
public class OfferingsController {

	@GetMapping("")
	public Object getAll(HttpServletRequest request, HttpServletResponse response) {
		ArrayList<Offering> list = OfferingRepository.getAll();
		ArrayList<HashMap<String, Object>> result = OfferingSerializer.serializeList(list);
		return result;
	}

	@GetMapping("/search")
	public Object search(
		@RequestParam("q") String query,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		ArrayList<Offering> filtered = OfferingRepository.search(query);
		ArrayList<HashMap<String, Object>> result = OfferingSerializer.serializeList(filtered);
		return result;
	}
}

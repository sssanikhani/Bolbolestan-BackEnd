package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import controllers.responses.Responses;
import models.entities.Offering;
import models.entities.Student;
import models.logic.DataBase;
import models.serializers.OfferingSerializer;

@RestController
@RequestMapping("/student/offerings")
public class StudentOfferingsController {

	@GetMapping("")
	public Object getStudentOfferings(HttpServletResponse response) {
		if (!DataBase.AuthManager.isLoggedIn()) {
			response.setStatus(401);
			return Responses.UnAuthorized;
		}

        ArrayList<HashMap<String, Object>> result = new ArrayList<>();

		Student s = DataBase.AuthManager.getLoggedInUser();
		ArrayList<Offering> oList = s.getChosenOfferings();
		for (Offering o : oList) {
            HashMap<String, Object> studentOffering = new HashMap<String, Object>() {
                {
                    put("status", s.getOfferingStatus(o));
                    put("offering", OfferingSerializer.serialize(o));
                }
            };
            result.add(studentOffering);
        }

        return result;
	}
}

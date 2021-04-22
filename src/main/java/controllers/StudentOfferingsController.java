package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import controllers.responses.Responses;
import models.entities.Offering;
import models.entities.Student;
import models.logic.DataBase;
import models.serializers.OfferingSerializer;
import models.statics.Constants;
import models.statics.Exceptions;

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

	@PostMapping("/{code}/{classCode}")
	public HashMap<String, Object> addOffering(
		@PathVariable("code") String code,
		@PathVariable("classCode") String classCode,
		HttpServletResponse response
	) {
		if (!DataBase.AuthManager.isLoggedIn()) {
			response.setStatus(401);
			return Responses.UnAuthorized;
		}

		Offering offering;
		try {
			offering = DataBase.OfferingManager.get(code, classCode);
		} catch (Exceptions.offeringNotFound e) {
			response.setStatus(404);
			return Responses.OfferingNotFound;
		}

		Student student = DataBase.AuthManager.getLoggedInUser();

		boolean hasPassedPrerequisites;
		try {
			hasPassedPrerequisites = student.hasPassedPrerequisites(code);
		} catch (Exceptions.offeringNotFound e) {
			response.setStatus(500);
			return Responses.InternalServerError;
		}

		if (!hasPassedPrerequisites) {
			response.setStatus(403);
			return Responses.NotPassedPrerequisites;
		}

		if (student.hasPassed(offering.getCourse().getCode())) {
			response.setStatus(403);
			return Responses.CoursePassedBefore;
		}

		ArrayList<Offering> chosenOfferings = student.getChosenOfferings();
		for (Offering o : chosenOfferings) {
			boolean hasClassTimeCollision = offering.hasOfferingTimeCollision(o);
			if (hasClassTimeCollision) {
				response.setStatus(403);
				return Responses.CourseTimeCollision;
			}
			boolean hasExamTimeCollision = offering.hasExamTimeCollision(o);
			if (hasExamTimeCollision) {
				response.setStatus(403);
				return Responses.ExamTimeCollision;
			}
		}

		student.addOfferingToList(offering);
		return Responses.OK;
	}

	@DeleteMapping("/{code}/{classCode}")
	public HashMap<String, Object> removeOffering(
		@PathVariable("code") String code,
		@PathVariable("classCode") String classCode,
		HttpServletResponse response
	) {
		if (!DataBase.AuthManager.isLoggedIn()) {
			response.setStatus(401);
			return Responses.UnAuthorized;
		}

		Student student = DataBase.AuthManager.getLoggedInUser();
		Offering offering;
		try {
			offering = DataBase.OfferingManager.get(code, classCode);
		} catch (Exceptions.offeringNotFound e) {
			response.setStatus(403);
			return Responses.NotChosenOffering;
		}

		try {
			student.removeOfferingFromList(offering.getCourse().getCode());
		} catch (Exceptions.offeringNotFound e) {
			response.setStatus(403);
			return Responses.NotChosenOffering;
		}

		return Responses.OK;
	}

    @PostMapping("/submit")
    public HashMap<String, Object> submitChosenOfferings(HttpServletResponse response) {
        if (!DataBase.AuthManager.isLoggedIn()) {
            response.setStatus(401);
            return Responses.UnAuthorized;
        }
        
        Student student = DataBase.AuthManager.getLoggedInUser();

        int numUnits = student.getNumberChosenUnits();
		if (numUnits < Constants.MIN_ALLOWED_UNITS) {
			response.setStatus(403);
            return Responses.MinUnits;
		}
        if (numUnits > Constants.MAX_ALLOWED_UNITS) {
            response.setStatus(403);
            return Responses.MaxUnits;
        }

		student.finalizeOfferings();
        return Responses.OK;
    }
}

package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import controllers.responses.Responses;
import models.database.repositories.OfferingRepository;
import models.database.repositories.StudentRepository;
import models.entities.Offering;
import models.entities.Student;
import models.serializers.OfferingSerializer;
import models.statics.Constants;
import models.statics.Exceptions;

@RestController
@RequestMapping("/student/offerings")
public class StudentOfferingsController {

	@GetMapping("")
	public Object getStudentOfferings(
		HttpServletRequest request,
		HttpServletResponse response
	) {
		Student s = (Student) request.getAttribute("student");
		
		HashMap<String, Object> result = new HashMap<>();

		result.put("chosenUnits", s.getNumberChosenUnits());

		ArrayList<HashMap<String, Object>> offeringsData = new ArrayList<>();
		result.put("offerings", offeringsData);

		ArrayList<Offering> oList = s.getChosenOfferings();
		for (Offering o : oList) {
			HashMap<String, Object> studentOffering = new HashMap<String, Object>() {
				{
					put("status", s.getOfferingStatus(o));
					put("offering", OfferingSerializer.serialize(o));
				}
			};
			offeringsData.add(studentOffering);
		}

		return result;
	}

	@PostMapping("")
	public HashMap<String, Object> addOffering(
		@RequestBody HashMap<String, Object> requestBody,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		Student student = (Student) request.getAttribute("student");

		if (!(requestBody.get("code") instanceof String)) {
			response.setStatus(400);
			return Responses.BadRequest;
		}
		if (!(requestBody.get("classCode") instanceof String)) {
			response.setStatus(400);
			return Responses.BadRequest;
		}

		String code = (String) requestBody.get("code");
		String classCode = (String) requestBody.get("classCode");

		if (code == null || classCode == null) {
			response.setStatus(400);
			return Responses.BadRequest;
		}

		Offering offering;
		try {
			offering = OfferingRepository.get(code, classCode);
		} catch (Exceptions.offeringNotFound e) {
			response.setStatus(404);
			return Responses.OfferingNotFound;
		}

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
		StudentRepository.updateOfferings(student);
		return Responses.OK;
	}

	@DeleteMapping("")
	public HashMap<String, Object> removeOffering(
		@RequestBody HashMap<String, Object> requestBody,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		Student student = (Student) request.getAttribute("student");

		if (!(requestBody.get("code") instanceof String)) {
			response.setStatus(400);
			return Responses.BadRequest;
		}
		if (!(requestBody.get("classCode") instanceof String)) {
			response.setStatus(400);
			return Responses.BadRequest;
		}

		String code = (String) requestBody.get("code");
		String classCode = (String) requestBody.get("classCode");

		if (code == null || classCode == null) {
			response.setStatus(400);
			return Responses.BadRequest;
		}

		boolean exists = student.existsOffering(code, classCode);
		if (!exists) {
			response.setStatus(403);
			return Responses.NotChosenOffering;
		}
		Offering o1 = student._getChosenOffering(code, classCode);
		Offering o2 = student._getLastOffering(code, classCode);

		try {
			if (o1 != null) {
				student.removeOfferingFromList(o1.getCourse().getCode());
				OfferingRepository.updateStudents(o1);
			} else {
				student.removeOfferingFromList(o2.getCourse().getCode());
				OfferingRepository.updateStudents(o2);
			}
			StudentRepository.updateOfferings(student);
		} catch (Exceptions.offeringNotFound e) {
			response.setStatus(403);
			return Responses.NotChosenOffering;
		}

		return Responses.OK;
	}

	@PostMapping("/submit")
	public HashMap<String, Object> submitChosenOfferings(
		HttpServletRequest request,
		HttpServletResponse response
	) {
		Student student = (Student) request.getAttribute("student");

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
		StudentRepository.updateOfferings(student);
		return Responses.OK;
	}

	@PostMapping("/reset")
	public HashMap<String, Object> resetChosenOfferings(
		HttpServletRequest request,
		HttpServletResponse response
	) {
		Student student = (Student) request.getAttribute("student");
		student.resetPlan();
		StudentRepository.updateOfferings(student);
		return Responses.OK;
	}
}

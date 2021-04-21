package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import controllers.responses.Responses;
import models.entities.Student;
import models.entities.Term;
import models.logic.DataBase;
import models.serializers.StudentSerializer;
import models.serializers.TermReportSerializer;

@RestController
@RequestMapping("/student")
public class StudentController {
    
    @GetMapping("")
    public HashMap<String, Object> getStudentInfo(HttpServletResponse response) {
        if (!DataBase.AuthManager.isLoggedIn()) {
            response.setStatus(401);
            return Responses.UnAuthorized;
        }
        
        Student s = DataBase.AuthManager.getLoggedInUser();
        HashMap<String, Object> result = StudentSerializer.serialize(s);
        return result;
    }

    @GetMapping("/report-card")
    public Object getStudentReportCard(HttpServletResponse response) {
        if (!DataBase.AuthManager.isLoggedIn()) {
            response.setStatus(401);
            return Responses.UnAuthorized;
        }
        
        Student s = DataBase.AuthManager.getLoggedInUser();
        ArrayList<Term> tList = s.getTermsReport();
        ArrayList<HashMap<String, Object>> result = TermReportSerializer.serializeList(tList);
        return result;
    }
}

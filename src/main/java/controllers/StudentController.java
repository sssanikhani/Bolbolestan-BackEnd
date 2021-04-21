package controllers;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import controllers.responses.Responses;
import models.entities.Student;
import models.logic.DataBase;
import models.serializers.StudentSerializer;

@RestController
@RequestMapping("/student")
public class StudentController {
    
    @GetMapping("")
    public HashMap<String, Object> getStudentInfo(HttpServletResponse response) {
        if (!DataBase.AuthManager.isLoggedIn())
            return Responses.UnAuthorized;
        
        Student s = DataBase.AuthManager.getLoggedInUser();
        HashMap<String, Object> result = StudentSerializer.serialize(s);
        return result;
    }
}

package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import models.entities.Offering;
import models.logic.DataBase;
import models.serializers.OfferingSerializer;
import models.statics.Responses;

@RestController
@RequestMapping("/offerings")
public class OfferingsController {
    
    @GetMapping("")
    public Object getAll(HttpServletResponse response) {
        String loggedInUserId = DataBase.getLoggedInUserId();
        if (loggedInUserId == null) {
            response.setStatus(401);
            return Responses.UnAuthorized;
        }
        ArrayList<Offering> offerings = DataBase.OfferingManager.getAll();
        ArrayList<HashMap<String, Object>> result = OfferingSerializer.serializeList(offerings);
        return result;
    }
}

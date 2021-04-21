package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import models.entities.Offering;
import models.logic.DataBase;
import models.serializers.OfferingSerializer;

@RestController
@RequestMapping("/offerings")
public class OfferingsController {
    
    @GetMapping("")
    public ArrayList<HashMap<String, Object>> getAll() {
        ArrayList<Offering> offerings = DataBase.OfferingManager.getAll();
        ArrayList<HashMap<String, Object>> response = OfferingSerializer.serializeList(offerings);
        return response;
    }
}

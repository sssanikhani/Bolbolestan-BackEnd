import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.Handler;

public class Handlers {

    public static Handler courses = ctx -> {

        HashMap<String, Object> data = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> offeringsDataList = new ArrayList<>();
        data.put("courses", offeringsDataList);

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Offering> offeringsList = DataBase.OfferingManager.getAll();
        for (Offering o : offeringsList) {
            HashMap<String, Object> oData = mapper.convertValue(o, HashMap.class);
            offeringsDataList.add(oData);
        }

        // String html = HtmlRenderer.renderCoursesPage(data);
        // ctx.html(html);
    };

    public static Handler studentProfile = ctx -> {

        // String html = HtmlRenderer.renderStudentProfilePage(data);
        // ctx.html(html);
    };

    public static Handler singleCourse = ctx -> {

        // String html = HtmlRenderer.renderSingleCoursePage(data);
        // ctx.html(html);
    };

    public static Handler changePlan = ctx -> {

        // String html = HtmlRenderer.renderChangePlanPage(data);
        // ctx.html(html);
    };

    public static Handler plan = ctx -> {

        // String html = HtmlRenderer.renderPlanPage(data);
        // ctx.html(html);
    };

    public static Handler submit = ctx -> {

        // String html = HtmlRenderer.renderSubmitPage(data);
        // ctx.html(html);
    };

    public static Handler okSubmit = ctx -> {

        // String html = HtmlRenderer.renderOkSubmitPage(data);
        // ctx.html(html);
    };

    public static Handler failSubmit = ctx -> {

        // String html = HtmlRenderer.renderFailSubmitPage(data);
        // ctx.html(html);
    };

}

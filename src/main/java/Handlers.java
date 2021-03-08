import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.Context;

public class Handlers {

    private static ObjectMapper mapper = new ObjectMapper();

    public static void courses(Context ctx) {
        HashMap<String, Object> response;

        ArrayList<HashMap<String, Object>> offeringsDataList = new ArrayList<>();
        ArrayList<Offering> offeringsList;
        try {
            offeringsList = DataBase.OfferingManager.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            response = Responses.ServerError;
            String html = HtmlRenderer.renderPage(response);
            ctx.status(500);
            ctx.html(html);
            return;
        }

        for (Offering o : offeringsList) {
            HashMap<String, Object> oData = mapper.convertValue(o, HashMap.class);
            offeringsDataList.add(oData);
        }

        response = new HashMap<String, Object>();
        response.put("courses", offeringsDataList);
        String html = HtmlRenderer.renderCoursesPage(response);
        ctx.html(html);
    }

    public static void studentProfile(Context ctx) {
        HashMap<String, Object> response;

        String studentId = ctx.pathParam("studentId");
        Student student;
        try {
            student = DataBase.StudentManager.get(studentId);
        } catch (Exceptions.StudentNotFound e) {
            String html = HtmlRenderer.renderNotFoundPage();
            ctx.html(html);
            return;
        }

        response = mapper.convertValue(student, HashMap.class);
        String html = HtmlRenderer.renderStudentProfilePage(response);
        ctx.html(html);
    }

    public static void singleCourse(Context ctx) {
        HashMap<String, Object> response;

        String code = ctx.pathParam("code");
        String classCode = ctx.pathParam("classCode");
        Offering offering;
        try {
            offering = DataBase.OfferingManager.get(code, classCode);
        } catch (Exceptions.offeringNotFound e) {
            String html = HtmlRenderer.renderNotFoundPage();
            ctx.html(html);
            return;
        }

        response = mapper.convertValue(offering, HashMap.class);
        String html = HtmlRenderer.renderSingleCoursePage(response);
        ctx.html(html);
    }

    public static void changePlan(Context ctx) {
        HashMap<String, Object> response;

        String studentId = ctx.pathParam("studentId");

        Student student;
        try {
            student = DataBase.StudentManager.get(studentId);
        } catch (Exceptions.StudentNotFound e) {
            String html = HtmlRenderer.renderNotFoundPage();
            ctx.html(html);
            return;
        }

        ArrayList<HashMap<String, Object>> offeringsDataList = new ArrayList<HashMap<String, Object>>();
        for (Offering o : student.getChosenOfferings()) {
            HashMap<String, Object> oData = mapper.convertValue(o, HashMap.class);
            offeringsDataList.add(oData);
        }

        response = mapper.convertValue(student, HashMap.class);
        String html = HtmlRenderer.renderChangePlanPage(response);
        ctx.html(html);
    }

    public static void plan(Context ctx) {
        HashMap<String, Object> response;

        String studentId = ctx.pathParam("studentId");
        Student student;
        try {
            student = DataBase.StudentManager.get(studentId);
        } catch (Exceptions.StudentNotFound e) {
            String html = HtmlRenderer.renderNotFoundPage();
            ctx.html(html);
            return;
        }

        ArrayList<HashMap<String, Object>> offeringsDataList = new ArrayList<HashMap<String, Object>>();
        for (Offering o : student.getChosenOfferings()) {
            HashMap<String, Object> oData = mapper.convertValue(o, HashMap.class);
            offeringsDataList.add(oData);
        }

        response = new HashMap<String, Object>();
        response.put("courses", offeringsDataList);
        String html = HtmlRenderer.renderPlanPage(response);
        ctx.html(html);
    }

    public static void submit(Context ctx) {
        HashMap<String, Object> response;

        String studentId = ctx.pathParam("studentId");
        Student student;
        try {
            student = DataBase.StudentManager.get(studentId);
        } catch (Exceptions.StudentNotFound e) {
            String html = HtmlRenderer.renderNotFoundPage();
            ctx.html(html);
            return;
        }

        response = mapper.convertValue(student, HashMap.class);
        response.put("submitLink", Server.SUBMIT_PLAN_URL);
        String html = HtmlRenderer.renderSubmitPage(response);
        ctx.html(html);
    }

    public static void okSubmit(Context ctx) {
        String html = HtmlRenderer.renderOkSubmitPage();
        ctx.html(html);
    }

    public static void failSubmit(Context ctx) {
        String html = HtmlRenderer.renderFailSubmitPage();
        ctx.html(html);
    }

    public static void addCourse(Context ctx) {
        HashMap<String, Object> response;

        String code = ctx.pathParam("code");
        String classCode = ctx.pathParam("classCode");

        Offering offering;
        try {
            offering = DataBase.OfferingManager.get(code, classCode);
        } catch (Exceptions.offeringNotFound e) {
            String html = HtmlRenderer.renderNotFoundPage();
            ctx.status(404);
            ctx.html(html);
            return;
        }

        String studentId = ctx.formParam("studentId");

        Student student;
        try {
            student = DataBase.StudentManager.get(studentId);
        } catch (Exceptions.StudentNotFound e) {
            response = Responses.BadRequest;
            String html = HtmlRenderer.renderPage(response);
            ctx.status(400);
            ctx.html(html);
            return;
        }

        boolean hasPassedPrerequisites;
        try {
            hasPassedPrerequisites = student.hasPassedPrerequisites(offering.getCode());
        } catch (Exception e) {
            response = Responses.ServerError;
            String html = HtmlRenderer.renderPage(response);
            ctx.status(500);
            ctx.html(html);
            return;
        }
        if (!hasPassedPrerequisites) {
            response = Responses.NotPassedPrerequisites;
            String html = HtmlRenderer.renderPage(response);
            ctx.status(403);
            ctx.html(html);
            return;
        }

        ArrayList<Offering> chosenOfferings = student.getChosenOfferings();
        for (Offering o : chosenOfferings) {
            if (offering.hasOfferingTimeCollision(o)) {
                response = Responses.CourseTimeCollision;
                String html = HtmlRenderer.renderPage(response);
                ctx.status(403);
                ctx.html(html);
                return;
            }
            boolean hasExamTimeCollision;
            try {
                hasExamTimeCollision = offering.hasExamTimeCollision(o);
            } catch (ParseException e) {
                e.printStackTrace();
                response = Responses.ServerError;
                String html = HtmlRenderer.renderPage(response);
                ctx.status(500);
                ctx.html(html);
                return;
            }
            if (hasExamTimeCollision) {
                response = Responses.ExamTimeCollision;
                String html = HtmlRenderer.renderPage(response);
                ctx.status(403);
                ctx.html(html);
                return;
            }
        }

        student.addOfferingToList(offering);

        ctx.redirect(Server.COURSES_URL);

    }

    public static void removeCourse(Context ctx) {
        HashMap<String, Object> response;

        String code = ctx.pathParam("code");
        String classCode = ctx.pathParam("classCode");

        Offering offering;
        try {
            offering = DataBase.OfferingManager.get(code, classCode);
        } catch (Exceptions.offeringNotFound e) {
            String html = HtmlRenderer.renderNotFoundPage();
            ctx.status(404);
            ctx.html(html);
            return;
        }

        String studentId = ctx.formParam("studentId");
        Student student;
        try {
            student = DataBase.StudentManager.get(studentId);
        } catch (Exceptions.StudentNotFound e) {
            response = Responses.BadRequest;
            String html = HtmlRenderer.renderPage(response);
            ctx.status(400);
            ctx.html(html);
            return;
        }

        try {
            student.removeOfferingFromList(offering.getCode());
        } catch (Exceptions.offeringNotFound e) {
            response = Responses.BadRequest;
            String html = HtmlRenderer.renderPage(response);
            ctx.status(400);
            ctx.html(html);
            return;
        }

        ctx.redirect(Utils.mkChangePlanLink(studentId));

    }

    public static void submitPlan(Context ctx) {
        HashMap<String, Object> response;

        String studentId = ctx.formParam("studentId");
        Student student;
        try {
            student = DataBase.StudentManager.get(studentId);
        } catch (Exceptions.StudentNotFound e) {
            response = Responses.BadRequest;
            String html = HtmlRenderer.renderPage(response);
            ctx.status(400);
            ctx.html(html);
            return;
        }

        int numUnits = student.getNumberChosenUnits();
        if (numUnits < Constants.MIN_ALLOWED_UNITS || numUnits > Constants.MAX_ALLOWED_UNITS) {
            ctx.html(HtmlRenderer.renderFailSubmitPage());
            return;
        }

        try {
            student.validateExamClassTimes();
            student.validateOfferingCapacities();
        } catch (Exceptions.OfferingCapacity e) {
            ctx.redirect(Server.SUBMIT_FAILED_URL);
        } catch (Exception e) {
            response = Responses.ServerError;
            String html = HtmlRenderer.renderPage(response);
            ctx.status(500);
            ctx.html(html);
            return;
        }

        student.finalizeOfferings();
        ctx.html(HtmlRenderer.renderOkSubmitPage());

    }

}

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.Context;

public class Handlers {

    private static ObjectMapper mapper = new ObjectMapper();

    // public static Handler courses = ctx -> {

    // ArrayList<HashMap<String, Object>> offeringsDataList = new ArrayList<>();
    // ArrayList<Offering> offeringsList = DataBase.OfferingManager.getAll();
    // for (Offering o : offeringsList) {
    // HashMap<String, Object> oData = mapper.convertValue(o, HashMap.class);
    // offeringsDataList.add(oData);
    // }

    // HashMap<String, Object> response = new HashMap<String, Object>();
    // response.put("courses", offeringsDataList);
    // // String html = HtmlRenderer.renderCoursesPage(response);
    // // ctx.html(html);
    // };

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

    // public static Handler studentProfile = ctx -> {
    // String studentId = ctx.pathParam("studentId");
    // Student student;
    // try {
    // student = DataBase.StudentManager.get(studentId);
    // } catch (Exceptions.StudentNotFound e) {
    // // String html = HtmlRenderer.renderNotFoundPage();
    // // ctx.html(html);
    // return;
    // }

    // response.clear();
    // response = mapper.convertValue(student, HashMap.class);
    // // String html = HtmlRenderer.renderStudentProfilePage(response);
    // // ctx.html(html);
    // };

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

    // public static Handler singleCourse = ctx -> {
    // String code = ctx.pathParam("code");
    // String classCode = ctx.pathParam("classCode");
    // Offering offering;
    // try {
    // offering = DataBase.OfferingManager.get(code, classCode);
    // } catch (Exceptions.offeringNotFound e) {
    // // String html = HtmlRenderer.renderNotFoundPage();
    // // ctx.html(html);
    // return;
    // }

    // HashMap<String, Object> response = mapper.convertValue(offering,
    // HashMap.class);
    // // String html = HtmlRenderer.renderSingleCoursePage(response);
    // // ctx.html(html);
    // };

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

    // public static Handler changePlan = ctx -> {
    // String studentId = ctx.pathParam("studentId");

    // Student student;
    // try {
    // student = DataBase.StudentManager.get(studentId);
    // } catch (Exceptions.StudentNotFound e) {
    // // String html = HtmlRenderer.renderNotFoundPage();
    // // ctx.html(html);
    // return;
    // }

    // ArrayList<HashMap<String, Object>> offeringsDataList = new
    // ArrayList<HashMap<String, Object>>();
    // for (Offering o : student.getChosenOfferings()) {
    // HashMap<String, Object> oData = mapper.convertValue(o, HashMap.class);
    // offeringsDataList.add(oData);
    // }

    // HashMap<String, Object> response = new HashMap<String, Object>();
    // response.put("courses", offeringsDataList);
    // response.put("studentId", student.getId());
    // // String html = HtmlRenderer.renderChangePlanPage(response);
    // // ctx.html(html);
    // };

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

        response = new HashMap<String, Object>();
        response.put("courses", offeringsDataList);
        response.put("studentId", student.getId());
        String html = HtmlRenderer.renderChangePlanPage(response);
        ctx.html(html);
    }

    // public static Handler plan = ctx -> {
    // String studentId = ctx.pathParam("studentId");
    // Student student;
    // try {
    // student = DataBase.StudentManager.get(studentId);
    // } catch (Exceptions.StudentNotFound e) {
    // // String html = HtmlRenderer.renderNotFoundPage();
    // // ctx.html(html);
    // return;
    // }

    // ArrayList<HashMap<String, Object>> offeringsDataList = new
    // ArrayList<HashMap<String, Object>>();
    // for (Offering o : student.getChosenOfferings()) {
    // HashMap<String, Object> oData = mapper.convertValue(o, HashMap.class);
    // offeringsDataList.add(oData);
    // }

    // HashMap<String, Object> response = new HashMap<String, Object>();
    // response.put("courses", offeringsDataList);
    // // String html = HtmlRenderer.renderPlanPage(response);
    // // ctx.html(html);
    // };

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

    // public static Handler submit = ctx -> {
    // String studentId = ctx.pathParam("studentId");
    // Student student;
    // try {
    // student = DataBase.StudentManager.get(studentId);
    // } catch (Exceptions.StudentNotFound e) {
    // // String html = HtmlRenderer.renderNotFoundPage();
    // // ctx.html(html);
    // return;
    // }

    // HashMap<String, Object> response = mapper.convertValue(student,
    // HashMap.class);
    // // String html = HtmlRenderer.renderSubmitPage(response);
    // // ctx.html(html);
    // };

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

    // public static Handler okSubmit = ctx -> {
    // // String html = HtmlRenderer.renderOkSubmitPage(response);
    // // ctx.html(html);
    // };

    public static void okSubmit(Context ctx) {
        String html = HtmlRenderer.renderOkSubmitPage();
        ctx.html(html);
    }

    // public static Handler failSubmit = ctx -> {
    // // String html = HtmlRenderer.renderFailSubmitPage(response);
    // // ctx.html(html);
    // };

    public static void failSubmit(Context ctx) {
        String html = HtmlRenderer.renderFailSubmitPage();
        ctx.html(html);
    }

    // public static Handler addCourse = ctx -> {
    // String code = ctx.pathParam("code");
    // String classCode = ctx.pathParam("classCode");

    // String body = ctx.body();
    // JsonNode bodyJson = mapper.readTree(body);
    // String studentId = bodyJson.get("studentId").asText();

    // Offering offering;
    // try {
    // offering = DataBase.OfferingManager.get(code, classCode);
    // } catch (Exceptions.offeringNotFound e) {
    // // String html = HtmlRenderer.renderNotFoundPage(response);
    // // ctx.html(html);
    // return;
    // }

    // Student student;
    // try {
    // student = DataBase.StudentManager.get(studentId);
    // } catch (Exceptions.StudentNotFound e) {
    // // String html = HtmlRenderer.renderNotFoundPage();
    // // ctx.html(html);
    // return;
    // }

    // ArrayList<Offering> chosenOfferings = student.getChosenOfferings();
    // for (Offering o : chosenOfferings) {
    // if (offering.hasOfferingTimeCollision(o))
    // }

    // };

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

        // HashMap<String, Object> body = ctx.bodyAsClass(HashMap.class);
        // JsonNode bodyJson;
        // try {
        //     bodyJson = mapper.readTree(body);
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     response = Responses.ServerError;
        //     String html = HtmlRenderer.renderPage(response);
        //     ctx.status(500);
        //     ctx.html(html);
        //     return;
        // }
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

        ctx.redirect(Utils.mkChangePlanLink(studentId), 200);

    }
    // public static Handler removeCourse = ctx -> {

    // };

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

        // HashMap<String, Object> body = ctx.bodyAsClass(HashMap.class);
        // JsonNode bodyJson;
        // try {
        //     bodyJson = mapper.readTree(body);
        // } catch (Exception e) {
        //     response = Responses.ServerError;
        //     String html = HtmlRenderer.renderPage(response);
        //     ctx.status(500);
        //     ctx.html(html);
        //     return;
        // }
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

        ctx.redirect(Server.CHANGE_PLAN_URL_PREFIX, 200);

    }

    // public static Handler submitPlan = ctx -> {
    // ctx.redirect(Server.SUBMIT_FAILED_URL);
    // };

    public static void submitPlan(Context ctx) {
        HashMap<String, Object> response;

        // HashMap<String, Object> body = ctx.bodyAsClass(HashMap.class);
        // JsonNode bodyJson;
        // try {
        //     bodyJson = mapper.readTree(body);
        // } catch (Exception e) {
        //     response = Responses.ServerError;
        //     String html = HtmlRenderer.renderPage(response);
        //     ctx.status(500);
        //     ctx.html(html);
        //     return;
        // }
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
        if (numUnits < Constants.MIN_ALLOWED_UNITS || numUnits > Constants.MAX_ALLOWED_UNITS)
            ctx.redirect(Server.SUBMIT_FAILED_URL, 403);

        try {
            student.validateExamClassTimes();
            student.validateOfferingCapacities();
        } catch (Exceptions.OfferingCapacity e) {
            ctx.redirect(Server.SUBMIT_FAILED_URL, 403);
        } catch (Exception e) {
            response = Responses.ServerError;
            String html = HtmlRenderer.renderPage(response);
            ctx.status(500);
            ctx.html(html);
            return;
        }

        student.finalizeOfferings();
        ctx.redirect(Server.SUBMIT_OK_URL, 200);

    }

}

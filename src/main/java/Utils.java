import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

    static LocalTime convertToLocalTime(String time) {
        LocalTime response;
        String timeStr = time;
        if (!timeStr.contains(":")) {
            timeStr = timeStr + ":00";
        }
        String hour = timeStr.split(":")[0];
        if (hour.length() < 2)
            timeStr = "0" + timeStr;
        response = LocalTime.parse(timeStr);
        return response;
    }

    public static HashMap<String, Object> sendRequest(String method, String url, HashMap<String, String> params,
            HashMap<String, Object> requestBody) throws IOException, InterruptedException {

        // An util to send request with "method", "params", "requestBody" to specified
        // "url"
        // Return type is a HashMap with this structure:
        // {
        // "status": <status_code>,
        // "data": <response_body>,
        // }

        if (params != null) {
            ArrayList<String> paramsList = new ArrayList<String>();

            for (String param : params.keySet()) {
                paramsList.add(param + "=" + params.get(param));
            }
            if (params.size() > 0) {
                url += "?";
                String parameters = String.join("&", paramsList);
                url += parameters;
            }
        }

        String body = "";
        if (requestBody != null) {
            ObjectMapper mapper = new ObjectMapper();
            body = mapper.writeValueAsString(requestBody);
        }

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(url);
        Builder builder = HttpRequest.newBuilder().uri(uri);
        HttpRequest request;
        switch (method) {
        case "GET":
            request = builder.GET().build();
            break;
        case "POST":
            request = builder.POST(BodyPublishers.ofString(body)).header("Content-Type", "application/json").build();
            break;
        case "PUT":
            request = builder.PUT(BodyPublishers.ofString(body)).header("Content-Type", "application/json").build();
            break;
        case "DELETE":
            request = builder.DELETE().build();
            break;
        default:
            return null;
        }
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String resBody = response.body();
        int resStatus = response.statusCode();

        HashMap<String, Object> res = new HashMap<String, Object>();
        res.put("status", resStatus);
        res.put("data", resBody);

        return res;
    }

    public static int getCodeUnits(String code) throws Exceptions.offeringNotFound {
        ArrayList<Offering> codeOfferings = DataBase.OfferingManager.getCodeOfferings(code);
        if (codeOfferings.size() == 0)
            throw new Exceptions.offeringNotFound();
        return codeOfferings.get(0).getUnits();
    }
}

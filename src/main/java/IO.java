import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IO {

    public IO() { }

    public HashMap<String, Offering> GETOfferings() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://138.197.181.131:5000/api/courses"))
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        ObjectMapper mapper = new ObjectMapper();
        List<Offering> offers = mapper.readValue(body, new TypeReference<List<Offering>>() {});
        HashMap<String, Offering> allOffers = new HashMap<String, Offering>();
        for(Offering o : offers) {
            allOffers.put(o.getCode(), o);
        }
        return allOffers;
    }

    public HashMap<String, Student> GETStds() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://138.197.181.131:5000/api/students"))
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        ObjectMapper mapper = new ObjectMapper();
        List<Student> stds = mapper.readValue(body, new TypeReference<List<Student>>() {});
        HashMap<String, Student> allStds = new HashMap<String, Student>();
        for(Student s : stds) {
            allStds.put(s.getId(), s);
        }
        return allStds;
    }

    public List<Grade> GETGrades(String s) throws IOException, InterruptedException {
        HttpClient client_grade = HttpClient.newHttpClient();
        HttpRequest request_grade = HttpRequest.newBuilder()
                .uri(URI.create("http://138.197.181.131:5000/api/grades/" + s))
                .build();
        HttpResponse<String> response_grade =
                client_grade.send(request_grade, HttpResponse.BodyHandlers.ofString());
        String body_grade = response_grade.body();
        ObjectMapper mapper = new ObjectMapper();
        List<Grade> grades = mapper.readValue(body_grade, new TypeReference<List<Grade>>() {});
        return grades;
    }
}

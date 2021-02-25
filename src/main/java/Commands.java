import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Commands {
    public Commands() {
    }
    public static Course addCourse(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Course newCourse = mapper.readValue(js, Course.class);
        return newCourse;
    }
}

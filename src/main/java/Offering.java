import java.text.ParseException;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonView;

//@JsonIgnoreProperties(ignoreUnknown = false)
public class Offering {
    @JsonView({ View.normal.class, View.offerings.class, View.weeklySch.class })
    private String code;
    @JsonView({ View.normal.class, View.offerings.class, View.weeklySch.class })
    private String name;
    @JsonView({ View.normal.class, View.offerings.class, View.weeklySch.class })
    private String instructor;
    @JsonView(View.normal.class)
    private int units;
    @JsonView({ View.normal.class, View.weeklySch.class })
    private OfferingClassTime classTime;
    @JsonView({ View.normal.class, View.weeklySch.class })
    private OfferingExamTime examTime;
    @JsonView(View.normal.class)
    private int capacity;
    @JsonView(View.normal.class)
    private String[] prerequisites;
    @JsonView(View.weeklySch.class)
    private String status = "non-finalize";

    private HashMap<String, Student> registeredStudents;

    public Offering() {
        registeredStudents = new HashMap<String, Student>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getNumRegisteredStudents() {
        return registeredStudents.size();
    }

    public void addStudent(Student s) {
        registeredStudents.put(s.getStudentId(), s);
    }

    public String[] getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String[] prerequisites) {
        this.prerequisites = prerequisites;
    }

    public OfferingClassTime getClassTime() {
        return classTime;
    }

    public void setClassTime(OfferingClassTime classTime) {
        this.classTime = classTime;
    }

    public OfferingExamTime getExamTime() {
        return examTime;
    }

    public void setExamTime(OfferingExamTime examTime) {
        this.examTime = examTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasOfferingTimeCollision(Offering c) {
        return this.classTime.hasCollision(c.getClassTime());
    }

    public boolean hasExamTimeCollision(Offering c) throws ParseException {
        return this.examTime.hasCollision(c.getExamTime());
    }
}

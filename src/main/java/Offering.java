import java.text.ParseException;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonView;

public class Offering {
    @JsonView({ View.normal.class, View.offerings.class })
    private String code;
    @JsonView({ View.normal.class, View.offerings.class })
    private String name;
    @JsonView({ View.normal.class, View.offerings.class })
    private String instructor;
    @JsonView(View.normal.class)
    private int units;
    @JsonView({ View.normal.class })
    private OfferingClassTime classTime;
    @JsonView({ View.normal.class })
    private OfferingExamTime examTime;
    @JsonView(View.normal.class)
    private int capacity;
    @JsonView(View.normal.class)
    private String[] prerequisites;

    private HashMap<String, Student> registeredStudents;

    public Offering() {
        this.registeredStudents = new HashMap<String, Student>();
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String _code) {
        this.code = _code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public String getInstructor() {
        return this.instructor;
    }

    public void setInstructor(String _instructor) {
        this.instructor = _instructor;
    }

    public int getUnits() {
        return this.units;
    }

    public void setUnits(int _units) {
        this.units = _units;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int _capacity) {
        this.capacity = _capacity;
    }

    public int getNumRegisteredStudents() {
        return this.registeredStudents.size();
    }

    public void addStudent(Student s) {
        this.registeredStudents.put(s.getStudentId(), s);
    }

    public void removeStudent(String stdId) throws Exception {
        Student s = this.registeredStudents.get(stdId);
        if (s == null)
            throw new Exceptions.StudentNotFound();
        this.registeredStudents.remove(stdId);
    }

    public boolean existStudent(String stdId) {
        Student s = this.registeredStudents.get(stdId);
        return (s == null) ? false : true;
    }

    public String[] getPrerequisites() {
        return this.prerequisites;
    }

    public void setPrerequisites(String[] _prerequisites) {
        this.prerequisites = _prerequisites;
    }

    public OfferingClassTime getClassTime() {
        return this.classTime;
    }

    public void setClassTime(OfferingClassTime _classTime) {
        this.classTime = _classTime;
    }

    public OfferingExamTime getExamTime() {
        return this.examTime;
    }

    public void setExamTime(OfferingExamTime _examTime) {
        this.examTime = _examTime;
    }

    public boolean hasOfferingTimeCollision(Offering c) {
        return this.classTime.hasCollision(c.getClassTime());
    }

    public boolean hasExamTimeCollision(Offering c) throws ParseException {
        return this.examTime.hasCollision(c.getExamTime());
    }
}

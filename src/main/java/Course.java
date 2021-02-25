//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonView;

//@JsonIgnoreProperties(ignoreUnknown = false)
public class Course {
    @JsonView({View.normal.class, View.offerings.class})
    private String code;
    @JsonView({View.normal.class, View.offerings.class})
    private String name;
    @JsonView({View.normal.class, View.offerings.class})
    private String instructor;
    @JsonView(View.normal.class)
    private int units;
    @JsonView(View.normal.class)
    private CourseClassTime classTime;
    @JsonView(View.normal.class)
    private CourseExamTime examTime;
    @JsonView(View.normal.class)
    private int capacity;
    @JsonView(View.normal.class)
    private String[] prerequisites;

    public Course() {
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

    public String[] getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String[] prerequisites) {
        this.prerequisites = prerequisites;
    }

    public CourseClassTime getClassTime() {
        return classTime;
    }

    public void setClassTime(CourseClassTime classTime) {
        this.classTime = classTime;
    }

    public CourseExamTime getExamTime() {
        return examTime;
    }

    public void setExamTime(CourseExamTime examTime) {
        this.examTime = examTime;
    }
}

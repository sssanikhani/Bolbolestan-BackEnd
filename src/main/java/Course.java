//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//@JsonIgnoreProperties(ignoreUnknown = false)
public class Course {

    private String code;
    private String name;
    private String instructor;
    private int units;
    private CourseClassTime classTime;
    private CourseExamTime examTime;
    private int capacity;
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

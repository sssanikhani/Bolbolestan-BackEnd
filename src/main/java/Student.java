import java.util.ArrayList;

public class Student {
    private String studentId;
    private String name;
    private String enteredAt;
    private ArrayList<Course> weeklyCourses = new ArrayList<Course>();

    public Student() {
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(String enteredAt) {
        this.enteredAt = enteredAt;
    }

    public ArrayList<Course> getWeeklyCourses() {
        return weeklyCourses;
    }

    public void setWeeklyCourses(ArrayList<Course> weeklyCourses) {
        this.weeklyCourses = weeklyCourses;
    }

    public void addCourseToList(Course c) {
        this.weeklyCourses.add(c);
        System.out.println(this.weeklyCourses);

    }

    public void removeCourseFromList(Course c) {
        this.weeklyCourses.remove(c);
        System.out.println(this.weeklyCourses);
    }
}

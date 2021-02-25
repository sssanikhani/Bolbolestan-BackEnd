

public class Student {
    private String studentId;
    private String name;
    private String enteredAt;
    private Course[] weeklyCourses;

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

    public Course[] getWeeklyCourses() {
        return weeklyCourses;
    }

    public void setWeeklyCourses(Course[] weeklyCourses) {
        this.weeklyCourses = weeklyCourses;
    }
}

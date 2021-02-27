import java.util.HashMap;

public class Student {
    private String studentId;
    private String name;
    private String enteredAt;
    private int numberChosenUnits = 0;
    // private ArrayList<Course> weeklyCourses = new ArrayList<Course>();
    private HashMap<String, Offer> weeklyCourses = new HashMap<String, Offer>();

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

    public HashMap<String, Offer> getWeeklyCourses() {
        return weeklyCourses;
    }

    public int getNumberChosenUnits() {
        return numberChosenUnits;
    }
    // public void setWeeklyCourses(HashMap<String, Offer> weeklyCourses) {
    // this.weeklyCourses = weeklyCourses;
    // }

    public void addCourseToList(Offer c) {
        this.weeklyCourses.put(c.getCode(), c);
        this.numberChosenUnits += c.getUnits();
        // System.out.println(this.weeklyCourses);
    }

    public Offer removeCourseFromList(String c) throws Exception {
        // this.weeklyCourses.remove(c);
        Offer offer = this.weeklyCourses.get(c);
        if (offer == null) {
            throw new Exceptions.OfferingNotFound();
        }
        this.numberChosenUnits -= offer.getUnits();
        return this.weeklyCourses.remove(c);
    }
}

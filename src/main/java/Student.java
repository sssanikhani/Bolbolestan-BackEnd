import java.util.HashMap;
import java.util.Set;

public class Student {
    private String studentId;
    private String name;
    private String enteredAt;
    private int numberChosenUnits;
    // private ArrayList<Course> weeklyCourses = new ArrayList<Course>();
    private HashMap<String, Offer> weeklyCourses;

    public Student() {
        weeklyCourses = new HashMap<String, Offer>();
        numberChosenUnits = 0;
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

    public void validateExamClassTimes() throws Exception {
        HashMap<String, Offer> temp = new HashMap<String, Offer>();
        temp.putAll(weeklyCourses);
        Set<String> offerKeySet = temp.keySet();
        for (String k1 : offerKeySet) {
            for (String k2 : temp.keySet()) {
                if (k1 != k2) {
                    Offer o1 = temp.get(k1);
                    Offer o2 = temp.get(k2);
                    if (o1.hasOfferTimeCollision(o2))
                        throw new Exceptions.ClassTimeCollision(o1.getCode(), o2.getCode());
                    if (o1.hasExamTimeCollision(o2))
                        throw new Exceptions.ExamTimeCollision(o1.getCode(), o2.getCode());
                }
            }
            temp.remove(k1);
        }
    }

    public void validateOfferCapacities() throws Exception {
        Set<String> offerKeySet = weeklyCourses.keySet();
        for (String key : offerKeySet) {
            Offer o = weeklyCourses.get(key);
            if (o.getCapacity() >= o.getNumRegisteredStudents())
                throw new Exceptions.OfferCapacity(o.getCode());
        }
    }
}

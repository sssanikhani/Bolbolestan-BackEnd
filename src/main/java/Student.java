import java.util.HashMap;
import java.util.Set;

public class Student {
    private String studentId;
    private String name;
    private String enteredAt;
    private int numberChosenUnits;
    // private ArrayList<Offering> offerings = new ArrayList<Offering>();
    private HashMap<String, Offering> offerings;

    public Student() {
        offerings = new HashMap<String, Offering>();
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

    public HashMap<String, Offering> getOfferings() {
        return offerings;
    }

    public int getNumberChosenUnits() {
        return numberChosenUnits;
    }
    // public void setOfferings(HashMap<String, Offering> offerings) {
    // this.offerings = offerings;
    // }

    public void addOfferingToList(Offering c) {
        this.offerings.put(c.getCode(), c);
        this.numberChosenUnits += c.getUnits();
        // System.out.println(this.offerings);
    }

    public Offering removeOfferingFromList(String c) throws Exception {
        // this.offerings.remove(c);
        Offering offering = this.offerings.get(c);
        if (offering == null) {
            throw new Exceptions.offeringNotFound();
        }
        this.numberChosenUnits -= offering.getUnits();
        return this.offerings.remove(c);
    }

    public void validateExamClassTimes() throws Exception {
        for (String k1 : offerings.keySet()) {
            for (String k2 : offerings.keySet()) {
                if (!k1.equals(k2)) {
                    Offering o1 = offerings.get(k1);
                    Offering o2 = offerings.get(k2);
                    if (o1.hasOfferingTimeCollision(o2))
                        throw new Exceptions.ClassTimeCollision(o1.getCode(), o2.getCode());
                    if (o1.hasExamTimeCollision(o2))
                        throw new Exceptions.ExamTimeCollision(o1.getCode(), o2.getCode());
                }
            }
        }
    }

    public void validateOfferingCapacities() throws Exception {
        Set<String> offeringKeySet = offerings.keySet();
        for (String key : offeringKeySet) {
            Offering o = offerings.get(key);
            if (o.getCapacity() >= o.getNumRegisteredStudents())
                throw new Exceptions.OfferingCapacity(o.getCode());
        }
    }
}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class Student {
    private String studentId;
    private String name;
    private String enteredAt;
    private int numberChosenUnits;
    // private ArrayList<Offering> offerings = new ArrayList<Offering>();
    private HashMap<String, Offering> offerings;
    private HashMap<String, Boolean> offeringStatus;

    public Student() {
        this.offerings = new HashMap<String, Offering>();
        this.offeringStatus = new HashMap<String, Boolean>();
        this.numberChosenUnits = 0;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String _studentId) {
        this.studentId = _studentId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public String getEnteredAt() {
        return this.enteredAt;
    }

    public void setEnteredAt(String _enteredAt) {
        this.enteredAt = _enteredAt;
    }

    public HashMap<String, Offering> getOfferings() {
        return this.offerings;
    }

    public List<LinkedHashMap<String, Object>> getOfferingsData() {
        List<LinkedHashMap<String, Object>> data = new ArrayList<LinkedHashMap<String, Object>>();
        for (String code : this.offerings.keySet()) {
            Offering o = this.offerings.get(code);
            LinkedHashMap<String, Object> o_data = new LinkedHashMap<String, Object>();

            o_data.put("code", o.getCode());
            o_data.put("name", o.getName());
            o_data.put("instructor", o.getInstructor());
            o_data.put("classTime", o.getClassTime());
            o_data.put("examTime", o.getExamTime());
            boolean is_finalized = this.offeringStatus.get(o.getCode());
            String finalized = is_finalized ? "finalized" : "non-finalized";
            o_data.put("status", finalized);

            data.add(o_data);
        }
        return data;
    }

    public int getNumberChosenUnits() {
        return this.numberChosenUnits;
    }

    // public void setOfferings(HashMap<String, Offering> offerings) {
    // this.offerings = offerings;
    // }

    public void addOfferingToList(Offering o) {
        this.offerings.put(o.getCode(), o);
        this.offeringStatus.put(o.getCode(), false);
        this.numberChosenUnits += o.getUnits();
        // System.out.println(this.offerings);
    }

    public Offering removeOfferingFromList(String c) throws Exception {
        // this.offerings.remove(c);
        Offering offering = this.offerings.get(c);
        if (offering == null)
            throw new Exceptions.offeringNotFound();
        this.numberChosenUnits -= offering.getUnits();
        this.offeringStatus.remove(c);
        return this.offerings.remove(c);
    }

    public void validateExamClassTimes() throws Exception {
        for (String k1 : this.offerings.keySet()) {
            for (String k2 : this.offerings.keySet()) {
                if (!k1.equals(k2)) {
                    Offering o1 = this.offerings.get(k1);
                    Offering o2 = this.offerings.get(k2);
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
            if (o.getCapacity() <= o.getNumRegisteredStudents())
                throw new Exceptions.OfferingCapacity(o.getCode());
        }
    }

    public void finalizeOfferings() {
        for (String code : this.offerings.keySet()) {
            Offering o = this.offerings.get(code);
            o.addStudent(this);
            this.offeringStatus.put(code, true);
        }
    }
}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class Student {
    private String id;
    private String name;
    private String secondName;
    private String birthDate;
    private HashMap<String, Offering> offerings;
    private HashMap<String, Grade> grades;

    public Student() {
        this.offerings = new HashMap<String, Offering>();
        this.grades = new HashMap<String, Grade>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public HashMap<String, Offering> getOfferings() {
        return this.offerings;
    }

    public List<LinkedHashMap<String, Object>> getOfferingsData() {
        List<LinkedHashMap<String, Object>> data = new ArrayList<LinkedHashMap<String, Object>>();
        for (String code : this.offerings.keySet()) {
            Offering o = this.offerings.get(code);
            LinkedHashMap<String, Object> oData = new LinkedHashMap<String, Object>();

            oData.put("code", o.getCode());
            oData.put("name", o.getName());
            oData.put("instructor", o.getInstructor());
            oData.put("classTime", o.getClassTime());
            oData.put("examTime", o.getExamTime());
            boolean isFinalized = o.existStudent(this.id);
            String finalized = isFinalized ? "finalized" : "non-finalized";
            oData.put("status", finalized);

            data.add(oData);
        }
        return data;
    }

    public HashMap<String, Grade> getGrades() {
        return this.grades;
    }

    public void addGrade(Grade _grade) {
        String code = _grade.getCode();
        this.grades.put(code, _grade);
    }

    public int getNumberChosenUnits() {
        int units = 0;
        for (String key : this.offerings.keySet()) {
            Offering o = this.offerings.get(key);
            units += o.getUnits();
        }
        return units;
    }

    public void addOfferingToList(Offering o) {
        this.offerings.put(o.getCode(), o);
    }

    public void removeOfferingFromList(String c) throws Exception {
        Offering offering = this.offerings.get(c);
        if (offering == null)
            throw new Exceptions.offeringNotFound();
        boolean finalized = offering.existStudent(this.id);
        if (finalized)
            offering.removeStudent(this.id);
        this.offerings.remove(c);
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
        }
    }
}

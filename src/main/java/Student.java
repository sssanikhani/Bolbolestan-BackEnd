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
    private HashMap<String, Offering> chosenOfferings;
    private HashMap<String, Grade> grades;

    public Student() {
        this.chosenOfferings = new HashMap<String, Offering>();
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

    public ArrayList<Offering> getChosenOfferings() {
        return new ArrayList<Offering>(this.chosenOfferings.values());
    }

    public List<LinkedHashMap<String, Object>> _getChosenOfferingsData() {
        List<LinkedHashMap<String, Object>> data = new ArrayList<LinkedHashMap<String, Object>>();
        for (String code : this.chosenOfferings.keySet()) {
            Offering o = this.chosenOfferings.get(code);
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

    public HashMap<String, Grade> _getGrades() {
        return this.grades;
    }

    public void addGrade(Grade _grade) {
        String code = _grade.getCode();
        this.grades.put(code, _grade);
    }

    public int getNumberChosenUnits() {
        int units = 0;
        for (String key : this.chosenOfferings.keySet()) {
            Offering o = this.chosenOfferings.get(key);
            units += o.getUnits();
        }
        return units;
    }

    public boolean hasPassed(String _code) {
        Grade grade = this.grades.get(_code);
        if (grade == null)
            return false;
        if (grade.getGrade() < 10)
            return false;
        return true;
    }

    public ArrayList<Grade> getPassedCoursesGrades() {
        ArrayList<Grade> passedGrades = new ArrayList<Grade>();
        for (Grade g : this.grades.values()) {
            if (this.hasPassed(g.getCode()))
                passedGrades.add(g);
        }
        return passedGrades;
    }

    public int getTotalPassedUnits() throws Exception {
        int passed = 0;
        for (Grade g : this.grades.values()) {
            String code = g.getCode();
            if (this.hasPassed(code)) {
                ArrayList<Offering> codeOfferings = DataBase.OfferingManager.getCodeOfferings(code);
                if (codeOfferings.size() == 0)
                    throw new Exceptions.offeringNotFound();
                passed += codeOfferings.get(0).getUnits();
            }
        }
        return passed;
    }

    public float getGpa() throws Exception {
        float sumGrades = 0;
        int totalUnits = 0;
        for (Grade g : this.grades.values()) {
            String code = g.getCode();
            int unit = Utils.getCodeUnits(code);
            sumGrades += g.getGrade() * unit;
            totalUnits += unit;
        }
        if (totalUnits == 0)
            return 0;

        return sumGrades / totalUnits;
    }

    public void addOfferingToList(Offering o) {
        this.chosenOfferings.put(o.getCode(), o);
    }

    public void removeOfferingFromList(String c) throws Exception {
        Offering offering = this.chosenOfferings.get(c);
        if (offering == null)
            throw new Exceptions.offeringNotFound();
        boolean finalized = offering.existStudent(this.id);
        if (finalized)
            offering.removeStudent(this.id);
        this.chosenOfferings.remove(c);
    }

    public void validateExamClassTimes() throws Exception {
        for (String k1 : this.chosenOfferings.keySet()) {
            for (String k2 : this.chosenOfferings.keySet()) {
                if (!k1.equals(k2)) {
                    Offering o1 = this.chosenOfferings.get(k1);
                    Offering o2 = this.chosenOfferings.get(k2);
                    if (o1.hasOfferingTimeCollision(o2))
                        throw new Exceptions.ClassTimeCollision(o1.getCode(), o2.getCode());
                    if (o1.hasExamTimeCollision(o2))
                        throw new Exceptions.ExamTimeCollision(o1.getCode(), o2.getCode());
                }
            }
        }
    }

    public void validateOfferingCapacities() throws Exception {
        Set<String> offeringKeySet = chosenOfferings.keySet();
        for (String key : offeringKeySet) {
            Offering o = chosenOfferings.get(key);
            if (o.getCapacity() <= o.getNumRegisteredStudents())
                throw new Exceptions.OfferingCapacity(o.getCode());
        }
    }

    public void finalizeOfferings() {
        for (String code : this.chosenOfferings.keySet()) {
            Offering o = this.chosenOfferings.get(code);
            o.addStudent(this);
        }
    }
}

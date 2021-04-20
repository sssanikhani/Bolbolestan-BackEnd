package models.entities;

import java.util.HashMap;

public class Term {
    private int term;
    private HashMap<String, Grade> grades;

    public int getTerm() {
        return this.term;
    }

    public int getUnits() {
        int units = 0;
        for (Grade g : this.grades.values()) {
            units += g.getCourse().getUnits();
        }
        return units;
    }

    public float getGpa() {
        float total = 0;
        for (Grade g : this.grades.values()) {
            total += g.getGrade() * g.getCourse().getUnits();
        }
        return total / this.getUnits();
    }

    public void addGrade(Grade _grade) {
        String code = _grade.getCourse().getCode();
        this.grades.put(code, _grade);
    }
}

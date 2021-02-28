import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalTime;
import java.util.ArrayList;


public class OfferClassTime {
    private ArrayList<String> days;
    @JsonIgnore
    private LocalTime startTime;
    @JsonIgnore
    private LocalTime endTime;
    private String time;

    public OfferClassTime() {
    }

    public ArrayList<String> getDays() {
        return days;
    }

    public void setDays(ArrayList<String> days) {
        this.days = days;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        String[] timePart = time.split("-");
        String s = timePart[0];
        String e = timePart[1];
        this.startTime = Utils.convertToLocalTime(s);
        this.endTime = Utils.convertToLocalTime(e);
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean hasTimeCollision(OfferClassTime t) {
        LocalTime s1 = this.startTime;
        LocalTime s2 = t.getStartTime();
        LocalTime e1 = this.endTime;
        LocalTime e2 = t.getEndTime();

        // (S1, _________E1)
        // _____(S2, E2)
        if (s1.compareTo(s2) <= 0 && e1.compareTo(e2) >= 0)
            return true;

        // _____(S1, E1)
        // (S2, _________E2)
        if (s1.compareTo(s2) >= 0 && e1.compareTo(e2) <= 0)
            return true;

        // (S1, E1)
        // ____(S2, E2)
        if (s1.compareTo(s2) < 0 && e1.compareTo(s2) > 0)
            return true;

        // ____(S1, E1)
        // (S2, E2)
        if (s1.compareTo(s2) > 0 && s1.compareTo(e2) < 0)
            return true;

        return false;
    }

    public boolean hasCollision(OfferClassTime t) {
        for (String d1 : days) {
            for (String d2 : t.getDays()) {
                if (d1 == d2)
                    if (this.hasTimeCollision(t))
                        return true;
            }
        }
        return false;
    }
}

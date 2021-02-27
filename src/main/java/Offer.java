import com.fasterxml.jackson.annotation.JsonView;

//@JsonIgnoreProperties(ignoreUnknown = false)
public class Offer {
    @JsonView({ View.normal.class, View.offerings.class, View.weeklySch.class })
    private String code;
    @JsonView({ View.normal.class, View.offerings.class, View.weeklySch.class })
    private String name;
    @JsonView({ View.normal.class, View.offerings.class, View.weeklySch.class })
    private String instructor;
    @JsonView(View.normal.class)
    private int units;
    @JsonView({ View.normal.class, View.weeklySch.class })
    private OfferClassTime classTime;
    @JsonView({ View.normal.class, View.weeklySch.class })
    private OfferExamTime examTime;
    @JsonView(View.normal.class)
    private int capacity;
    @JsonView(View.normal.class)
    private String[] prerequisites;
    @JsonView(View.weeklySch.class)
    private String status = "non-finalize";

    public Offer() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String[] getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String[] prerequisites) {
        this.prerequisites = prerequisites;
    }

    public OfferClassTime getClassTime() {
        return classTime;
    }

    public void setClassTime(OfferClassTime classTime) {
        this.classTime = classTime;
    }

    public OfferExamTime getExamTime() {
        return examTime;
    }

    public void setExamTime(OfferExamTime examTime) {
        this.examTime = examTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

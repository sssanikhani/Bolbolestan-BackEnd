import java.util.HashMap;
import java.util.Set;

public class Student {
    private String studentId;
    private String name;
    private String enteredAt;
    private int numberChosenUnits;
    // private ArrayList<Offer> offers = new ArrayList<Offer>();
    private HashMap<String, Offer> offers;

    public Student() {
        offers = new HashMap<String, Offer>();
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

    public HashMap<String, Offer> getOffers() {
        return offers;
    }

    public int getNumberChosenUnits() {
        return numberChosenUnits;
    }
    // public void setOffers(HashMap<String, Offer> offers) {
    // this.offers = offers;
    // }

    public void addOfferToList(Offer c) {
        this.offers.put(c.getCode(), c);
        this.numberChosenUnits += c.getUnits();
        // System.out.println(this.offers);
    }

    public Offer removeOfferFromList(String c) throws Exception {
        // this.offers.remove(c);
        Offer offer = this.offers.get(c);
        if (offer == null) {
            throw new Exceptions.OfferingNotFound();
        }
        this.numberChosenUnits -= offer.getUnits();
        return this.offers.remove(c);
    }

    public void validateExamClassTimes() throws Exception {
        for (String k1 : offers.keySet()) {
            for (String k2 : offers.keySet()) {
                if (!k1.equals(k2)) {
                    Offer o1 = offers.get(k1);
                    Offer o2 = offers.get(k2);
                    if (o1.hasOfferTimeCollision(o2))
                        throw new Exceptions.ClassTimeCollision(o1.getCode(), o2.getCode());
                    if (o1.hasExamTimeCollision(o2))
                        throw new Exceptions.ExamTimeCollision(o1.getCode(), o2.getCode());
                }
            }
        }
    }

    public void validateOfferCapacities() throws Exception {
        Set<String> offerKeySet = offers.keySet();
        for (String key : offerKeySet) {
            Offer o = offers.get(key);
            if (o.getCapacity() >= o.getNumRegisteredStudents())
                throw new Exceptions.OfferCapacity(o.getCode());
        }
    }
}

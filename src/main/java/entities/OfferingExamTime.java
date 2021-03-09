package entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OfferingExamTime {

	String start;
	String end;

	@JsonIgnore
	Date startDate;

	@JsonIgnore
	Date endDate;

	public OfferingExamTime() {}

	public String getStart() {
		return start;
	}

	public void setStart(String start) throws ParseException {
		this.start = start;
		SimpleDateFormat frm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		this.startDate = frm.parse(start);
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) throws ParseException {
		this.end = end;
		SimpleDateFormat frm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		this.endDate = frm.parse(end);
	}

	public Date getEndDate() {
		return endDate;
	}

	public boolean hasCollision(OfferingExamTime t) throws ParseException {
		Date s1 = this.startDate;
		Date s2 = t.getStartDate();
		Date e1 = this.endDate;
		Date e2 = t.getEndDate();

		// (S1, _________E1)
		// _____(S2, E2)
		if (s1.compareTo(s2) <= 0 && e1.compareTo(e2) >= 0) return true;

		// _____(S1, E1)
		// (S2, _________E2)
		if (s1.compareTo(s2) >= 0 && e1.compareTo(e2) <= 0) return true;

		// (S1, E1)
		// ____(S2, E2)
		if (s1.compareTo(s2) < 0 && e1.compareTo(s2) > 0) return true;

		// ____(S1, E1)
		// (S2, E2)
		if (s1.compareTo(s2) > 0 && s1.compareTo(e2) < 0) return true;

		return false;
	}
}

import java.time.LocalTime;

public class Utils {

    static LocalTime convertToLocalTime(String time) {
        LocalTime response;
        String time_str = time;
        if (!time_str.contains(":")) {
            time_str = time_str + ":00";
        }
        String hour = time_str.split(":")[0];
        if (hour.length() < 2)
            time_str = "0" + time_str;
        response = LocalTime.parse(time_str);
        return response;
    }

}

package fortscale.web.rest.Utils;

import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by shays on 08/10/2015.
 */
public class ApiUtils {

    public static Date getStartOfBeforeXDays(int numberOfDays){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));

        cal.add(Calendar.DAY_OF_MONTH, -1 * numberOfDays);
        Date date = cal.getTime();
        return date;

    }

    public static String[] splitToArrayOfStrings(String string){
        String[] stringArr;
        if (string.contains(",")){
            stringArr = string.split(",");
        }  else {
            stringArr = new String[1];
            stringArr[0] = string;
        }

        return stringArr;
    }
}

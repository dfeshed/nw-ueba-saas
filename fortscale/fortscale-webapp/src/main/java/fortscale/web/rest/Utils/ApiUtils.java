package fortscale.web.rest.Utils;

import org.apache.commons.lang.time.DateUtils;

import java.util.*;

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

    public static List<Long> splitTo2Longs(String string){

        if (string.contains(",")){
            String[] stringArr = string.split(",");
            if (stringArr.length!=2) {
                throw new RuntimeException("Expected to value sepereated with ,");
            } else {
                List<Long> list = new ArrayList<>();
                list.add(Long.parseLong(stringArr[0].trim()));
                list.add(Long.parseLong(stringArr[1].trim()));
                return  list;
            }

        }  else {
            throw new RuntimeException("Expected to value sepereated with ,");
        }


    }
}

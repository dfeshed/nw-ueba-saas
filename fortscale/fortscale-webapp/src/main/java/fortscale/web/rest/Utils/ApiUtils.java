package fortscale.web.rest.Utils;

import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.*;

/**
 * Created by shays on 08/10/2015.
 */
public class ApiUtils {

    /**
     * Convert
     * @param string to numbers splited by ","
     * @return array which the first item is the "from date" in miliseconds,
     *          and the second is the "to date" in miliseconds
     * @throws RuntimeException if the string is not 2 numbers splitted by ","
     */
    public static List<Long> splitTimeRangeToFromAndToMiliseconds(String string){

        if (string.contains(",")){
            String[] stringArr = string.split(",");
            if (stringArr.length!=2) {
                throw new IllegalArgumentException ("Expected to value to be long numbers splitted by ,");
            } else {
                List<Long> list = new ArrayList<>();

                long fromTime = TimestampUtils.convertToMilliSeconds(Long.parseLong(stringArr[0].trim()));
                long toTime = TimestampUtils.convertToMilliSeconds(Long.parseLong(stringArr[1].trim()));

                fromTime = TimestampUtils.convertToMilliSeconds(fromTime);
                 toTime = TimestampUtils.convertToMilliSeconds(toTime);

                list.add(fromTime);
                list.add(toTime);
                return  list;
            }

        }  else {
            throw new IllegalArgumentException ("Expected to value to be long numbers splitted by ,");
        }


    }
}

package fortscale.collection.services.time;

import java.util.LinkedList;
import java.util.List;

/**
 * Static class which holds all available date formats
 * TODO move to property file
 *
 * @author gils
 *02/03/2016
 */
public class FortscaleDateTimeFormats {

    private static List<String> availableDateFormatList = new LinkedList<>();

    static {
        // a suite of default date formats
        availableDateFormatList.add("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        availableDateFormatList.add("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        availableDateFormatList.add("yyyyMMddHHmmss'.0Z'");
        availableDateFormatList.add("MM/dd/yyyy HH:mm:ss z");
        availableDateFormatList.add("MM/dd/yyyy HH:mm:ss");
        availableDateFormatList.add("yyyy/MM/dd HH:mm:ss");
        availableDateFormatList.add("EEE MMM d HH:mm:ss z yyyy");
        availableDateFormatList.add("yyyy-MM-dd'T'HH:mm:ssXXX");
        availableDateFormatList.add("MMM dd yyyy HH:mm:ss");
        availableDateFormatList.add("MM/dd/yy HH:mm:ss");
        availableDateFormatList.add("MMM  dd HH:mm:ss yyyy");
        availableDateFormatList.add("MMM dd HH:mm:ss yyyy");
        availableDateFormatList.add("yyyy MMM  dd HH:mm:ss");
        availableDateFormatList.add("MM/dd/yyyy:HH:mm:ss");
        availableDateFormatList.add("MMM dd yyyy  HH:mm:ss");
        availableDateFormatList.add("yyyy-MM-dd'T'HH:mm:ss.SSS");
        availableDateFormatList.add("yyyy-MM-dd'T'HH:mm:ss");
        availableDateFormatList.add("MM/dd/yyyy h:mm a");
        availableDateFormatList.add("MM/d/yyyy H:mm");
        availableDateFormatList.add("yyyy-MM-dd HH:mm:ss");
        availableDateFormatList.add("yyyy MMM dd HH:mm:ss");
        availableDateFormatList.add("EEE MMM dd HH:mm:ss yyyy");
        availableDateFormatList.add("yyyy MMM d HH:mm:ss");
    }

    public static List<String> getAvailableInputFormats() {
        // TODO get list from properties file
        return availableDateFormatList;
    }
}

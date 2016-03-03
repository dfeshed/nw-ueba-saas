package fortscale.collection.services.time;

import java.util.LinkedList;

/**
 * @author gils
 *02/03/2016
 */
public class FortscaleDateTimeFormats {

    private static final String UNIX_TIME_IN_SECONDS = "unixTimeInSeconds";
    private static final String UNIX_TIME_IN_MILLIS = "unixTimeInMillis";

    private static LinkedList<String> availableInputFormatList = new LinkedList<>();

    static {
        // a suite of default date formats
        availableInputFormatList.add("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        availableInputFormatList.add("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        availableInputFormatList.add("yyyyMMddHHmmss'.0Z'");
        availableInputFormatList.add("MM/dd/yyyy HH:mm:ss z");
        availableInputFormatList.add("MM/dd/yyyy HH:mm:ss");
        availableInputFormatList.add("yyyy/MM/dd HH:mm:ss");
        availableInputFormatList.add("EEE MMM d HH:mm:ss z yyyy");
        availableInputFormatList.add("yyyy-MM-dd'T'HH:mm:ssXXX");
        availableInputFormatList.add("MMM dd yyyy HH:mm:ss");
        availableInputFormatList.add("MM/dd/yy HH:mm:ss");
        availableInputFormatList.add("MMM  dd HH:mm:ss yyyy");
        availableInputFormatList.add("MMM dd HH:mm:ss yyyy");
        availableInputFormatList.add("yyyy MMM  dd HH:mm:ss");
        availableInputFormatList.add("MM/dd/yyyy:HH:mm:ss");
        availableInputFormatList.add("MMM dd yyyy  HH:mm:ss");
        availableInputFormatList.add("yyyy-MM-dd'T'HH:mm:ss.SSS");
        availableInputFormatList.add("yyyy-MM-dd'T'HH:mm:ss");
        availableInputFormatList.add("MM/dd/yyyy h:mm a");
        availableInputFormatList.add("MM/d/yyyy H:mm");
        availableInputFormatList.add("yyyy-MM-dd HH:mm:ss");
        availableInputFormatList.add("yyyy MMM dd HH:mm:ss");
        availableInputFormatList.add("EEE MMM dd HH:mm:ss yyyy");
        availableInputFormatList.add("yyyy MMM d HH:mm:ss");
        availableInputFormatList.add(UNIX_TIME_IN_SECONDS);
        availableInputFormatList.add(UNIX_TIME_IN_MILLIS);
    }

    public static LinkedList<String> getAvailableInputFormats() {
        return availableInputFormatList;
    };
}

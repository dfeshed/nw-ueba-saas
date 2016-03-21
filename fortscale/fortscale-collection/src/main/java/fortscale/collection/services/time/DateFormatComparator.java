package fortscale.collection.services.time;

import java.util.Comparator;

/**
 * @author gils
 * 07/03/2016
 */
public class DateFormatComparator implements Comparator<String> {

    @Override
    public int compare(String dateFormat1, String dateFormat2) {
        if (dateFormat1.endsWith("Z") || dateFormat1.endsWith("z")) {
            if (dateFormat2.endsWith("Z") || dateFormat2.endsWith("z")) {
                return dateFormat1.compareTo(dateFormat2);
            }

            return 1;
        }

        if (dateFormat2.endsWith("Z") || dateFormat2.endsWith("z")) {
            return -1;
        }
        else {
            return dateFormat1.compareTo(dateFormat2);
        }
    }
}

package fortscale.utils.cleanup;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Amir Keren on 25/09/15.
 */
public class CleanupPredicate {

    @Value("${prefix.flag}")
    private String prefixFlag;
    @Value("${contains.flag}")
    private String containsFlag;

    /***
     *
     * This method is the filter method for the predicate
     *
     * @param name    string to check
     * @param value   value to check whether to filter out or not
     * @param filter  the filter type
     * @return
     */
    public boolean apply(String name, String value, String filter) {
        if (filter.equals(prefixFlag)) {
            return name.startsWith(value);
        } else if (filter.equals(containsFlag)) {
            return name.contains(value);
        } else {
            //by default, don't filter anything
            return true;
        }
    }

}
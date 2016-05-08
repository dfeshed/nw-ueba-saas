package fortscale.utils.cleanup;

import fortscale.utils.logging.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Amir Keren on 25/09/15.
 */
public class CleanupPredicate {

    private static Logger logger = Logger.getLogger(CleanupPredicate.class);

    private enum Flag { PREFIX, SUFFIX, CONTAINS, REGEX, ALL }

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
        Flag flag = Flag.ALL;
        try {
            flag = Flag.valueOf(filter.toUpperCase());
        } catch (Exception ex) {
            logger.warn("no filter {} found, defaulting to ALL", filter.toUpperCase());
        }
        switch (flag) {
            case PREFIX: {
                return name.startsWith(value);
            }
            case SUFFIX: {
                return name.endsWith(value);
            }
            case REGEX: {
                Pattern pattern = Pattern.compile(value);
                Matcher matcher = pattern.matcher(name);
                return matcher.matches();
            }
            case CONTAINS: {
                return name.contains(value);
            } case ALL:
                //by default, don't filter anything
              default : {
                return true;
            }
        }
    }

}
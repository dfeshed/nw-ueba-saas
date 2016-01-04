package fortscale.collection.jobs.gds.helper;

/**
 * Helper class for user input methods
 *
 * @author gils
 * 31/12/2015
 */
public class GDSUserInputHelper {
    private static final String Y_CHAR = "y";
    private static final String YES_STR = "yes";

    public static boolean isConfirmed(String input) {
        return input.toLowerCase().equals(Y_CHAR) || input.toLowerCase().equals(YES_STR);
    }
}

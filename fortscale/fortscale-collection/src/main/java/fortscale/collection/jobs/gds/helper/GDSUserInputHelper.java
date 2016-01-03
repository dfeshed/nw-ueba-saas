package fortscale.collection.jobs.gds.helper;

/**
 * @author gils
 * 31/12/2015
 */
public class GDSUserInputHelper {
    public static boolean isConfirmed(String input) {
        return input.toLowerCase().equals("y") || input.toLowerCase().equals("yes");
    }
}

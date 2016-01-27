package fortscale.utils.prettifiers;

/**
 * Created by avivs on 20/01/16.
 */
public class BytesPrettifier {

    /**
     * Converts a string representing bytes into pretty bytes.
     * @param bytesString string representing bytes
     * @param precision The number of decimal digits
     * @return prettifies bytes string. For example 23.24 MB
     */
    public static String prettify (String bytesString, int precision) {

        long bytes;

        // If bytes string can not be converted to Long, then return the bytesString value as is.
        try {
            bytes = Long.parseLong(bytesString);
        } catch (NumberFormatException e) {
            return bytesString;
        }

        // Possible units
        String[] units = {"bytes", "KB", "MB", "GB", "TB", "PB"};

        // Calculates which unit should be used in this case
        int unitIndex = (int)Math.floor(Math.log(bytes) / Math.log(1024));

        // Calculates the value of pretty bytes. For example 1048 should be 1.0234375
        double prettyBytes = (bytes / Math.pow(1024, unitIndex));
        // Set precision. For example 1.0234375 with precision of 2 should be 1.02
        prettyBytes = Math.floor(prettyBytes*Math.pow(10, precision))/Math.pow(10, precision);

        // Convert to pretty string. for example 1.02 should be 1.02 KB
        bytesString = String.valueOf(prettyBytes) + " " + units[unitIndex];
        return bytesString;
    }

    public static String prettify (String bytesString) {
        return prettify(bytesString, 2);
    }

    public static String ratePrettify (String bytesString, int precision, String rateUnit) {
        return prettify(bytesString,precision) + "/" + rateUnit;
    }

    public static String ratePrettify (String bytesString, String rateUnit) {
        return prettify(bytesString) + "/" + rateUnit;
    }

    public static String ratePrettify (String bytesString, int precision) {
        return ratePrettify(bytesString,precision, "s");
    }

    public static String ratePrettify (String bytesString) {
        return ratePrettify(bytesString, "s");
    }
}

package fortscale.monitoring.external.stats.Util;


public class CollectorsUtil {

    /**
     * converts Integer/Double/Object to long
     *
     * @param entry entry to convert
     * @return long value of entry
     */
    public static long entryValueToLong(Object entry) {
        long result;
        if (entry.getClass().isAssignableFrom(Integer.class)) {
            result = ((Integer) entry).longValue();
        } else if (entry.getClass().isAssignableFrom(Double.class)) {
            result = ((Double) entry).longValue();
        } else if (entry.getClass().isAssignableFrom(Boolean.class)) {
            if ((boolean) entry) {
                result = 1;
            } else {
                result = 0;
            }
        } else {
            result = (Long) entry;
        }
        return result;
    }
}

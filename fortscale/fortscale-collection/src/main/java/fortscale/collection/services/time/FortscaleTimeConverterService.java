package fortscale.collection.services.time;

import java.util.List;

/**
 * @author gils
 * 02/03/2016
 */
public interface FortscaleTimeConverterService {
    /**
     *
     * @param timestamp
     * @param outputFormatStr
     * @param outputTimezone
     * @return
     */
    String convertTimestamp(String timestamp, String outputFormatStr, String outputTimezone);

    /**
     *
     * @param timestamp
     * @param inputTimezone
     * @param outputFormatStr
     * @param outputTimezone
     * @return
     */
    String convertTimestamp(String timestamp, String inputTimezone, String outputFormatStr, String outputTimezone);

    /**
     *
     * @param timestamp
     * @param optionalInputFormats
     * @param inputTimezone
     * @param outputFormatStr
     * @param outputTimezone
     * @return
     */
    String convertTimestamp(String timestamp, List<String> optionalInputFormats, String inputTimezone, String outputFormatStr, String outputTimezone);
}

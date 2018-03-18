package fortscale.domain.historical.data;

import fortscale.utils.time.TimeUtils;

/**
 * Supporting information Timestamp key. Same as the generic key but with specific toString method
 *
 * @author gils
 * Date: 16/09/2015
 */
public class SupportingInformationTimestampKey extends SupportingInformationSingleKey {
    public SupportingInformationTimestampKey(String key) {
        super(key);
    }

    public SupportingInformationTimestampKey(String key, String uniqueIdentifier) {
        super(key, uniqueIdentifier);
    }

    @Override
    public String toString() {
        return "SupportingInformationSingleKey{" +
                "key='" + TimeUtils.getFormattedTime(Long.parseLong(key)) + '\'' +
                ", keyIdentifier='" + keyIdentifier + '\'' +
                '}';
    }
}

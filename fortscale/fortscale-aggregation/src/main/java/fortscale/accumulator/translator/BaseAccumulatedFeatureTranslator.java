package fortscale.accumulator.translator;

import org.apache.commons.lang3.StringUtils;

/**
 * translates feature name to accumulated collection name
 * Created by barak_schuster on 10/6/16.
 */
public abstract class BaseAccumulatedFeatureTranslator {

    public static final String DAILY_COLLECTION_SUFFIX = "daily";
    private static final String DAILY_COLLECTION_REGEX = String.format("%s$", DAILY_COLLECTION_SUFFIX);
    public static final String HOURLY_COLLECTION_SUFFIX = "hourly";
    private static final String HOURLY_COLLECTION_REGEX = String.format("%s$", HOURLY_COLLECTION_SUFFIX);
    private static final String NAME_DELIMITER = "_";
    public static final String ACCUMULATED_COLLECTION_SUFFIX = String.format("%sacm", NAME_DELIMITER);
    private static final String ACCUMULATED_DAILY_COLLECTION_SUFFIX = String.format("d%s", ACCUMULATED_COLLECTION_SUFFIX);
    private static final String ACCUMULATED_HOURLY_COLLECTION_SUFFIX = String.format("h%s", ACCUMULATED_COLLECTION_SUFFIX);

    /**
     *
     * {@return mongodb collection name by convention dedicated for accumulated collections} for {@param featureName}
     *
     */
    public abstract String toAcmCollectionName(String featureName);

    /**
     * @return regular expression used to find all collections with that convention
     */
    public abstract String getAcmCollectionNameRegex();

    protected String getAcmCollectionName(String originalCollectionName) {
        String collectionName;
        if (originalCollectionName.endsWith(DAILY_COLLECTION_SUFFIX)) {
            collectionName =
                    originalCollectionName.replaceAll(DAILY_COLLECTION_REGEX, ACCUMULATED_DAILY_COLLECTION_SUFFIX);
        } else if (originalCollectionName.endsWith(HOURLY_COLLECTION_SUFFIX)) {
            collectionName =
                    originalCollectionName.replaceAll(HOURLY_COLLECTION_REGEX, ACCUMULATED_HOURLY_COLLECTION_SUFFIX);
        } else {
            collectionName =
                    StringUtils.join(originalCollectionName, NAME_DELIMITER, ACCUMULATED_COLLECTION_SUFFIX);
        }
        return collectionName;
    }
}

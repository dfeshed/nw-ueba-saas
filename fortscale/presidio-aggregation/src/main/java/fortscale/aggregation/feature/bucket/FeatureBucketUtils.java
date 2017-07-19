package fortscale.aggregation.feature.bucket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import presidio.ade.domain.record.AdeRecordReader;

public class FeatureBucketUtils {
	private static final Logger logger = Logger.getLogger(FeatureBucketUtils.class);
	private static final String CONTEXT_ID_SEPARATOR = "###";
	private static final String BUCKET_ID_BUILDER_SEPARATOR = "###";
	
	
	public static String buildContextId(Map<String, String> context) {
		List<Map.Entry<String, String>> listOfEntries = new ArrayList<>(context.entrySet());
		Collections.sort(listOfEntries, new Comparator<Map.Entry<String, String>>() {
			@Override
			public int compare(Map.Entry<String, String> entry1, Map.Entry<String, String> entry2) {
				return entry1.getKey().compareTo(entry2.getKey());
			}
		});

		List<String> listOfPairs = new ArrayList<>();
		for (Map.Entry<String, String> entry : listOfEntries) {
			listOfPairs.add(String.format("%s%s%s", entry.getKey(), CONTEXT_ID_SEPARATOR, entry.getValue()));
		}

		return StringUtils.join(listOfPairs, CONTEXT_ID_SEPARATOR);
	}

	/**
	 * Generate bucket id.
	 * The bucket id consist: strategyId , contextFieldNames, contextFieldName value.
	 *
	 * @param adeRecordReader
	 * @param featureBucketConf e.g: normalized_user_name
	 * @param strategyId        e.g: fixed_duration_hourly
	 * @return bucket id
	 */
	public static String buildBucketId(AdeRecordReader adeRecordReader, FeatureBucketConf featureBucketConf, String strategyId){
		List<String> sorted = new ArrayList<>(featureBucketConf.getContextFieldNames());
		Collections.sort(sorted);
		StringBuilder builder = new StringBuilder();
		builder.append(strategyId);

		for (String contextFieldName : sorted) {
			builder.append(BUCKET_ID_BUILDER_SEPARATOR);
			String contextValue = adeRecordReader.get(contextFieldName, String.class);

			// Return null as the bucket ID if one of the contexts is missing
			if (StringUtils.isBlank(contextValue)) {
				logger.debug("The {} value is missing.", contextFieldName);
				return null;
			}
			builder.append(contextFieldName).append(BUCKET_ID_BUILDER_SEPARATOR).append(contextValue);

			builder.append(BUCKET_ID_BUILDER_SEPARATOR);
			builder.append(BUCKET_ID_BUILDER_SEPARATOR).append(featureBucketConf.getName());
		}

		return builder.toString();
	}
}

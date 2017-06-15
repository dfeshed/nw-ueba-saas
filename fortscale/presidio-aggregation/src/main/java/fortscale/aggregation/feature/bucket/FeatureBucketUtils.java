package fortscale.aggregation.feature.bucket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class FeatureBucketUtils {
	private static final String CONTEXT_ID_SEPARATOR = "###";
	
	
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
}

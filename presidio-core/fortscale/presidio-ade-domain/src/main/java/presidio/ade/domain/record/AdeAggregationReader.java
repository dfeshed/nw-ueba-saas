package presidio.ade.domain.record;

import fortscale.utils.recordreader.transformation.Transformation;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * A record reader for {@link AdeRecord}s.
 *
 */
public class AdeAggregationReader extends AdeRecordReader {
	private static final String CONTEXT_FIELD = "context";

	/**
	 * C'tor.
	 *
	 * @param adeAggregationRecord          the record from which values are extracted
	 * @param transformations    a map containing the transformations that are used when fields are missing
	 */
	public AdeAggregationReader(
			@NotNull AdeAggregationRecord adeAggregationRecord,
			@NotNull Map<String, Transformation<?>> transformations) {

		super(adeAggregationRecord, transformations);
	}


	/**
     *
	 * @param contextFieldName context field name
	 * @return context value
	 */
	public String getContext(String contextFieldName) {
		return get(new String[]{CONTEXT_FIELD, contextFieldName}, String.class);
	}

	@Override
	protected <T> T getFeature(String fieldPath, Class<T> fieldClass) {
		String[] contextPathArray;
		if (fieldPath.startsWith(CONTEXT_FIELD)) {
			contextPathArray = new String[]{ CONTEXT_FIELD, fieldPath.substring(CONTEXT_FIELD.length() + 1)};
		}
		else {
			contextPathArray = new String[] { fieldPath};
		}
		return get(contextPathArray, fieldClass);
	}
}

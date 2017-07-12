package presidio.ade.domain.record;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.feature.FeatureValue;
import fortscale.utils.recordreader.ReflectionRecordReader;
import fortscale.utils.recordreader.transformation.Transformation;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map;

/**
 * A record reader for {@link AdeRecord}s.
 *
 * Created by Lior Govrin on 19/06/2017.
 */
public class AdeRecordReader extends ReflectionRecordReader {
	private AdeRecord adeRecord;

	/**
	 * C'tor.
	 *
	 * @param adeRecord          the record from which values are extracted
	 * @param transformations    a map containing the transformations that are used when fields are missing
	 * @param fieldPathDelimiter this reader's field path delimiter (evaluated as a regular expression)
	 */
	public AdeRecordReader(
			@NotNull AdeRecord adeRecord,
			@NotNull Map<String, Transformation<?>> transformations,
			@NotNull String fieldPathDelimiter) {

		super(adeRecord, transformations, fieldPathDelimiter);
		this.adeRecord = adeRecord;
	}

	/**
	 * C'tor.
	 * There are no transformations configured.
	 * The default field path delimiter is used.
	 *
	 * @param adeRecord the record from which values are extracted
	 */
	public AdeRecordReader(@NotNull AdeRecord adeRecord) {
		super(adeRecord);
		this.adeRecord = adeRecord;
	}

	/**
	 * C'tor.
	 * The default field path delimiter is used.
	 *
	 * @param adeRecord       the record from which values are extracted
	 * @param transformations a map containing the transformations that are used when fields are missing
	 */
	public AdeRecordReader(@NotNull AdeRecord adeRecord, @NotNull Map<String, Transformation<?>> transformations) {
		super(adeRecord, transformations);
		this.adeRecord = adeRecord;
	}

	/**
	 * C'tor.
	 * There are no transformations configured.
	 *
	 * @param adeRecord          the record from which values are extracted
	 * @param fieldPathDelimiter this reader's field path delimiter (evaluated as a regular expression)
	 */
	public AdeRecordReader(@NotNull AdeRecord adeRecord, @NotNull String fieldPathDelimiter) {
		super(adeRecord, fieldPathDelimiter);
		this.adeRecord = adeRecord;
	}

	public String getAdeRecordType() {
		return adeRecord.getAdeRecordType();
	}

	public String getId() {
		return adeRecord.getId();
	}

	public Instant getCreated_date() {
		return adeRecord.getCreatedDate();
	}

	public Instant getDate_time() {
		return adeRecord.getStartInstant();
	}

	public AdeRecord getAdeRecord(){
		return this.adeRecord;
	}

	public String getDataSource(){
		return adeRecord.getDataSource();
	}

	/**
	 * Create map of feature name and feature
	 * @param featureNames set of feature names
	 * @return feature name to feature map
	 */
	public Map<String, Feature> getAllFeatures(Set<String> featureNames) {
		Map<String, Feature> featureMap = new HashMap<>();

		for (String featureName : featureNames) {
			Object featureValue = get(featureName, Object.class);
			Feature feature = Feature.toFeature(featureName, featureValue);
			featureMap.put(featureName, feature);
		}
		return featureMap;
	}

}

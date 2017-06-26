package presidio.ade.domain.record;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.feature.FeatureValue;
import fortscale.utils.recordreader.ReflectionRecordReader;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
	 * @param adeRecord          the ADE record from which values are extracted
	 * @param fieldPathDelimiter this ADE record reader's field path delimiter (evaluated as a regular expression)
	 */
	public AdeRecordReader(@NotNull AdeRecord adeRecord, @NotNull String fieldPathDelimiter) {
		super(adeRecord, fieldPathDelimiter);
		this.adeRecord = adeRecord;
	}

	/**
	 * Default c'tor (default field path delimiter is used).
	 *
	 * @param adeRecord the ADE record from which values are extracted
	 */
	public AdeRecordReader(@NotNull AdeRecord adeRecord) {
		super(adeRecord);
		this.adeRecord = adeRecord;
	}

	public String getAdeRecordType() {
		return adeRecord.getAdeRecordType();
	}

	public String getId() {
		return adeRecord.getId();
	}

	public Instant getCreated_date() {
		return adeRecord.getCreated_date();
	}

	public Instant getDate_time() {
		return adeRecord.getDate_time();
	}

	public AdeRecord getAdeRecord(){
		return this.adeRecord;
	}

	public String getDataSource(){
		return adeRecord.getDataSource();
	}

	public String getStringValue(String fieldPath){
		return get(fieldPath, String.class);
	}

	/**
	 * Create map of feature name and feature
	 * @param featureNames set of feature names
	 * @return feature name to feature map
	 */
	public Map<String, Feature> getAllFeatures(Set<String> featureNames) {
		Map<String, Feature> featureMap = new HashMap<>();

		for (String featureName : featureNames) {
			Double featureValue = get(featureName, Double.class);
			Feature feature = toFeature(featureName, featureValue);
			featureMap.put(featureName, feature);
		}
		return featureMap;
	}

	/**
	 * Create feature of feature name and feature value.
	 *
	 * @param name  feature name
	 * @param value feature value
	 * @return Feature
	 */
	private static Feature toFeature(String name, Double value) {
		FeatureValue featureValue = new FeatureNumericValue((value));
		return new Feature(name, featureValue);
	}
}

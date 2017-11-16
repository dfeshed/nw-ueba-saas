package presidio.ade.domain.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.feature.Feature;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.recordreader.ReflectionRecordReader;
import fortscale.utils.recordreader.transformation.Transformation;
import net.minidev.json.JSONObject;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A record reader for {@link AdeRecord}s.
 *
 * Created by Lior Govrin on 19/06/2017.
 */
public class AdeRecordReader extends ReflectionRecordReader {
	private ObjectMapper objectMapper = ObjectMapperProvider.getInstance().getDefaultObjectMapper();
	private AdeRecord adeRecord;
	private JSONObject adeRecordJson;

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

	public String getAdeEventType() {
		return adeRecord.getAdeEventType();
	}

	public List<String> getDataSources(){
		return adeRecord.getDataSources();
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

	public JSONObject getAdeRecordAsJsonObject() {
		if (adeRecordJson == null) {
			adeRecordJson = objectMapper.convertValue(adeRecord, JSONObject.class);
		}
		return adeRecordJson;
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

	/**
	 *
	 * @param contextFieldName context field name
	 * @return context value
	 */
	public String getContext(String contextFieldName){
		return get(contextFieldName, String.class);
	}

}

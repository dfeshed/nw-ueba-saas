package fortscale.utils.recordreader.transformation;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * An epochtime transformation takes an {@link Instant} and transforms it into its epochtime representation
 * ({@link Long}), but keeps the seconds within the configured resolution. For example, if the instant is
 * "2017-01-01T00:02:30.000Z" (epochtime = 1483228950) and the resolution is 2 minutes (120 seconds), the
 * output will be 1483228920 (instant = "2017-01-01T00:02:00.000Z").
 *
 * Created by Lior Govrin on 22/06/2017.
 */
public class EpochtimeTransformation implements Transformation<Long> {
	private String featureName;
	private String instantFieldName;
	private long resolutionInSeconds;
	private Collection<String> requiredFieldNames;

	/**
	 * C'tor.
	 *
	 * @param featureName         the name of the new epochtime feature
	 * @param instantFieldName    the name of the required instant field
	 * @param resolutionInSeconds the resolution in seconds of the new epochtime feature
	 */
	public EpochtimeTransformation(String featureName, String instantFieldName, long resolutionInSeconds) {
		if (resolutionInSeconds <= 0) {
			throw new IllegalArgumentException("resolutionInSeconds must be greater than zero.");
		}

		this.featureName = featureName;
		this.instantFieldName = instantFieldName;
		this.resolutionInSeconds = resolutionInSeconds;
		this.requiredFieldNames = Collections.singleton(instantFieldName);
	}

	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public Collection<String> getRequiredFieldNames() {
		return requiredFieldNames;
	}

	@Override
	public Long transform(Map<String, Object> requiredFieldNameToValueMap) {
		Instant instant = (Instant)requiredFieldNameToValueMap.get(instantFieldName);
		return (instant.getEpochSecond() / resolutionInSeconds) * resolutionInSeconds;
	}
}

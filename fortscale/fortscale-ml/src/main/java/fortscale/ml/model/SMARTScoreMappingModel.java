package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(
		fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class SMARTScoreMappingModel extends ScoreMappingModel {
	static final double EPSILON = 0.000000000001;

	public void init(double threshold, double maximalScore) {
		Map<Double, Double> mapping = new HashMap<>();
		mapping.put(maximalScore, 100D);
		mapping.put(threshold, 50D);
		mapping.put(threshold - EPSILON, 49D);
		super.init(mapping);
	}
	/**
	 *
	 * @return ToString you know...
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}

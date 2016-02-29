package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class SMARTThresholdModel extends ScoreMappingModel {
	public void init(double threshold, double maxSeenScore) {
		Map<Double, Double> mapping = new HashMap<>();
		mapping.put(threshold, 50D);
		mapping.put(maxSeenScore, 100D);
		super.init(mapping);
	}
}

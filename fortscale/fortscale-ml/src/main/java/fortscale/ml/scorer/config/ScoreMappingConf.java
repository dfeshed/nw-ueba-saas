package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.NONE)
public class ScoreMappingConf {
	private Map<Double, Double> mapping;

	public ScoreMappingConf() {
		setMapping(new HashMap<>());
	}

	public Map<Double, Double> getMapping() {
		return mapping;
	}

	public void setMapping(Map<Double, Double> mapping) {
		Assert.notNull(mapping);
		mapping.put(0D, mapping.getOrDefault(0D, 0D));
		mapping.put(100D, mapping.getOrDefault(100D, 100D));
		Assert.isTrue(isMonotonic(mapping));
		this.mapping = mapping;
	}

	private boolean isMonotonic(Map<Double, Double> mapping) {
		final double[] last = {-Double.MAX_VALUE};
		return mapping.entrySet().stream()
				.sorted((e1, e2) -> (int) Math.signum(e1.getKey() - e2.getKey()))
				.map(Map.Entry::getValue)
				.allMatch(x -> {
					boolean isMonotonic = x >= last[0];
					last[0] = x;
					return isMonotonic;
				});
	}
}

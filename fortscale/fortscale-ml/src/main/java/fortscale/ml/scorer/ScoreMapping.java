package fortscale.ml.scorer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ScoreMapping {
	@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE)
	public static class ScoreMappingConf {
		private Map<Double, Double> mapping;

		public ScoreMappingConf() {
			setMapping(new HashMap<>());
		}

		public Map<Double, Double> getMapping() {
			return mapping;
		}

		public ScoreMappingConf setMapping(Map<Double, Double> mapping) {
			Assert.notNull(mapping);
			mapping.put(0D, mapping.getOrDefault(0D, 0D));
			mapping.put(100D, mapping.getOrDefault(100D, 100D));
			Assert.isTrue(isMonotonic(mapping));
			this.mapping = mapping;
			return this;
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

	private ScoreMapping() {
	}

	public static double mapScore(double score, ScoreMappingConf scoreMappingConf) {
		List<ImmutablePair<Double, Double>> sortedMappingPoints = scoreMappingConf.getMapping().entrySet().stream()
				.sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
				.map(e -> new ImmutablePair<>(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
		for (int i = 0; i < sortedMappingPoints.size(); i++) {
			if (sortedMappingPoints.get(i).getKey() >= score) {
				if (i == 0) {
					return sortedMappingPoints.get(i).getValue();
				}
				Map.Entry<Double, Double> before = sortedMappingPoints.get(i - 1);
				Map.Entry<Double, Double> after = sortedMappingPoints.get(i);
				double ratio = (score - before.getKey()) / (after.getKey() - before.getKey());
				return before.getValue() + (after.getValue() - before.getValue()) * ratio;
			}
		}
		throw new RuntimeException("shouldn't get here. There's a bug somewhere. Good luck!");
	}
}

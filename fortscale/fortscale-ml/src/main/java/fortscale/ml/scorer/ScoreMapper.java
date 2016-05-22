package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.domain.core.FeatureScore;
import fortscale.ml.scorer.config.ScoreMappingConf;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ScoreMapper extends AbstractScorer {
	private List<Pair<Double, Double>> sortedMappingPoints;
	private Scorer baseScorer;

	public ScoreMapper(String name, Scorer baseScorer, ScoreMappingConf scoreMappingConf) {
		super(name);
		Assert.notNull(baseScorer);
		Assert.notNull(scoreMappingConf);
		this.baseScorer = baseScorer;
		sortedMappingPoints = scoreMappingConf.getMapping().entrySet().stream()
				.sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
				.map(e -> new ImmutablePair<>(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}

	@Override
	public FeatureScore calculateScore(Event eventMessage, long eventEpochtime) throws Exception {
		FeatureScore baseScore = baseScorer.calculateScore(eventMessage, eventEpochtime);
		double mappedScore = mapScore(baseScore.getScore());
		return new FeatureScore(getName(), mappedScore, Collections.singletonList(baseScore));
	}

	private double mapScore(double score) {
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

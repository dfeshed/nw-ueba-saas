package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.common.feature.Feature;
import fortscale.domain.core.FeatureScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configurable(preConstruction = true)
public class AggregatedFeatureEventUnreducedScoreRetriever extends AbstractDataRetriever {
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

	private AggregatedFeatureEventUnreducedScoreRetrieverConf config;
	private AggregatedFeatureEventConf aggregatedFeatureEventToCalibrateConf;

	public AggregatedFeatureEventUnreducedScoreRetriever(AggregatedFeatureEventUnreducedScoreRetrieverConf config) {
		super(config);
		this.config = config;
		String aggregatedFeatureEventConfNameToCalibrate = config.getAggregatedFeatureEventToCalibrateConfName();
		aggregatedFeatureEventToCalibrateConf = aggregatedFeatureEventsConfService
				.getAggregatedFeatureEventConf(aggregatedFeatureEventConfNameToCalibrate);
		Assert.notNull(aggregatedFeatureEventToCalibrateConf);
	}

	@Override
	public Map<Long, List<Double>> retrieve(String contextId, Date endTime) {
		Assert.isNull(contextId, String.format("%s can't be used with a context", this.getClass().getSimpleName()));
		Map<Long, List<AggrEvent>> dateToTopAggrEvents = aggregatedFeatureEventsReaderService.getDateToTopAggrEvents(
				aggregatedFeatureEventToCalibrateConf,
				endTime,
				config.getNumOfDays(),
				config.getNumOfIndicatorsPerDay());

		return dateToTopAggrEvents.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()
				.stream()
				.map(aggrEvent -> findScoreToCalibrate(aggrEvent.getFeatureScores(), config.getScoreNameToCalibrate()))
				.filter(unreducedScore -> unreducedScore != null)
				.collect(Collectors.toList())));
	}

	private Stream<FeatureScore> flattenFeatureScoresRecursively(List<FeatureScore> featureScores) {
		return featureScores.stream()
				.flatMap(this::flattenFeatureScoresRecursively);
	}

	private Stream<FeatureScore> flattenFeatureScoresRecursively(FeatureScore featureScore) {
		return Stream.concat(
				Stream.of(featureScore),
				flattenFeatureScoresRecursively(featureScore.getFeatureScores())
		);
	}

	private Double findScoreToCalibrate(List<FeatureScore> featureScores, String scoreNameToCalibrate) {
		return flattenFeatureScoresRecursively(featureScores)
				.filter(featureScore -> featureScore.getName().equals(scoreNameToCalibrate))
				.findFirst()
				.map(FeatureScore::getScore)
				.orElse(null);
	}

	@Override
	public Object retrieve(String contextId, Date endTime, Feature feature) {
		throw new UnsupportedOperationException(String.format(
				"%s does not support retrieval of a single feature",
				getClass().getSimpleName()));
	}

	@Override
	public Set<String> getEventFeatureNames() {
//		throw new UnsupportedOperationException("the model created by this retriever is used for calibrating " +
//				"scores. As such, it is composed upon another scorer, so this function should never be called");
		return Collections.singleton("TODO: once analytics_infra_improvements is merged into master, throw an exception"); //TODO: s
	}

	@Override
	public List<String> getContextFieldNames() {
		return aggregatedFeatureEventToCalibrateConf.getBucketConf().getContextFieldNames();
	}

	@Override
	public String getContextId(Map<String, String> context) {
		Assert.notEmpty(context);
		return AggrFeatureEventBuilderService.getAggregatedFeatureContextId(context);
	}
}

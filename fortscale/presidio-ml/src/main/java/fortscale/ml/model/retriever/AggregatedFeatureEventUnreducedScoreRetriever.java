package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.common.feature.Feature;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.ModelBuilderData;
import org.springframework.util.Assert;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;

import java.util.*;
import java.util.stream.Stream;


public class AggregatedFeatureEventUnreducedScoreRetriever extends AbstractDataRetriever {

	private AggregatedFeatureEventUnreducedScoreRetrieverConf config;
	private AggregatedFeatureEventConf aggregatedFeatureEventToCalibrateConf;

	public AggregatedFeatureEventUnreducedScoreRetriever(AggregatedFeatureEventUnreducedScoreRetrieverConf config,
														 AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService) {
		super(config);
		this.config = config;

		String aggregatedFeatureEventConfNameToCalibrate = config.getAggregatedFeatureEventToCalibrateConfName();
		aggregatedFeatureEventToCalibrateConf = aggregatedFeatureEventsConfService
				.getAggregatedFeatureEventConf(aggregatedFeatureEventConfNameToCalibrate);
		Assert.notNull(aggregatedFeatureEventToCalibrateConf);
	}

	@Override
	public ModelBuilderData retrieve(String contextId, Date endTime) {
		Assert.isNull(contextId, String.format("%s can't be used with a context", this.getClass().getSimpleName()));
		// todo: the following query assumes long term persistence of aggr records. this should be replaced to be retrieved from accumulated data.
//		Map<Long, List<AggrEvent>> dateToTopAggrEvents = aggregatedFeatureEventsReaderService.getDateToTopAggrEvents(
//				aggregatedFeatureEventToCalibrateConf,
//				endTime,
//				config.getNumOfDays(),
//				config.getNumOfIndicatorsPerDay());
//
//		if (dateToTopAggrEvents.isEmpty()) {
//			return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);
//		}
//
//		Map<Long, List<Double>> data = dateToTopAggrEvents.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()
//				.stream()
//				.map(aggrEvent -> findScoreToCalibrate(aggrEvent.getFeatureScores(), config.getScoreNameToCalibrate()))
//				.filter(unreducedScore -> unreducedScore != null)
//				.collect(Collectors.toList())));
//
//		if (data.isEmpty()) {
//			return new ModelBuilderData(NoDataReason.ALL_DATA_FILTERED);
//		} else {
//			return new ModelBuilderData(data);
//		}
		return null;
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
	public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
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
		return AdeContextualAggregatedRecord.getAggregatedFeatureContextId(context);
	}
}

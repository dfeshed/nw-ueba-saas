package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.common.feature.Feature;
import fortscale.domain.core.FeatureScore;
import fortscale.ml.model.PersonalThresholdModelBuilderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Configurable(preConstruction = true)
public class AggregatedFeaturePersonalThresholdModelBuilderDataRetriever extends AbstractDataRetriever {
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
	@Autowired
	private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

	private AggregatedFeatureEventConf aggregatedFeatureEventConfToCalibrate;
	private int desiredNumberOfIndicators;
	private String scoreNameToCalibrate;

	public AggregatedFeaturePersonalThresholdModelBuilderDataRetriever(AggregatedFeaturePersonalThresholdModelBuilderDataRetrieverConf config) {
		super(config);
		String aggregatedFeatureEventConfNameToCalibrate = config.getAggregatedFeatureEventConfNameToCalibrate();
		aggregatedFeatureEventConfToCalibrate = aggregatedFeatureEventsConfService
				.getAggregatedFeatureEventConf(aggregatedFeatureEventConfNameToCalibrate);
		desiredNumberOfIndicators = config.getDesiredNumberOfIndicators();
		scoreNameToCalibrate = config.getScoreNameToCalibrate();
	}

	@Override
	public PersonalThresholdModelBuilderData retrieve(String contextId, Date endTime) {
		Date startTime = getStartTime(endTime);
		int numOfContexts = aggregatedFeatureEventsReaderService.findDistinctContextsByTimeRange(
				aggregatedFeatureEventConfToCalibrate,
				startTime,
				endTime
		).size();
		long numOfOrganizationScores = aggregatedFeatureEventsReaderService.findNumOfAggrEventsByTimeRange(
				aggregatedFeatureEventConfToCalibrate,
				startTime,
				endTime
		);
		List<FeatureScore> featureScores = aggregatedFeatureEventsReaderService.findAggrEventWithTopKScore(
				aggregatedFeatureEventConfToCalibrate,
				startTime,
				endTime,
				desiredNumberOfIndicators
		).getFeatureScores();
		double scoreToCalibrate = findScoreToCalibrate(featureScores, scoreNameToCalibrate);

		return new PersonalThresholdModelBuilderData()
				.setNumOfContexts(numOfContexts)
				.setNumOfOrganizationScores(numOfOrganizationScores)
				.setOrganizationKTopProbOfHighScore(scoreToCalibrate);
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

	private double findScoreToCalibrate(List<FeatureScore> featureScores, String scoreNameToCalibrate) {
		return flattenFeatureScoresRecursively(featureScores)
				.filter(featureScore -> featureScore.getName().equals(scoreNameToCalibrate))
				.findFirst()
				.map(FeatureScore::getScore)
				.get();
	}

	@Override
	public Object retrieve(String contextId, Date endTime, Feature feature) {
		throw new UnsupportedOperationException(String.format(
				"%s does not support retrieval of a single feature",
				getClass().getSimpleName()));
	}

	@Override
	public Set<String> getEventFeatureNames() {
		throw new UnsupportedOperationException("the model created by this retriever is used for calibrating " +
				"scores. As such, it is composed upon another scorer, so this function should never be called");
	}

	@Override
	public List<String> getContextFieldNames() {
		return aggregatedFeatureEventConfToCalibrate.getBucketConf().getContextFieldNames();
	}

	@Override
	public String getContextId(Map<String, String> context) {
		Assert.notEmpty(context);
		return AggrFeatureEventBuilderService.getAggregatedFeatureContextId(context);
	}
}

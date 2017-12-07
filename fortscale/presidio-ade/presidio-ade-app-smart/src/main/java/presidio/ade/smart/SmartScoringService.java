package presidio.ade.smart;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.ScoringService;
import fortscale.utils.logging.Logger;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.aggregated.SmartAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm.CONTRIBUTIONS_FEATURE_SCORE_NAME;
import static fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm.SCORE_AND_WEIGHT_PRODUCTS_FEATURE_SCORE_NAME;

/**
 * This service uses a generic {@link ScoringService} to score {@link SmartRecord}s. For each smart record,
 * the service validates the output list of feature scores and updates the smart value and score accordingly.
 *
 * @author Lior Govrin
 */
public class SmartScoringService {
	private static final Logger logger = Logger.getLogger(SmartScoringService.class);

	private final RecordReaderFactoryService recordReaderFactoryService;
	private final ScoringService scoringService;

	/**
	 * C'tor.
	 *
	 * @param recordReaderFactoryService a {@link RecordReaderFactoryService}
	 * @param scoringService             a generic {@link ScoringService}
	 */
	public SmartScoringService(RecordReaderFactoryService recordReaderFactoryService, ScoringService scoringService) {
		this.recordReaderFactoryService = recordReaderFactoryService;
		this.scoringService = scoringService;
	}

	/**
	 * Score each of the smart records in the given collection. If a smart record's list
	 * of feature scores is not as expected, the smart value and score will not be changed.
	 *
	 * @param smartRecords the smart records that are scored
	 */
	public void score(Collection<SmartRecord> smartRecords) {
		logger.debug("Going to calculate the value and score of {} smart records.", smartRecords.size());
		smartRecords.forEach(this::score);
	}

	/**
	 * Reset model cache.
	 */
	public void resetModelCache() {
		scoringService.resetModelCache();
	}

	private void score(SmartRecord smartRecord) {
		AdeRecordReader adeRecordReader = (AdeRecordReader)recordReaderFactoryService.getRecordReader(smartRecord);
		List<FeatureScore> levelOneFeatureScores = scoringService.score(adeRecordReader);

		if (levelOneFeatureScores.size() == 1) {
			FeatureScore smartScore = levelOneFeatureScores.get(0);
			List<FeatureScore> levelTwoFeatureScores = smartScore.getFeatureScores();

			if (levelTwoFeatureScores.size() == 1) {
				FeatureScore smartValue = levelTwoFeatureScores.get(0);
				smartRecord.setSmartValue(smartValue.getScore());
				smartRecord.setScore(smartScore.getScore());
				smartRecord.setFeatureScoreList(levelOneFeatureScores);
				addAdditionalInfo(smartRecord, extractAdditionalInfo(smartValue));
			} else {
				logger.error(
						"A smart record's second level list of feature scores should contain only one " +
						"feature score - The alphas and betas score, which is the smart value itself. " +
						"Smart record = {}, feature scores = {}.", smartRecord, levelTwoFeatureScores);
			}
		} else {
			logger.error(
					"A smart record's first level list of feature scores should contain " +
					"only one feature score - The smart value score. Smart record = {}, " +
					"feature scores = {}.", smartRecord, levelOneFeatureScores);
		}
	}

	private static Map<String, List<FeatureScore>> extractAdditionalInfo(FeatureScore smartValue) {
		List<FeatureScore> featureScores = smartValue.getFeatureScores();
		Map<String, List<FeatureScore>> additionalInfo = featureScores.stream()
				.collect(Collectors.toMap(FeatureScore::getName, FeatureScore::getFeatureScores));
		featureScores.clear();
		return additionalInfo;
	}

	private static void addAdditionalInfo(SmartRecord smartRecord, Map<String, List<FeatureScore>> additionalInfo) {
		Map<String, Double> contributions = toMap(additionalInfo.get(CONTRIBUTIONS_FEATURE_SCORE_NAME));
		Map<String, Double> scoreAndWeightProducts = toMap(additionalInfo.get(SCORE_AND_WEIGHT_PRODUCTS_FEATURE_SCORE_NAME));

		for (SmartAggregationRecord smartAggregationRecord : smartRecord.getSmartAggregationRecords()) {
			String featureName = smartAggregationRecord.getAggregationRecord().getFeatureName();
			smartAggregationRecord.setContribution(contributions.get(featureName));
			smartAggregationRecord.setScoreAndWeightProduct(scoreAndWeightProducts.get(featureName));
		}
	}

	private static Map<String, Double> toMap(List<FeatureScore> list) {
		return list.stream().collect(Collectors.toMap(FeatureScore::getName, FeatureScore::getScore));
	}
}

package presidio.ade.smart;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.ScoringService;
import fortscale.utils.logging.Logger;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

	private void score(SmartRecord smartRecord) {
		AdeRecordReader adeRecordReader = (AdeRecordReader)recordReaderFactoryService.getRecordReader(smartRecord);
		// List<FeatureScore> levelOneFeatureScores = scoringService.score(adeRecordReader);
		// TODO: Remove following code and uncomment previous one once alphas and betas model is implemented
		Random random = new Random();
		List<FeatureScore> levelOneFeatureScores = Collections.singletonList(
				new FeatureScore("score", (double)random.nextInt(101), Collections.singletonList(
				new FeatureScore("value", 1.0 / (random.nextInt(100) + 1), Collections.emptyList()))));

		if (levelOneFeatureScores.size() == 1) {
			FeatureScore smartScore = levelOneFeatureScores.get(0);
			List<FeatureScore> levelTwoFeatureScores = smartScore.getFeatureScores();

			if (levelTwoFeatureScores.size() == 1) {
				FeatureScore smartValue = levelTwoFeatureScores.get(0);
				smartRecord.setSmartValue(smartValue.getScore());
				smartRecord.setScore(smartScore.getScore());
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
}

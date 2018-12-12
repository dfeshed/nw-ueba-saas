package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.*;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.joiner.PartitionsDataModelJoiner;
import fortscale.ml.model.store.ModelDAO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class JoinGaussianModelScorerTest {
    private JoinGaussianModelScorer createScorer(List<String> additionalModelNames, int globalInfluence, String modelName,
                                                 String secondaryModelName, List<String> contextFieldNames, List<String> secondaryContextFieldNames,
                                                 List<String> additionalContextFieldNames, EventModelsCacheService eventModelsCacheService) {
        return new JoinGaussianModelScorer(
                "scorerName",
                modelName,
                secondaryModelName,
                additionalModelNames,
                contextFieldNames,
                secondaryContextFieldNames,
                additionalModelNames.stream()
                        .map(additionalModelName -> additionalContextFieldNames)
                        .collect(Collectors.toList()),
                "featureValue",
                1,
                1,
                false,
                globalInfluence,
                eventModelsCacheService,
                86400,
                new PartitionsDataModelJoiner(20, 86400, 2,1), 90
        );
    }


    @Test
    public void test() {
        String modelName = "modelName";
        String secondaryModelName = "secondaryScorerName";
        String additionalModelName = "additionalModelName";
        List<String> secondaryContextFieldNames = Collections.singletonList("userId");
        List<String> contextFieldNames = new ArrayList<>();
        contextFieldNames.add("userId");
        contextFieldNames.add("process");
        EventModelsCacheService eventModelsCacheService = mock(EventModelsCacheService.class);
        JoinGaussianModelScorer scorer = createScorer(Collections.singletonList(additionalModelName), 0, modelName,
                secondaryModelName, contextFieldNames, secondaryContextFieldNames, contextFieldNames, eventModelsCacheService);


        AdeRecordReader adeRecordReader = createAdeRecordReader();
        Instant startInstant = Instant.parse("2018-07-01T00:00:00Z");
        Instant endInstant = startInstant.plus(Duration.ofDays(4));
        Model model = createModel(startInstant, endInstant, 5, Duration.ofHours(1), 3600, "userId#testUser#process#testProcess", Duration.ofHours(6));
        when(eventModelsCacheService.getLatestModelBeforeEventTime(adeRecordReader, modelName, contextFieldNames)).thenReturn(model);
        Model secondaryModel = createModel(startInstant, endInstant, 5, Duration.ofHours(1), 3600 * 2, "userId#testUser1", Duration.ofHours(3));
        when(eventModelsCacheService.getLatestModelBeforeEventTime(adeRecordReader, secondaryModelName, secondaryContextFieldNames)).thenReturn(secondaryModel);
        Model additionalModel = createGaussianPriorModel();
        when(eventModelsCacheService.getLatestModelBeforeEventTime(adeRecordReader, additionalModelName, contextFieldNames)).thenReturn(additionalModel);

        FeatureScore featureScore = scorer.calculateScore(adeRecordReader);
        Assert.assertTrue(featureScore.getScore() > 0);
    }

    /**
     * @return GaussianPriorModel
     */
    private GaussianPriorModel createGaussianPriorModel() {
        GaussianPriorModel gaussianPriorModel = new GaussianPriorModel();
        GaussianPriorModel.SegmentPrior segmentPrior = new GaussianPriorModel.SegmentPrior();
        segmentPrior.init(1, 2, 0, 0);
        gaussianPriorModel.init(Collections.singletonList(segmentPrior));
        return gaussianPriorModel;
    }

    /**
     * Create model
     *
     * @param startInstant        startInstant
     * @param endInstant          endInstant
     * @param numOfPartitions     numOfPartitions
     * @param instantStep         instantStep
     * @param resolutionInSeconds resolutionInSeconds
     * @return Collection<ModelDAO>
     */
    private Model createModel(Instant startInstant, Instant endInstant, long numOfPartitions, Duration instantStep, long resolutionInSeconds, String contextId, Duration duration) {
        List<ModelDAO> modelDaoList = new ArrayList<>();
        Map<Long, Double> instantToValue = new HashMap<>();
        Instant start = startInstant;
        while (start.isBefore(endInstant)) {
            instantToValue.put(start.getEpochSecond(), 1D);
            start = start.plus(duration);
        }
        return new PartitionsDataModel(instantToValue, resolutionInSeconds, instantStep, numOfPartitions);
    }

    /**
     * @return AdeRecordReader
     */
    private AdeRecordReader createAdeRecordReader() {
        AdeAggregationRecord featureAdeAggrRecord = new AdeAggregationRecord();
        featureAdeAggrRecord.setFeatureValue(40D);
        return new AdeRecordReader(featureAdeAggrRecord);
    }
}

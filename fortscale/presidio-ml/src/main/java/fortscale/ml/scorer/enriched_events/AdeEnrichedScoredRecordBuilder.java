package fortscale.ml.scorer.enriched_events;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.util.AdeEnrichedRecordToAdeScoredEnrichedRecordResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.List;

/**
 * Created by YaronDL on 6/18/2017.
 */
public class AdeEnrichedScoredRecordBuilder {
    private static final Logger logger = Logger.getLogger(AdeEnrichedScoredRecordBuilder.class);

    private AdeEnrichedRecordToAdeScoredEnrichedRecordResolver adeEnrichedRecordToAdeScoredEnrichedRecordResolver;

    public AdeEnrichedScoredRecordBuilder(AdeEnrichedRecordToAdeScoredEnrichedRecordResolver adeEnrichedRecordToAdeScoredEnrichedRecordResolver){
        this.adeEnrichedRecordToAdeScoredEnrichedRecordResolver = adeEnrichedRecordToAdeScoredEnrichedRecordResolver;
    }

    public void fill(List<AdeScoredEnrichedRecord> scoredRecordList, EnrichedRecord enrichedRecord, List<FeatureScore> featureScoreList){

        if(featureScoreList.size() == 0){
            //TODO: add metrics.
            logger.error("after calculating an enriched record we got an empty feature score list while expecting for a list of size 1. the enrich record: {}", enrichedRecord);
            return;
        }

        //expect to get as a root feature score which hold the event score and inside it all the relevant features.
        if(featureScoreList.size() > 1){
            //TODO: add metrics.
            logger.error("after calculating an enriched record we got feature score list of size > 1 while expecting to get a root which hold the event score and inside it all the relevant features. the enrich record: {}", enrichedRecord);
            return;
        }

        FeatureScore eventScore = featureScoreList.get(0);

        if(eventScore.getFeatureScores().size() == 0){
            //TODO: add metrics.
            logger.error("after calculating an enriched record we got an empty feature score list!!! the enrich record: {}", enrichedRecord);
            return;
        }

        for (FeatureScore featureScore : eventScore.getFeatureScores()) {
            AdeScoredEnrichedRecord scoredRecord = buildAdeEnrichedScoredRecord(enrichedRecord, featureScore);
            if(scoredRecord != null) {
                scoredRecordList.add(scoredRecord);
            }
        }
    }

    private AdeScoredEnrichedRecord buildAdeEnrichedScoredRecord(EnrichedRecord enrichedRecord, FeatureScore featureScore) {
        AdeScoredEnrichedRecord ret = null;
        Class<? extends AdeScoredEnrichedRecord> pojoClass = adeEnrichedRecordToAdeScoredEnrichedRecordResolver.getClass(enrichedRecord.getClass());
        Assert.notNull(pojoClass ,String.format("did not found matching scored record class for enriched record: %s",enrichedRecord));
        try {
            Constructor<? extends AdeScoredEnrichedRecord> constructor = pojoClass.getConstructor(Instant.class, String.class, String.class, Double.class, List.class, EnrichedRecord.class);
            String featureName = featureScore.getName();
            ret = constructor.newInstance(enrichedRecord.getStartInstant(), featureName, enrichedRecord.getAdeEventType(), featureScore.getScore(), featureScore.getFeatureScores(), enrichedRecord);
            ret.fillContext(enrichedRecord);
        } catch (NoSuchMethodException e) {
            //TODO: ADD metrics
            logger.error("got an exception while trying to buildAdeEnrichedScoredRecord from {}, {} The exception: {}", enrichedRecord, featureScore, e);
        } catch (IllegalAccessException e) {
            //TODO: ADD metrics
            logger.error("got an exception while trying to buildAdeEnrichedScoredRecord from {}, {} The exception: {}", enrichedRecord, featureScore, e);
        } catch (InstantiationException e) {
            //TODO: ADD metrics
            logger.error("got an exception while trying to buildAdeEnrichedScoredRecord from {}, {} The exception: {}", enrichedRecord, featureScore, e);
        } catch (InvocationTargetException e) {
            //TODO: ADD metrics
            logger.error("got an exception while trying to buildAdeEnrichedScoredRecord from {}, {} The exception: {}", enrichedRecord, featureScore, e);
        }

        return ret;
    }
}

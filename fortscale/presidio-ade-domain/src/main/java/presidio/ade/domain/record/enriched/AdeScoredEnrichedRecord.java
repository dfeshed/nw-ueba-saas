package presidio.ade.domain.record.enriched;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.mongodb.index.DynamicIndexing;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeScoredRecord;

import java.time.Instant;
import java.util.List;

/**
 * @author Yaron DL
 */
@DynamicIndexing(compoundIndexes = {
        @CompoundIndex(name = "startScr", def = "{'startInstant': 1, 'score': 1}"),
        @CompoundIndex(name = "eventId", def = "{'context.eventId': 1}")
})
public abstract class AdeScoredEnrichedRecord<U extends BaseEnrichedContext> extends AdeRecord implements AdeScoredRecord {
    public static final String EVENT_TYPE_PREFIX = "scored_enriched";
    public static final String CONTEXT_FIELD_NAME = "context";
    public static final String SCORE_FIELD_NAME = "score";

    private String featureName;
    private String featureEventType;
    @Field(SCORE_FIELD_NAME)
    private Double score;
    private List<FeatureScore> featureScoreList;
    @Field(CONTEXT_FIELD_NAME)
    private U context;

    public AdeScoredEnrichedRecord() {
        super();
    }

    public AdeScoredEnrichedRecord(Instant startInstant, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList, EnrichedRecord enrichedRecord) {
        super(startInstant);
        this.featureName = featureName;
        this.featureEventType = featureEventType;
        this.score = score;
        this.featureScoreList = featureScoreList;
        fillContext(enrichedRecord);
    }

    @Override
    public String getAdeEventType() {
        return EVENT_TYPE_PREFIX + "." + getFeatureEventType() + "." + getFeatureName();
    }

    public abstract void fillContext(EnrichedRecord enrichedRecord);

    public U getContext() {
        return context;
    }

    public void setContext(U context) {
        this.context = context;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureEventType() {
        return featureEventType;
    }

    public void setFeatureEventType(String featureEventType) {
        this.featureEventType = featureEventType;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public List<FeatureScore> getFeatureScoreList() {
        return featureScoreList;
    }

    public void setFeatureScoreList(List<FeatureScore> featureScoreList) {
        this.featureScoreList = featureScoreList;
    }
}

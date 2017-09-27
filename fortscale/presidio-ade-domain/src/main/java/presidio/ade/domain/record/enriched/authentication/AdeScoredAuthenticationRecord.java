package presidio.ade.domain.record.enriched.authentication;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeScoredEnrichedMetadata;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * @author Barak Schuster
 */
@Document
@AdeScoredEnrichedMetadata(erichedRecord = EnrichedAuthenticationRecord.class)
public class AdeScoredAuthenticationRecord extends AdeScoredEnrichedRecord<AdeEnrichedAuthenticationContext> {
    public AdeScoredAuthenticationRecord() {
        super();
    }

    public AdeScoredAuthenticationRecord(Instant startInstant, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList, EnrichedRecord enrichedRecord) {
        super(startInstant, featureName, featureEventType, score, featureScoreList, enrichedRecord);
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList(AdeAuthenticationRecord.AUTHENTICATION_STR);
    }

    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        EnrichedAuthenticationRecord enrichedFileRecord = (EnrichedAuthenticationRecord) enrichedRecord;
        setContext(enrichedFileRecord.getContext());
    }
}

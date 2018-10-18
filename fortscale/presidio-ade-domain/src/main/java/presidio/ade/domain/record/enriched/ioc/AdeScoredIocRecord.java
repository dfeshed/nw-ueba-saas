package presidio.ade.domain.record.enriched.ioc;

import fortscale.common.general.Schema;
import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeScoredEnrichedMetadata;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Document
@AdeScoredEnrichedMetadata(enrichedRecord = EnrichedIocRecord.class)
public class AdeScoredIocRecord extends AdeScoredEnrichedRecord<AdeEnrichedIocContext> {

    public AdeScoredIocRecord() {
        super();
    }

    public AdeScoredIocRecord(Instant startInstant, String featureName, String featureEventType, Double score, List<FeatureScore> featureScoreList, EnrichedRecord enrichedRecord) {
        super(startInstant, featureName, featureEventType, score, featureScoreList, enrichedRecord);
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList(Schema.IOC.getName());
    }

    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        EnrichedIocRecord enrichedIocRecord = (EnrichedIocRecord) enrichedRecord;
        setContext(enrichedIocRecord.getContext());
    }
}

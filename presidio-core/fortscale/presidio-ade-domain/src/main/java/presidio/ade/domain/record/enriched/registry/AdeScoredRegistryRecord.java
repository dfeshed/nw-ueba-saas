package presidio.ade.domain.record.enriched.registry;

import fortscale.common.general.Schema;
import fortscale.domain.feature.score.FeatureScore;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeScoredEnrichedMetadata;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * The scored enriched Registry record POJO.
 */
@Document
@AdeScoredEnrichedMetadata(enrichedRecord = EnrichedRegistryRecord.class)
public class AdeScoredRegistryRecord extends AdeScoredEnrichedRecord<AdeEnrichedRegistryContext> {
    public AdeScoredRegistryRecord() {
        super();
    }

    public AdeScoredRegistryRecord(
            Instant startInstant,
            String featureName,
            String featureEventType,
            Double score,
            List<FeatureScore> featureScoreList,
            EnrichedRecord enrichedRecord) {

        super(startInstant, featureName, featureEventType, score, featureScoreList, enrichedRecord);
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList(Schema.REGISTRY.getName());
    }

    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        EnrichedRegistryRecord enrichedRegistryRecord = (EnrichedRegistryRecord)enrichedRecord;
        setContext(enrichedRegistryRecord.getContext());
    }
}

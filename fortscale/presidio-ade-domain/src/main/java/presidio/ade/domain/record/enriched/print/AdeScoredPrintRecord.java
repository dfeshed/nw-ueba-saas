package presidio.ade.domain.record.enriched.print;

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
 * The scored enriched Print record POJO.
 *
 * @author Lior Govrin
 */
@Document
@AdeScoredEnrichedMetadata(enrichedRecord = EnrichedPrintRecord.class)
public class AdeScoredPrintRecord extends AdeScoredEnrichedRecord<AdeEnrichedPrintContext> {
    public AdeScoredPrintRecord() {
        super();
    }

    public AdeScoredPrintRecord(
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
        return Collections.singletonList(Schema.PRINT.getName());
    }

    @Override
    public void fillContext(EnrichedRecord enrichedRecord) {
        EnrichedPrintRecord enrichedPrintRecord = (EnrichedPrintRecord)enrichedRecord;
        setContext(enrichedPrintRecord.getContext());
    }
}

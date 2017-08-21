package presidio.ade.domain.record.enriched.authentication;

import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeScoredEnrichedMetadata;

import java.util.Collections;
import java.util.List;

/**
 * Created by barak_schuster on 8/21/17.
 */
@Document
@AdeScoredEnrichedMetadata(erichedRecord = EnrichedAuthenticationRecord.class)
public class AdeScoredAuthenticationRecord extends AdeScoredEnrichedRecord<AdeEnrichedAuthenticationContext> {
    public AdeScoredAuthenticationRecord() {
        super();
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

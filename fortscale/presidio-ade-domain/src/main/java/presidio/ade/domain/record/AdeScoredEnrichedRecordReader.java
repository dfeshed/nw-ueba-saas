package presidio.ade.domain.record;

import fortscale.utils.recordreader.transformation.Transformation;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Created by presidio on 8/22/17.
 */
public class AdeScoredEnrichedRecordReader extends AdeRecordReader {

    private static final String CONTEXT_FIELD = "context";

    /**
     * C'tor.
     *
     * @param adeScoredEnrichedRecord          the record from which values are extracted
     * @param transformations    a map containing the transformations that are used when fields are missing
     */
    public AdeScoredEnrichedRecordReader(
            @NotNull AdeScoredEnrichedRecord adeScoredEnrichedRecord,
            @NotNull Map<String, Transformation<?>> transformations) {

        super(adeScoredEnrichedRecord, transformations);
    }


    /**
     *
     * @param contextFieldName context field name
     * @return context value
     */
    public String getContext(String contextFieldName){
        return get(CONTEXT_FIELD + "." + contextFieldName, String.class);
    }

}

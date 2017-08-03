package presidio.output.domain.translator;

import fortscale.common.general.Schema;
import fortscale.utils.mongodb.util.ToCollectionNameTranslator;

/**
 * Translator from enriched data to collection name.
 * <p>
 * Created by barak_schuster on 5/18/17.
 */
public class OutputToCollectionNameTranslator implements ToCollectionNameTranslator<Schema> {
    private static final String ENRICHED_COLLECTION_PREFIX = "output_enriched_events_";

    @Override
    public String toCollectionName(Schema schema) {
        return String.format(ENRICHED_COLLECTION_PREFIX + "%s", schema);
    }

}

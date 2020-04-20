package presidio.output.domain.translator;

import fortscale.common.general.Schema;
import fortscale.utils.mongodb.util.ToCollectionNameTranslator;

/**
 * Translator from enriched data to collection name.
 * <p>
 * Created by barak_schuster on 5/18/17.
 */
public class OutputToCollectionNameTranslator implements ToCollectionNameTranslator<Schema> {
    private static final String COMPONENT_NAME = "output_";
    private static final String COLLECTION_DESCRIPTION = "_enriched_events";

    @Override
    public String toCollectionName(Schema schema) {
        return String.format("%s%s%s" , COMPONENT_NAME ,schema.toString().toLowerCase(),COLLECTION_DESCRIPTION);
    }

}

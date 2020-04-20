package presidio.sdk.api.utils;

import fortscale.common.general.Schema;
import fortscale.utils.mongodb.util.ToCollectionNameTranslator;


public class InputToCollectionNameTranslator implements ToCollectionNameTranslator<Schema> {

    @Override
    public String toCollectionName(Schema schema) {
        return String.format("input_%s_raw_events", schema.getName());
    }
}

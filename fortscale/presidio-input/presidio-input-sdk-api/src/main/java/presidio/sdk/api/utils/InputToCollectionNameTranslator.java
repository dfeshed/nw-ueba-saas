package presidio.sdk.api.utils;

import fortscale.common.general.PresidioSchemas;
import fortscale.utils.mongodb.util.ToCollectionNameTranslator;


public class InputToCollectionNameTranslator implements ToCollectionNameTranslator<PresidioSchemas>{

    @Override
    public String toCollectionName(PresidioSchemas presidioSchemas) {
        return String.format("input_%s_raw_events", presidioSchemas.getName());
    }
}

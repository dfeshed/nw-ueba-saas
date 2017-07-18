package presidio.sdk.api.utils;

import fortscale.common.general.DataSource;
import fortscale.utils.mongodb.util.ToCollectionNameTranslator;


public class InputToCollectionNameTranslator implements ToCollectionNameTranslator<DataSource>{

    @Override
    public String toCollectionName(DataSource dataSource) {
        return String.format("input_%s_raw_events", dataSource.getName());
    }
}

package presidio.sdk.api.domain;

import fortscale.common.general.DataSource;
import fortscale.utils.mongodb.util.ToCollectionNameTranslator;


public class InputToCollectionNameTranslator implements ToCollectionNameTranslator<DataSource>{

    @Override
    public String toCollectionName(DataSource dataSource) {
        return String.format("%s_stored_data", dataSource.getName());
    }
}

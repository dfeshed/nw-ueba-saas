package com.rsa.netwitness.presidio.automation.domain.store;

import fortscale.common.general.Schema;

public class CollectionNameTranslator {

    public String toCollectionName(Schema schema) {
        return String.format("netwitness_%s_events", schema.getName());
    }
}

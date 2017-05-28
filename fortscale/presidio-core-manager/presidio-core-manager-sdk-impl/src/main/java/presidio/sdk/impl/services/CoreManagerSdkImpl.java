package presidio.sdk.impl.services;


import fortscale.common.general.Datasource;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.sdk.api.services.CoreManagerSdk;
import presidio.sdk.api.services.PresidioInputSdk;

import java.util.List;

public class CoreManagerSdkImpl implements CoreManagerSdk {

    private static final Logger logger = Logger.getLogger(CoreManagerSdkImpl.class);

    private PresidioInputSdk presidioInputSdk;

    @Autowired
    public CoreManagerSdkImpl(PresidioInputSdk presidioInputSdk) {
        this.presidioInputSdk = presidioInputSdk;
    }

    @Override
    public boolean store(Datasource dataSource, List<AbstractAuditableDocument> records) {
        logger.info("Core manager has received a request to store {} records", records.size());
        //todo: metrics
        return presidioInputSdk.store(dataSource, records);
    }


}

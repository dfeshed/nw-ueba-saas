package presidio.sdk.impl.services;


import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.sdk.api.services.CoreManagerSdk;
import presidio.sdk.api.services.PresidioInputSdk;

import java.util.List;

public class CoreManagerSdkImpl implements CoreManagerSdk {


    private PresidioInputSdk presidioInputSdk;

    @Autowired
    public CoreManagerSdkImpl(PresidioInputSdk presidioInputSdk) {
        this.presidioInputSdk = presidioInputSdk;
    }

    @Override
    public boolean store(List<AbstractAuditableDocument> events) {
        return presidioInputSdk.store(events);
    }

}

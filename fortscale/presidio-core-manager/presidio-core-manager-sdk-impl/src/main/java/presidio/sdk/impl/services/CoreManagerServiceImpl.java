package presidio.sdk.impl.services;


import org.springframework.beans.factory.annotation.Autowired;
import presidio.sdk.api.domain.AbstractRecordDocument;
import presidio.sdk.api.services.CoreManagerService;
import presidio.sdk.api.services.PresidioInputSdk;

import java.util.List;

public class CoreManagerServiceImpl implements CoreManagerService {


    private PresidioInputSdk presidioInputSdk;

    @Autowired
    public CoreManagerServiceImpl(PresidioInputSdk presidioInputSdk) {
        this.presidioInputSdk = presidioInputSdk;
    }

    @Override
    public boolean store(List<AbstractRecordDocument> events) {
        return presidioInputSdk.store(events);
    }

}

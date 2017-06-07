package presidio.sdk.impl.services;


import fortscale.common.general.DataSource;
import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.sdk.api.services.CoreManagerService;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.util.List;

public class CoreManagerServiceImpl implements CoreManagerService {


    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Autowired
    public CoreManagerServiceImpl(PresidioInputPersistencyService presidioInputPersistencyService) {
        this.presidioInputPersistencyService = presidioInputPersistencyService;
    }

    @Override
    public boolean store(DataSource dataSource, List<AbstractAuditableDocument> records) {
        return presidioInputPersistencyService.store(dataSource, records);
    }

}

package presidio.sdk.impl.services;


import org.springframework.beans.factory.annotation.Autowired;
import presidio.sdk.api.domain.AbsEventRecord;
import presidio.sdk.api.services.CoreManagerService;
import presidio.sdk.api.services.InputProcessService;

/**
 * Created by shays on 17/05/2017.
 */

public class CoreManagerServiceImpl implements CoreManagerService {

    @Autowired
    private InputProcessService inputProcessService;

    @Override
    public boolean store(AbsEventRecord event) {
        return inputProcessService.store(event);
    }
}

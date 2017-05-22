package fortscale.services.impl;

import fortscale.domain.AbsEventRecord;
import fortscale.services.InputProcessService;

import fortscale.services.api.CoreManagerService;
import org.springframework.beans.factory.annotation.Autowired;

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

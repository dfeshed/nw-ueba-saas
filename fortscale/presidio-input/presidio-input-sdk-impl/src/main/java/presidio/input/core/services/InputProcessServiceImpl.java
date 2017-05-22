package presidio.input.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.sdk.api.domain.AbsEventRecord;
import presidio.sdk.api.services.InputProcessService;

/**
 * Created by shays on 17/05/2017.
 */

public class InputProcessServiceImpl implements InputProcessService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean store(AbsEventRecord event) {
        logger.info("Record Stored");
        return false;
    }
}

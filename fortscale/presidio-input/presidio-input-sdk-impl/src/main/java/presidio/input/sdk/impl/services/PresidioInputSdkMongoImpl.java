package presidio.input.sdk.impl.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.sdk.api.domain.AbstractRecordDocument;
import presidio.sdk.api.services.PresidioInputSdk;

import java.util.List;

/**
 * Created by shays on 17/05/2017.
 */

public class PresidioInputSdkMongoImpl implements PresidioInputSdk {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean store(List<AbstractRecordDocument> event) {
        logger.info("Record Stored");
        return false;
    }
}

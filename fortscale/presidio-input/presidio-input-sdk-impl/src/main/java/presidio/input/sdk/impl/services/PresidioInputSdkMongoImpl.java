package presidio.input.sdk.impl.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.sdk.api.domain.AbstractRecordDocument;
import presidio.sdk.api.services.PresidioInputSdk;

import java.util.List;

public class PresidioInputSdkMongoImpl implements PresidioInputSdk {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean store(List<AbstractRecordDocument> event) {
        int i = 1;
        for (AbstractRecordDocument abstractRecordDocument : event) {
            System.out.println(abstractRecordDocument);
            System.out.println(i);
            System.out.println();
            i++;
        }
        logger.info("Record Stored");
        return true;
    }
}

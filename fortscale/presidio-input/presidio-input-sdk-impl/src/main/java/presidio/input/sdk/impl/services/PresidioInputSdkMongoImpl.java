package presidio.input.sdk.impl.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.sdk.api.domain.AbstractRecordDocument;
import presidio.sdk.api.services.PresidioInputSdk;

import java.util.List;

public class PresidioInputSdkMongoImpl implements PresidioInputSdk {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean store(List<AbstractRecordDocument> events) {
        //TODO: change this when we have the new service and repo
        int i = 1;
        System.out.println();
        for (AbstractRecordDocument abstractRecordDocument : events) {
            System.out.println(i + ": - " + abstractRecordDocument);
            i++;
        }
        logger.info(events.size() + " Records stored");
        return true;
    }
}

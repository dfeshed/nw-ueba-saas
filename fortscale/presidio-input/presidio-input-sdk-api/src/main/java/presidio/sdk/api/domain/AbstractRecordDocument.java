package presidio.sdk.api.domain;

import java.util.Date;

public class AbstractRecordDocument {
    protected Date dateTime;
    protected long dateTimeUnix;

    public AbstractRecordDocument(Date dateTime, long dateTimeUnix) {
        this.dateTime = dateTime;
        this.dateTimeUnix = dateTimeUnix;
    }
}

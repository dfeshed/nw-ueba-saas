package presidio.ade.domain.store.input;

import fortscale.utils.mongodb.index.DynamicallyIndexed;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * basic ade input record. and inserted record should inherit this entity
 * Created by barak_schuster on 5/18/17.
 */
@Document
public class ADEInputRecord {
    public static final String EVENT_TIME_FIELD = "eventTime";

    @Id
    protected String id;
    @CreatedDate
    protected Instant creationTime;
    @Field(EVENT_TIME_FIELD)
    @DynamicallyIndexed
    protected Instant eventTime;

    public ADEInputRecord(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public String getId() {
        return id;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }
}

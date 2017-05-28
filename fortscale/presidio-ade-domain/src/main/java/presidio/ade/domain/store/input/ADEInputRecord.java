package presidio.ade.domain.store.input;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.co.jemos.podam.common.PodamStrategyValue;


import java.time.Instant;
import java.util.Map;

/**
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
    protected Instant eventTime;

    public ADEInputRecord(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public Instant getEventTime() {
        return eventTime;
    }
}

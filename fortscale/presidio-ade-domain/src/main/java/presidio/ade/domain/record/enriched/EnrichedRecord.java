package presidio.ade.domain.record.enriched;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.AdeRecord;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * A basic ADE enriched record. All ADE enriched records (across all data sources) should extend this one.
 * <p>
 * Created by Lior Govrin on 06/06/2017.
 */
@Document
public abstract class EnrichedRecord extends AdeRecord {

    public static final String EVENT_ID_FIELD = "eventId";
    public static final String DATA_SOURCE_FIELD = "dataSource";

    @Field(EVENT_ID_FIELD)
    private String eventId;
    @Field(DATA_SOURCE_FIELD)
    private String dataSource;


    public EnrichedRecord(Instant startInstant) {
        super(startInstant);
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @Transient
    public List<String> getDataSources() {
        return Collections.singletonList(getDataSource());
    }
}

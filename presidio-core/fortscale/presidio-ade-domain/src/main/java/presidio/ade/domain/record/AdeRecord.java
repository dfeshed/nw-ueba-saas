package presidio.ade.domain.record;

import fortscale.utils.mongodb.index.DynamicIndexing;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

/**
 * A basic ADE record. All ADE related records (enriched and scored) should extend this one.
 * <p>
 * Created by Lior Govrin on 06/06/2017.
 */
@DynamicIndexing(compoundIndexes = {
        @CompoundIndex(name = "start", def = "{'startInstant': 1}")
})
public abstract class AdeRecord {
    public static final String START_INSTANT_FIELD = "startInstant";

    @Id
    private String id;
    @CreatedDate
    private Instant createdDate;
    @Field(START_INSTANT_FIELD)
    private Instant startInstant;

    public AdeRecord() {
    }

    public AdeRecord(Instant startInstant) {
        this.startInstant = startInstant;
    }

    /**
     * @return a string representation of the event type
     */
    public abstract String getAdeEventType();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public void setStartInstant(Instant startInstant) {
        this.startInstant = startInstant;
    }

    public abstract List<String> getDataSources();
}

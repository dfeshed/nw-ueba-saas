package fortscale.utils.store;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * StoreManagerRecordTest record used for StoreManagerTest.
 */
public class StoreManagerRecordTest {

    public final static String END_FIELD = "end";
    public final static String START_FIELD = "start";

    @Field(START_FIELD)
    private Instant start;
    @Field(END_FIELD)
    private Instant end;
    @Field
    private String name;

    public StoreManagerRecordTest(String name, Instant start, Instant end) {
        this.start = start;
        this.end = end;
        this.name = name;
    }

    public Instant getStart() {
        return start;
    }
}

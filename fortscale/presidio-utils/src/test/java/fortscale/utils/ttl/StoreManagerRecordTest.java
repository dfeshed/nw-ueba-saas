package fortscale.utils.ttl;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * StoreManagerRecordTest record used for StoreManagerTest.
 */
public class StoreManagerRecordTest {

    public final static String END_FIELD = "end";

    @Field
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
}

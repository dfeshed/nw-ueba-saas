package fortscale.utils.ttl;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * TtlServiceRecordTest record used for TtlServiceTest.
 */
public class TtlServiceRecordTest {

    public final static String END_FIELD = "end";

    @Field
    private Instant start;
    @Field(END_FIELD)
    private Instant end;
    @Field
    private String name;

    public TtlServiceRecordTest(String name, Instant start, Instant end) {
        this.start = start;
        this.end = end;
        this.name = name;
    }
}

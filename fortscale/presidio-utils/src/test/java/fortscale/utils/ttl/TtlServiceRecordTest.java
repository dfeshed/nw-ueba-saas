package fortscale.utils.ttl;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * TtlServiceRecordTest record used for TtlServiceTest.
 */
public class TtlServiceRecordTest {

    public final static String START_FIELD = "start";

    @Field(START_FIELD)
    private Instant start;
    @Field
    private Instant end;
    @Field
    private String name;

    public TtlServiceRecordTest(String name, Instant start, Instant end) {
        this.start = start;
        this.end = end;
        this.name = name;
    }
}

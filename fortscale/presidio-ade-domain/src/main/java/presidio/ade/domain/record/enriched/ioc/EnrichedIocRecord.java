package presidio.ade.domain.record.enriched.ioc;

import fortscale.common.general.Schema;
import fortscale.domain.core.Level;
import fortscale.domain.core.Tactic;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;


@Document
@AdeRecordMetadata(adeEventType = Schema.IOC)
public class EnrichedIocRecord extends EnrichedRecord {
    public static final String USER_ID_FIELD = "userId";
    public static final String MACHINE_ID_FIELD = "machineId";
    public static final String NAME_FIELD = "name";
    public static final String TACTIC_FIELD = "tactic";
    public static final String LEVEL_FIELD = "level";

    @Field(USER_ID_FIELD)
    private String userId;
    @Field(MACHINE_ID_FIELD)
    private String machineId;
    @Field(NAME_FIELD)
    private String name;
    @Field(TACTIC_FIELD)
    private Tactic tactic;
    @Field(LEVEL_FIELD)
    private Level level;

    public EnrichedIocRecord(Instant startInstant) {
        super(startInstant);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tactic getTactic() {
        return tactic;
    }

    public void setTactic(Tactic tactic) {
        this.tactic = tactic;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    @Transient
    public String getAdeEventType() {
        return Schema.IOC.getName();
    }

    @Transient
    public AdeEnrichedIocContext getContext() {
        return new AdeEnrichedIocContext(this);
    }
}

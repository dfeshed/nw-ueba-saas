package presidio.sdk.api.domain.rawevents;

import fortscale.domain.core.EventResult;
import fortscale.domain.core.ioc.Level;
import fortscale.domain.core.ioc.Tactic;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document
public class IocRawEvent extends AbstractInputDocument {


    public static final String NAME_FIELD_NAME = "name";
    public static final String TACTIC_FIELD_NAME = "tactic";
    public static final String LEVEL_FIELD_NAME = "level";
    public static final String MACHINE_ID_FIELD_NAME = "machineId";
    public static final String MACHINE_NAME_FIELD_NAME = "machineName";
    public static final String MACHINE_OWNER_FIELD_NAME = "machineOwner";

    @NotEmpty
    @Field(NAME_FIELD_NAME)
    private String name;

    @Field(TACTIC_FIELD_NAME)
    private Tactic tactic;

    @Field(LEVEL_FIELD_NAME)
    private Level level;

    @NotEmpty
    @Field(MACHINE_ID_FIELD_NAME)
    private String machineId;

    @NotEmpty
    @Field(MACHINE_NAME_FIELD_NAME)
    private String machineName;

    @Field(MACHINE_OWNER_FIELD_NAME)
    private String machineOwner;

    public IocRawEvent() {
    }

    public IocRawEvent(IocRawEvent other) {
        super(other);
        this.name = other.name;
        this.tactic = other.tactic;
        this.level = other.level;
        this.machineId = other.machineId;
        this.machineName = other.machineName;
        this.machineOwner = other.machineOwner;
    }

    public IocRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType,
                       List<String> operationTypeCategories, EventResult result, String userName, String userDisplayName,
                       Map<String, String> additionalInfo, String name, Tactic tactic, Level level, String machineId, String machineName, String machineOwner, String resultCode) {
        super(dateTime, eventId, dataSource, userId, operationType, operationTypeCategories, result, userName, userDisplayName, additionalInfo, resultCode);
        this.name = name;
        this.tactic = tactic;
        this.level = level;
        this.machineId = machineId;
        this.machineName = machineName;
        this.machineOwner = machineOwner;
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

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineOwner() {
        return machineOwner;
    }

    public void setMachineOwner(String machineOwner) {
        this.machineOwner = machineOwner;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

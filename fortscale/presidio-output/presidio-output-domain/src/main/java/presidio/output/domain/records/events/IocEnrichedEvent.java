package presidio.output.domain.records.events;

import fortscale.domain.core.EventResult;
import fortscale.domain.core.Level;
import fortscale.domain.core.Tactic;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;


@Document
public class IocEnrichedEvent extends EnrichedEvent {

    public static final String NAME_FIELD_NAME = "name";
    public static final String TACTIC_FIELD_NAME = "tactic";
    public static final String LEVEL_FIELD_NAME = "level";
    public static final String MACHINE_ID_FIELD_NAME = "machineId";
    public static final String MACHINE_NAME_FIELD_NAME = "machineName";
    public static final String MACHINE_OWNER_FIELD_NAME = "machineOwner";

    @Field(NAME_FIELD_NAME)
    private String name;

    @Field(TACTIC_FIELD_NAME)
    private Tactic tactic;

    @Field(LEVEL_FIELD_NAME)
    private Level level;

    @NotEmpty
    @Field(MACHINE_ID_FIELD_NAME)
    private String machineId;

    @Field(MACHINE_NAME_FIELD_NAME)
    private String machineName;

    @Field(MACHINE_OWNER_FIELD_NAME)
    private String machineOwner;

    public IocEnrichedEvent() {
    }

    public IocEnrichedEvent(String name,
                            Tactic tactic,
                            Level level,
                            String machineId,
                            String machineName,
                            String machineOwner) {
        this.name = name;
        this.tactic = tactic;
        this.level = level;
        this.machineId = machineId;
        this.machineName = machineName;
        this.machineOwner = machineOwner;
    }

    public IocEnrichedEvent(Instant createdDate,
                            Instant eventDate,
                            String eventId,
                            String schema,
                            String userId,
                            String userName,
                            String userDisplayName,
                            String dataSource,
                            String operationType,
                            List<String> operationTypeCategories,
                            EventResult result,
                            String resultCode,
                            Map<String, String> additionalInfo,
                            String name,
                            Tactic tactic,
                            Level level,
                            String machineId,
                            String machineName,
                            String machineOwner) {
        super(createdDate, eventDate, eventId, schema, userId, userName, userDisplayName, dataSource, operationType, operationTypeCategories, result, resultCode, additionalInfo);
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
}

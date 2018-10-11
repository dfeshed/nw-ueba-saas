package presidio.ade.domain.record.enriched.ioc;

import fortscale.domain.core.ioc.Level;
import fortscale.domain.core.ioc.Tactic;
import presidio.ade.domain.record.enriched.BaseEnrichedContext;


public class AdeEnrichedIocContext extends BaseEnrichedContext {
    private String userId;
    private String machineId;
    private String name;
    private Tactic tactic;
    private Level level;

    public AdeEnrichedIocContext() {
        super();
    }

    public AdeEnrichedIocContext(String eventId) {
        super(eventId);
    }

    public AdeEnrichedIocContext(EnrichedIocRecord enrichedIocRecord) {
        super(enrichedIocRecord.getEventId());
        this.userId = enrichedIocRecord.getUserId();
        this.machineId = enrichedIocRecord.getMachineId();
        this.name = enrichedIocRecord.getName();
        this.tactic = enrichedIocRecord.getTactic();
        this.level = enrichedIocRecord.getLevel();
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
}

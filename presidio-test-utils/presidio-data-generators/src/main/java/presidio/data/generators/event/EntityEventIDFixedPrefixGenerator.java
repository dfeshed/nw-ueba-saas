package presidio.data.generators.event;

import presidio.data.generators.common.IStringGenerator;

public class EntityEventIDFixedPrefixGenerator implements IStringGenerator {
    private String entityname;
    private final String PREFIX = "EV";
    private long eventSeqNum = 1;

    public EntityEventIDFixedPrefixGenerator(String entityname) {
        this.entityname = entityname;
    }

    public EntityEventIDFixedPrefixGenerator() {}

    public void setEntityname(String entityname){
        this.entityname = entityname;
    }
    
    public String getNext() {

        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX);
        sb.append("-");
        sb.append(eventSeqNum++);
        sb.append("-");
        sb.append(this.entityname);

        return sb.toString();
    }
}

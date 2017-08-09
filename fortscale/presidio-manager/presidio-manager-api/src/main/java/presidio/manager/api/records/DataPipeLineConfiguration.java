package presidio.manager.api.records;


import java.time.Instant;

public class DataPipeLineConfiguration {

    private Instant startTime;

    private String schemasEnum;

    public DataPipeLineConfiguration() {
        this.startTime = null;
        this.schemasEnum = null;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void setSchemasEnum(String schemasEnum) {
        this.schemasEnum = schemasEnum;
    }
}

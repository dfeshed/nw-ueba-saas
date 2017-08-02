package fortscale.domain.core;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document
public class AuthenticationRawEvent extends AbstractPresidioDocument {

    public static final String SRC_MACHINE_ID_FIELD_NAME = "srcMachineId";
    public static final String DST_MACHINE_ID_FIELD_NAME = "dstMachineId";
    private static final String SRC_MACHINE_NAME_FIELD_NAME = "srcMachineName";
    private static final String DST_MACHINE_NAME_FIELD_NAME = "dstMachineName";
    private static final String DST_MACHINE_DOMAIN_FIELD_NAME = "dstMachineDomain";

    @Field(SRC_MACHINE_ID_FIELD_NAME)
    @NotEmpty
    private String srcMachineId;

    @Field(SRC_MACHINE_NAME_FIELD_NAME)
    private String srcMachineName;

    @Field(DST_MACHINE_ID_FIELD_NAME)
    @NotEmpty
    private String dstMachineId;

    @Field(DST_MACHINE_NAME_FIELD_NAME)
    private String dstMachineName;

    @Field(DST_MACHINE_DOMAIN_FIELD_NAME)
    private String dstMachineDomain;

    public AuthenticationRawEvent() {

    }

    public AuthenticationRawEvent(String event[]) {
        this.dateTime = Instant.parse(event[0]);
        this.eventId = event[1];
        this.dataSource = event[2];
        this.userId = event[3];
        this.operationType = event[4];
        this.result = EventResult.valueOf(event[5]);
        this.srcMachineId = event[6];
        this.srcMachineName = event[7];
        this.dstMachineId = event[8];
        this.dstMachineName = event[9];
        this.dstMachineDomain = event[10];
        this.operationTypeCategory = new ArrayList<>();
        for (int i = 11; i <= event.length; i++) {
            this.operationTypeCategory.add(event[i]);
        }
    }

    public AuthenticationRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType,
                                  List<String> operationTypeCategory, EventResult result, String srcMachineId,
                                  String srcMachineName, String dstMachineId, String dstMachineName, String dstMachineDomain) {
        super(dateTime, eventId, dataSource, userId, operationType, operationTypeCategory, result);
        this.srcMachineId = srcMachineId;
        this.srcMachineName = srcMachineName;
        this.dstMachineId = dstMachineId;
        this.dstMachineName = dstMachineName;
        this.dstMachineDomain = dstMachineDomain;
    }

    public String getDstMachineId() {
        return dstMachineId;
    }

    public void setDstMachineId(String dstMachineId) {
        this.dstMachineId = dstMachineId;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public String getSrcMachineName() {
        return srcMachineName;
    }

    public void setSrcMachineName(String srcMachineName) {
        this.srcMachineName = srcMachineName;
    }

    public String getDstMachineName() {
        return dstMachineName;
    }

    public void setDstMachineName(String dstMachineName) {
        this.dstMachineName = dstMachineName;
    }

    public String getDstMachineDomain() {
        return dstMachineDomain;
    }

    public void setDstMachineDomain(String dstMachineDomain) {
        this.dstMachineDomain = dstMachineDomain;
    }
}

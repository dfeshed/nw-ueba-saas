package presidio.sdk.api.domain;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document
public class AuthenticationRawEvent extends AbstractInputDocument {

    public static final String AUTHENTICATION_TYPE_FIELD_NAME = "authenticationType";
    public static final String IS_DST_MACHINE_REMOTE_FIELD_NAME = "isDstMachineRemote";
    public static final String NORMALIZED_DST_MACHINE_FIELD_NAME = "normalizedDstMachine";
    public static final String NORMALIZED_SRC_MACHINE_FIELD_NAME = "normalizedSrcMachine";
    public static final String RESULT_CODE_FIELD_NAME = "resultCode";
    @Field(AUTHENTICATION_TYPE_FIELD_NAME)
    private String authenticationType;
    @Field(IS_DST_MACHINE_REMOTE_FIELD_NAME)
    private boolean isDstMachineRemote;
    @Field(NORMALIZED_DST_MACHINE_FIELD_NAME)
    @NotEmpty
    private String normalizedDstMachine;
    @Field(NORMALIZED_SRC_MACHINE_FIELD_NAME)
    @NotEmpty
    private String normalizedSrcMachine;
    @Field(RESULT_CODE_FIELD_NAME)
    private String resultCode;

    public AuthenticationRawEvent() {

    }

    public AuthenticationRawEvent(Instant dateTime, String dataSource, String normalizedUsername, String eventId,
                                  EventResult result, String authenticationType, boolean isDstMachineRemote,
                                  String normalizedDstMachine, String normalizedSrcMachine, String resultCode) {
        super(dateTime, dataSource, normalizedUsername, eventId, result);
        this.authenticationType = authenticationType;
        this.isDstMachineRemote = isDstMachineRemote;
        this.normalizedDstMachine = normalizedDstMachine;
        this.normalizedSrcMachine = normalizedSrcMachine;
        this.resultCode = resultCode;
    }

    public AuthenticationRawEvent(String record[]) {
        dateTime = Instant.parse(record[0]);
        this.eventId = record[1];
        this.dataSource = record[2];
        this.authenticationType = record[3];
        this.isDstMachineRemote = Boolean.getBoolean(record[4]);
        this.normalizedDstMachine = record[5];
        this.normalizedSrcMachine = record[6];
        this.normalizedUsername = record[7];
        this.result = EventResult.valueOf(record[8]);
        this.resultCode = record[9];
    }

    public boolean isDstMachineRemote() {
        return isDstMachineRemote;
    }

    public void setDstMachineRemote(boolean dstMachineRemote) {
        isDstMachineRemote = dstMachineRemote;
    }

    public String getNormalizedDstMachine() {
        return normalizedDstMachine;
    }

    public void setNormalizedDstMachine(String normalizedDstMachine) {
        this.normalizedDstMachine = normalizedDstMachine;
    }

    public String getNormalizedSrcMachine() {
        return normalizedSrcMachine;
    }

    public void setNormalizedSrcMachine(String normalizedSrcMachine) {
        this.normalizedSrcMachine = normalizedSrcMachine;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "AuthenticationRawEvent{" +
                "dataSource='" + dataSource + '\'' +
                ", normalizedUsername='" + normalizedUsername + '\'' +
                ", authenticationType='" + authenticationType + '\'' +
                ", eventId='" + eventId + '\'' +
                ", isDstMachineRemote=" + isDstMachineRemote +
                ", result=" + result +
                ", normalizedDstMachine='" + normalizedDstMachine + '\'' +
                ", normalizedSrcMachine='" + normalizedSrcMachine + '\'' +
                ", dateTime=" + dateTime +
                ", resultCode='" + resultCode + '\'' +
                '}';
    }
}

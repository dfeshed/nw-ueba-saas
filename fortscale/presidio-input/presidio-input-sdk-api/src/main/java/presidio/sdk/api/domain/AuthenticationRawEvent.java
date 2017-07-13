package presidio.sdk.api.domain;

import fortscale.domain.core.AbstractAuditableDocument;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document
public class AuthenticationRawEvent extends AbstractAuditableDocument {

    public static final String DATA_SOURCE_FIELD_NAME = "dataSource";
    public static final String AUTHENTICATION_TYPE_FIELD_NAME = "authenticationType";
    public static final String IS_DST_MACHINE_REMOTE_FIELD_NAME = "isDstMachineRemote";
    public static final String NORMALIZED_DST_MACHINE_FIELD_NAME = "normalizedDstMachine";
    public static final String NORMALIZED_SRC_MACHINE_FIELD_NAME = "normalizedSrcMachine";
    public static final String RESULT_FIELD_NAME = "result";
    public static final String NORMALIZED_USERNAME_FIELD_NAME = "normalizedUsername";
    public static final String RESULT_CODE_FIELD_NAME = "resultCode";
    public static final String EVENT_ID_FIELD_NAME = "eventId";

    @Field(DATA_SOURCE_FIELD_NAME)
    @NotEmpty
    private String dataSource;
    @Field(AUTHENTICATION_TYPE_FIELD_NAME)
    private AuthenticationType authenticationType;
    @Field(IS_DST_MACHINE_REMOTE_FIELD_NAME)
    private boolean isDstMachineRemote;
    @Field(NORMALIZED_DST_MACHINE_FIELD_NAME)
    @NotEmpty
    private String normalizedDstMachine;
    @Field(NORMALIZED_SRC_MACHINE_FIELD_NAME)
    @NotEmpty
    private String normalizedSrcMachine;
    @Field(NORMALIZED_USERNAME_FIELD_NAME)
    @NotEmpty
    private String normalizedUsername;
    @Field(RESULT_FIELD_NAME)
    private EventResult result;
    @Field(RESULT_CODE_FIELD_NAME)
    private AuthenticationResultCode resultCode;
    @NotEmpty
    @Field(EVENT_ID_FIELD_NAME)
    private String eventId;

    public AuthenticationRawEvent(String dataSource, AuthenticationType authenticationType, boolean isDstMachineRemote,
                                  String normalizedDstMachine, String normalizedSrcMachine, String normalizedUsername,
                                  EventResult result, AuthenticationResultCode resultCode, String eventId) {
        this.dataSource = dataSource;
        this.authenticationType = authenticationType;
        this.isDstMachineRemote = isDstMachineRemote;
        this.normalizedDstMachine = normalizedDstMachine;
        this.normalizedSrcMachine = normalizedSrcMachine;
        this.normalizedUsername = normalizedUsername;
        this.result = result;
        this.resultCode = resultCode;
        this.eventId = eventId;
    }

    public AuthenticationRawEvent(String record[]) {
        dateTime = Instant.parse(record[0]);
        this.eventId = record[1];
        this.dataSource = record[2];
        this.authenticationType = AuthenticationType.valueOf(record[3]);
        this.isDstMachineRemote = Boolean.getBoolean(record[4]);
        this.normalizedDstMachine = record[5];
        this.normalizedSrcMachine = record[6];
        this.normalizedUsername = record[7];
        this.result = EventResult.valueOf(record[8]);
        this.resultCode = AuthenticationResultCode.valueOf(record[9]);
    }

    public AuthenticationRawEvent() {
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
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

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
    }

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }

    public AuthenticationResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(AuthenticationResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}

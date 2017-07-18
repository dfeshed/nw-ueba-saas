package presidio.ade.domain.record.enriched;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;
import java.util.List;

/**
 * The enriched authentication record POJO.
 * interactive logon : src machine and dst machine are same.
 * interactive remote : src machine is original and dst machine is computer.
 */
@Document
//todo: add @AdeRecordMetadata annotation
public class EnrichedAuthenticationRecord extends EnrichedRecord {

    public static final String AUTHENTICATION_TYPE_FIELD = "authenticationType";
    public static final String IS_DST_MACHINE_REMOTE_FIELD = "isDstMachineRemote";
    public static final String NORMALIZED_USERNAME_FIELD = "normalizedUsername";
    public static final String NORMALIZED_SRC_MACHINE_FIELD = "normalizedSrcMachine";
    public static final String NORMALIZED_DST_MACHINE_FIELD = "normalizedDstMachine";
    public static final String RESULT_FIELD = "result";
    public static final String RESULT_CODE_FIELD = "resultCode";



    @Indexed
    @Field(NORMALIZED_USERNAME_FIELD)
    private String normalizedUsername;
    @Field(AUTHENTICATION_TYPE_FIELD)
    private String authenticationType;
    @Field(IS_DST_MACHINE_REMOTE_FIELD)
    private Boolean isDstMachineRemote;
    @Field(NORMALIZED_SRC_MACHINE_FIELD)
    private String normalizedSrcMachine;
    @Field(NORMALIZED_DST_MACHINE_FIELD)
    private String normalizedDstMachine;
    @Field(RESULT_FIELD)
    private String result;
    @Field(RESULT_CODE_FIELD)
    private String resultCode;

    /**
     * C'tor.
     *
     * @param dateTime The record's logical time
     */
    public EnrichedAuthenticationRecord(Instant dateTime) {
        super(dateTime);
    }

    @Override
    public String getAdeEventType() {
        return AuthenticationRecord.AUTHENTICATION_STR;
    }

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public Boolean getDstMachineRemote() {
        return isDstMachineRemote;
    }

    public void setDstMachineRemote(Boolean dstMachineRemote) {
        isDstMachineRemote = dstMachineRemote;
    }

    public String getNormalizedSrcMachine() {
        return normalizedSrcMachine;
    }

    public void setNormalizedSrcMachine(String normalizedSrcMachine) {
        this.normalizedSrcMachine = normalizedSrcMachine;
    }

    public String getNormalizedDstMachine() {
        return normalizedDstMachine;
    }

    public void setNormalizedDstMachine(String normalizedDstMachine) {
        this.normalizedDstMachine = normalizedDstMachine;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Transient
    public AdeEnrichedAuthenticationContext getContext() {
        return new AdeEnrichedAuthenticationContext(this);
    }
}

package presidio.ade.domain.record.enriched;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * The enriched authentication record POJO.
 * interactive logon : src machine and dst machine are same.
 * interactive remote : src machine is original and dst machine is computer.
 */
@Document
//todo: add @AdeRecordMetadata annotation
public class EnrichedAuthenticationRecord extends EnrichedRecord {
    public static final String USER_ID_FIELD = "userId";
    public static final String SRC_MACHINE_ID_FIELD = "SrcMachineId";
    public static final String DST_MACHINE_ID_FIELD = "dstMachineId";
    public static final String SRC_MACHINE_NAME_REGEX_CLUSTER = "srcMachineNameRegexCluster";
    public static final String DST_MACHINE_NAME_REGEX_CLUSTER = "dstMachineNameRegexCluster";
    public static final String DST_MACHINE_DOMAIN = "dstMachineDomain";




    @Indexed
    @Field(USER_ID_FIELD)
    private String userId;
    @Field(SRC_MACHINE_ID_FIELD)
    private String SrcMachineId;
    @Field(DST_MACHINE_ID_FIELD)
    private String dstMachineId;
    @Field(SRC_MACHINE_NAME_REGEX_CLUSTER)
    private String srcMachineNameRegexCluster;
    @Field(DST_MACHINE_NAME_REGEX_CLUSTER)
    private String dstMachineNameRegexCluster;
    @Field(DST_MACHINE_DOMAIN)
    private String dstMachineDomain;

    /**
     * C'tor.
     *
     * @param startInstant The record's logical time
     */
    public EnrichedAuthenticationRecord(Instant startInstant) {
        super(startInstant);
    }

    @Override
    public String getAdeEventType() {
        return AuthenticationRecord.AUTHENTICATION_STR;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSrcMachineId() {
        return SrcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.SrcMachineId = srcMachineId;
    }

    public String getDstMachineId() {
        return dstMachineId;
    }

    public void setDstMachineId(String dstMachineId) {
        this.dstMachineId = dstMachineId;
    }

    public String getSrcMachineNameRegexCluster() {
        return srcMachineNameRegexCluster;
    }

    public void setSrcMachineNameRegexCluster(String srcMachineNameRegexCluster) {
        this.srcMachineNameRegexCluster = srcMachineNameRegexCluster;
    }

    public String getDstMachineNameRegexCluster() {
        return dstMachineNameRegexCluster;
    }

    public void setDstMachineNameRegexCluster(String dstMachineNameRegexCluster) {
        this.dstMachineNameRegexCluster = dstMachineNameRegexCluster;
    }

    public String getDstMachineDomain() {
        return dstMachineDomain;
    }

    public void setDstMachineDomain(String dstMachineDomain) {
        this.dstMachineDomain = dstMachineDomain;
    }

    @Transient
    public AdeEnrichedAuthenticationContext getContext() {
        return new AdeEnrichedAuthenticationContext(this);
    }
}

package presidio.ade.domain.record.enriched.authentication;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;

/**
 * The enriched authentication record POJO.
 * interactive logon : src machine and dst machine are same.
 * interactive remote : src machine is original and dst machine is computer.
 */
@Document
@AdeRecordMetadata(adeEventType = AdeAuthenticationRecord.AUTHENTICATION_STR)
@CompoundIndexes({
        @CompoundIndex(name = "start", def = "{'startInstant': 1}")
        // A compound index is created dynamically for every <'contextType', 'startInstant'> pair in use
})
public class EnrichedAuthenticationRecord extends EnrichedRecord {
    public static final String USER_ID_FIELD = "userId";
    public static final String SRC_MACHINE_ID_FIELD = "srcMachineId";
    public static final String DST_MACHINE_ID_FIELD = "dstMachineId";
    public static final String SRC_MACHINE_NAME_REGEX_CLUSTER_FIELD = "srcMachineNameRegexCluster";
    public static final String DST_MACHINE_NAME_REGEX_CLUSTER_FIELD = "dstMachineNameRegexCluster";
    public static final String DST_MACHINE_DOMAIN = "dstMachineDomain";

    @Field(USER_ID_FIELD)
    private String userId;
    @Field(SRC_MACHINE_ID_FIELD)
    private String srcMachineId;
    @Field(DST_MACHINE_ID_FIELD)
    private String dstMachineId;
    @Field(SRC_MACHINE_NAME_REGEX_CLUSTER_FIELD)
    private String srcMachineNameRegexCluster;
    @Field(DST_MACHINE_NAME_REGEX_CLUSTER_FIELD)
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
        return AdeAuthenticationRecord.AUTHENTICATION_STR;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
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

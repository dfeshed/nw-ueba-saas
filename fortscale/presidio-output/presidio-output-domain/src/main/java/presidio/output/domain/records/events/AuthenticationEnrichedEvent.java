package presidio.output.domain.records.events;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by efratn on 02/08/2017.
 */
@Document
public class AuthenticationEnrichedEvent extends EnrichedEvent {

    public static final String SRC_MACHINE_ID_FIELD = "SrcMachineId";
    public static final String DST_MACHINE_ID_FIELD = "dstMachineId";
    public static final String SRC_MACHINE_NAME_REGEX_CLUSTER = "srcMachineNameRegexCluster";
    public static final String DST_MACHINE_NAME_REGEX_CLUSTER = "dstMachineNameRegexCluster";
    public static final String DST_MACHINE_DOMAIN = "dstMachineDomain";

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

    public void setSrcMachineId(String srcMachineId) {
        SrcMachineId = srcMachineId;
    }

    public void setDstMachineId(String dstMachineId) {
        this.dstMachineId = dstMachineId;
    }

    public void setSrcMachineNameRegexCluster(String srcMachineNameRegexCluster) {
        this.srcMachineNameRegexCluster = srcMachineNameRegexCluster;
    }

    public void setDstMachineNameRegexCluster(String dstMachineNameRegexCluster) {
        this.dstMachineNameRegexCluster = dstMachineNameRegexCluster;
    }

    public void setDstMachineDomain(String dstMachineDomain) {
        this.dstMachineDomain = dstMachineDomain;
    }

    public String getSrcMachineId() {
        return SrcMachineId;
    }

    public String getDstMachineId() {
        return dstMachineId;
    }

    public String getSrcMachineNameRegexCluster() {
        return srcMachineNameRegexCluster;
    }

    public String getDstMachineNameRegexCluster() {
        return dstMachineNameRegexCluster;
    }

    public String getDstMachineDomain() {
        return dstMachineDomain;
    }
}

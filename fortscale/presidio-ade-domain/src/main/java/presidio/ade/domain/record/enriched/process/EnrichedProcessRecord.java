package presidio.ade.domain.record.enriched.process;

import fortscale.common.general.Schema;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;
import java.util.List;

/**
 * The enriched process record POJO.
 */
@Document
@AdeRecordMetadata(adeEventType = Schema.PROCESS)
public class EnrichedProcessRecord extends EnrichedRecord {

    public static final String USER_ID_FIELD = "userId";
    public static final String MACHINE_ID_FIELD = "machineId";
    public static final String SRC_PROCESS_DIRECTORY_FIELD = "srcProcessDirectory";
    public static final String SRC_PROCESS_FILE_NAME_FIELD = "srcProcessFileName";
    public static final String SRC_PROCESS_DIRECTORY_GROUPS_FIELD = "srcProcessDirectoryGroups";
    public static final String SRC_PROCESS_CATEGORIES_FIELD = "srcProcessCategories";
    public static final String SRC_PROCESS_CERTIFICATE_ISSUER_FIELD = "srcProcessCertificateIssuer";
    public static final String DST_PROCESS_DIRECTORY_FIELD = "dstProcessDirectory";
    public static final String DST_PROCESS_FILE_NAME_FIELD = "dstProcessFileName";
    public static final String DST_PROCESS_DIRECTORY_GROUPS_FIELD = "dstProcessDirectoryGroups";
    public static final String DST_PROCESS_CATEGORIES_FIELD = "dstProcessCategories";
    public static final String DST_PROCESS_CERTIFICATE_ISSUER_FIELD = "dstProcessCertificateIssuer";
    public static final String OPERATION_TYPE_FIELD = "operationType";

    @Field(USER_ID_FIELD)
    private String userId;
    @Field(MACHINE_ID_FIELD)
    private String machineId;
    @Field(SRC_PROCESS_DIRECTORY_FIELD)
    private String srcProcessDirectory;
    @Field(SRC_PROCESS_FILE_NAME_FIELD)
    private String srcProcessFileName;
    @Field(SRC_PROCESS_DIRECTORY_GROUPS_FIELD)
    private List<String> srcProcessDirectoryGroups;
    @Field(SRC_PROCESS_CATEGORIES_FIELD)
    private List<String> srcProcessCategories;
    @Field(SRC_PROCESS_CERTIFICATE_ISSUER_FIELD)
    private String srcProcessCertificateIssuer;
    @Field(DST_PROCESS_DIRECTORY_FIELD)
    private String dstProcessDirectory;
    @Field(DST_PROCESS_FILE_NAME_FIELD)
    private String dstProcessFileName;
    @Field(DST_PROCESS_DIRECTORY_GROUPS_FIELD)
    private List<String> dstProcessDirectoryGroups;
    @Field(DST_PROCESS_CATEGORIES_FIELD)
    private List<String> dstProcessCategories;
    @Field(DST_PROCESS_CERTIFICATE_ISSUER_FIELD)
    private String dstProcessCertificateIssuer;
    @Field(OPERATION_TYPE_FIELD)
    private String operationType;

    /**
     * C'tor.
     *
     * @param startInstant The record's logical date and time
     */
    public EnrichedProcessRecord(Instant startInstant) {
        super(startInstant);
    }

    @Override
    @Transient
    public String getAdeEventType() {
        return Schema.PROCESS.getName();
    }

    @Transient
    public AdeEnrichedProcessContext getContext() {
        return new AdeEnrichedProcessContext(this);
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

    public String getSrcProcessDirectory() {
        return srcProcessDirectory;
    }

    public void setSrcProcessDirectory(String srcProcessDirectory) {
        this.srcProcessDirectory = srcProcessDirectory;
    }

    public String getSrcProcessFileName() {
        return srcProcessFileName;
    }

    public void setSrcProcessFileName(String srcProcessFileName) {
        this.srcProcessFileName = srcProcessFileName;
    }

    public List<String> getSrcProcessDirectoryGroups() {
        return srcProcessDirectoryGroups;
    }

    public void setSrcProcessDirectoryGroups(List<String> srcProcessDirectoryGroups) {
        this.srcProcessDirectoryGroups = srcProcessDirectoryGroups;
    }

    public List<String> getSrcProcessCategories() {
        return srcProcessCategories;
    }

    public void setSrcProcessCategories(List<String> srcProcessCategories) {
        this.srcProcessCategories = srcProcessCategories;
    }

    public String getSrcProcessCertificateIssuer() {
        return srcProcessCertificateIssuer;
    }

    public void setSrcProcessCertificateIssuer(String srcProcessCertificateIssuer) {
        this.srcProcessCertificateIssuer = srcProcessCertificateIssuer;
    }

    public String getDstProcessDirectory() {
        return dstProcessDirectory;
    }

    public void setDstProcessDirectory(String dstProcessDirectory) {
        this.dstProcessDirectory = dstProcessDirectory;
    }

    public String getDstProcessFileName() {
        return dstProcessFileName;
    }

    public void setDstProcessFileName(String dstProcessFileName) {
        this.dstProcessFileName = dstProcessFileName;
    }

    public List<String> getDstProcessDirectoryGroups() {
        return dstProcessDirectoryGroups;
    }

    public void setDstProcessDirectoryGroups(List<String> dstProcessDirectoryGroups) {
        this.dstProcessDirectoryGroups = dstProcessDirectoryGroups;
    }

    public List<String> getDstProcessCategories() {
        return dstProcessCategories;
    }

    public void setDstProcessCategories(List<String> dstProcessCategories) {
        this.dstProcessCategories = dstProcessCategories;
    }

    public String getDstProcessCertificateIssuer() {
        return dstProcessCertificateIssuer;
    }

    public void setDstProcessCertificateIssuer(String dstProcessCertificateIssuer) {
        this.dstProcessCertificateIssuer = dstProcessCertificateIssuer;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}

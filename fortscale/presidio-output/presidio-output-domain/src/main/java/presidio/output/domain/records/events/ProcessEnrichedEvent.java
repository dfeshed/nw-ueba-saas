package presidio.output.domain.records.events;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ProcessEnrichedEvent extends EnrichedEvent {

    public static final String MACHINE_ID_FIELD_NAME = "machineId";
    public static final String MACHINE_NAME_FIELD_NAME = "machineName";
    public static final String MACHINE_OWNER_FIELD_NAME = "machineOwner";
    public static final String SRC_PROCESS_DIRECTORY_FIELD_NAME = "srcProcessDirectory";
    public static final String SRC_PROCESS_FILE_NAME_FIELD_NAME = "srcProcessFileName";
    public static final String SRC_PROCESS_DIRECTORY_GROUPS_FIELD_NAME = "srcProcessDirectoryGroups";
    public static final String SRC_PROCESS_CATEGORIES_FIELD_NAME = "srcProcessCategories";
    public static final String SRC_PROCESS_CERTIFICATE_ISSUER_FIELD_NAME = "srcProcessCertificateIssuer";
    public static final String DST_PROCESS_DIRECTORY_FIELD_NAME = "dstProcessDirectory";
    public static final String DST_PROCESS_FILE_NAME_FIELD_NAME = "dstProcessFileName";
    public static final String DST_PROCESS_DIRECTORY_GROUPS_FIELD_NAME = "dstProcessDirectoryGroups";
    public static final String DST_PROCESS_CATEGORIES_FIELD_NAME = "dstProcessCategories";
    public static final String DST_PROCESS_CERTIFICATE_ISSUER_FIELD_NAME = "dstProcessCertificateIssuer";
    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";


    @Field(MACHINE_ID_FIELD_NAME)
    private String machineId;

    @Field(MACHINE_NAME_FIELD_NAME)
    private String machineName;

    @Field(MACHINE_OWNER_FIELD_NAME)
    private String machineOwner;

    @Field(SRC_PROCESS_DIRECTORY_FIELD_NAME)
    private String srcProcessDirectory;

    @Field(SRC_PROCESS_FILE_NAME_FIELD_NAME)
    private String srcProcessFileName;

    @Field(SRC_PROCESS_DIRECTORY_GROUPS_FIELD_NAME)
    private List<String> srcProcessDirectoryGroups;

    @Field(SRC_PROCESS_CATEGORIES_FIELD_NAME)
    private List<String> srcProcessCategories;

    @Field(SRC_PROCESS_CERTIFICATE_ISSUER_FIELD_NAME)
    private String srcProcessCertificateIssuer;

    @Field(DST_PROCESS_DIRECTORY_FIELD_NAME)
    private String dstProcessDirectory;

    @Field(DST_PROCESS_FILE_NAME_FIELD_NAME)
    private String dstProcessFileName;

    @Field(DST_PROCESS_DIRECTORY_GROUPS_FIELD_NAME)
    private List<String> dstProcessDirectoryGroups;

    @Field(DST_PROCESS_CATEGORIES_FIELD_NAME)
    private List<String> dstProcessCategories;

    @Field(DST_PROCESS_CERTIFICATE_ISSUER_FIELD_NAME)
    private String dstProcessCertificateIssuer;

    @Field(OPERATION_TYPE_FIELD_NAME)
    private String operationType;


    public ProcessEnrichedEvent() {
    }

    public ProcessEnrichedEvent(Instant createdDate, Instant eventDate, String eventId, String schema, String userId, String userName, String userDisplayName, String dataSource, String operationType, Map<String, String> additionalInfo, String machineId, String machineName, String machineOwner, String srcProcessDirectory, String srcProcessFileName, List<String> srcProcessDirectoryGroups, List<String> srcProcessCategories, String srcProcessCertificateIssuer, String dstProcessDirectory, String dstProcessFileName, List<String> dstProcessDirectoryGroups, List<String> dstProcessCategories, String dstProcessCertificateIssuer) {
        super(createdDate, eventDate, eventId, schema, userId, userName, userDisplayName, dataSource, additionalInfo);
        this.machineId = machineId;
        this.machineName = machineName;
        this.machineOwner = machineOwner;
        this.srcProcessDirectory = srcProcessDirectory;
        this.srcProcessFileName = srcProcessFileName;
        this.srcProcessDirectoryGroups = srcProcessDirectoryGroups;
        this.srcProcessCategories = srcProcessCategories;
        this.srcProcessCertificateIssuer = srcProcessCertificateIssuer;
        this.dstProcessDirectory = dstProcessDirectory;
        this.dstProcessFileName = dstProcessFileName;
        this.dstProcessDirectoryGroups = dstProcessDirectoryGroups;
        this.dstProcessCategories = dstProcessCategories;
        this.dstProcessCertificateIssuer = dstProcessCertificateIssuer;
        this.operationType = operationType;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineOwner() {
        return machineOwner;
    }

    public void setMachineOwner(String machineOwner) {
        this.machineOwner = machineOwner;
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

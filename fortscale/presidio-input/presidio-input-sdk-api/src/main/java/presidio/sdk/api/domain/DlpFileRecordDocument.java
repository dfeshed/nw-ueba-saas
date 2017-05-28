package presidio.sdk.api.domain;


import java.util.Date;

public class DlpFileRecordDocument extends AbstractRecordDocument {

    private String eventType;
    private String eventId;
    private String username;
    private String normalizedUsername;
    private String fullName;
    private String hostname;
    private String normalizedSrcMachine;
    private String sourceIp;
    private String executingApplication;
    private String sourcePath;
    private String destinationPath;
    private String sourceFileName;
    private String destinationFileName;
    private double fileSize;
    private String sourceDriveType;
    private String destinationDriveType;
    private boolean wasClassified;
    private boolean wasBlocked;
    private String malwareScanResult;
    private String policyName;
    private String isRdp;
    private String isAdminActivity;
    private String isRegistryChanged;
    private String updateTimestamp;
    private String yearmonthday;


    protected DlpFileRecordDocument(Date dateTime, long dateTimeUnix, String eventType, String eventId, String username, String normalizedUsername, String fullName, String hostname, String normalizedSrcMachine, String sourceIp, String executingApplication, String sourcePath, String destinationPath, String sourceFileName, String destinationFileName, double fileSize, String sourceDriveType, String destinationDriveType, boolean wasClassified, boolean wasBlocked, String malwareScanResult, String policyName, String isRdp, String isAdminActivity, String isRegistryChanged, String updateTimestamp, String yearmonthday) {
        super(dateTime, dateTimeUnix);
        this.eventType = eventType;
        this.eventId = eventId;
        this.username = username;
        this.normalizedUsername = normalizedUsername;
        this.fullName = fullName;
        this.hostname = hostname;
        this.normalizedSrcMachine = normalizedSrcMachine;
        this.sourceIp = sourceIp;
        this.executingApplication = executingApplication;
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        this.sourceFileName = sourceFileName;
        this.destinationFileName = destinationFileName;
        this.fileSize = fileSize;
        this.sourceDriveType = sourceDriveType;
        this.destinationDriveType = destinationDriveType;
        this.wasClassified = wasClassified;
        this.wasBlocked = wasBlocked;
        this.malwareScanResult = malwareScanResult;
        this.policyName = policyName;
        this.isRdp = isRdp;
        this.isAdminActivity = isAdminActivity;
        this.isRegistryChanged = isRegistryChanged;
        this.updateTimestamp = updateTimestamp;
        this.yearmonthday = yearmonthday;
    }

    @Override
    public String toString() {
        return "DlpFileRecordDocument{" + "dateTime=" + dateTime +
                ", dateTimeUnix=" + dateTimeUnix +
                ", eventType='" + eventType + '\'' +
                ", eventId='" + eventId + '\'' +
                ", username='" + username + '\'' +
                ", normalizedUsername='" + normalizedUsername + '\'' +
                ", fullName='" + fullName + '\'' +
                ", hostname='" + hostname + '\'' +
                ", normalizedSrcMachine='" + normalizedSrcMachine + '\'' +
                ", sourceIp='" + sourceIp + '\'' +
                ", executingApplication='" + executingApplication + '\'' +
                ", sourcePath='" + sourcePath + '\'' +
                ", destinationPath='" + destinationPath + '\'' +
                ", sourceFileName='" + sourceFileName + '\'' +
                ", destinationFileName='" + destinationFileName + '\'' +
                ", fileSize=" + fileSize +
                ", sourceDriveType='" + sourceDriveType + '\'' +
                ", destinationDriveType='" + destinationDriveType + '\'' +
                ", wasClassified=" + wasClassified +
                ", wasBlocked=" + wasBlocked +
                ", malwareScanResult='" + malwareScanResult + '\'' +
                ", policyName='" + policyName + '\'' +
                ", isRdp='" + isRdp + '\'' +
                ", isAdminActivity='" + isAdminActivity + '\'' +
                ", isRegistryChanged='" + isRegistryChanged + '\'' +
                ", updateTimestamp='" + updateTimestamp + '\'' +
                ", yearmonthday='" + yearmonthday + '\'' +
                '}';
    }
}



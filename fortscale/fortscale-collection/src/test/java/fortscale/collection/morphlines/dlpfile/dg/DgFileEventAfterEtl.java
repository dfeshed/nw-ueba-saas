package fortscale.collection.morphlines.dlpfile.dg;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class DgFileEventAfterEtl {

    public String dateTime;
    public String dateTimeUnix;
    public String eventType;
    public String eventId;
    public String username;
    public String normalizedUsername;
    public String fullName;
    public String hostname;
    public String normalizedSrcMachine;
    public String ipAddress;
    public String application;
    public String sourceDirectory;
    public String destinationDirectory;
    public String sourceFile;
    public String destinationFile;
    public String detailFileSize;
    public String sourceDriveType;
    public String destinationDriveType;
    public String wasClassified;
    public String wasBlocked;
    public String scanValueStatusText;
    public String malwarePolicyName;
    public String isRdp;
    public String isAdminActivity;
    public String isRegistryChanged;
    public String dataSource;
    public String lastState;

    protected DgFileEventAfterEtl() {
    }

    public DgFileEventAfterEtl(String dateTime, String dateTimeUnix, String eventType, String eventId, String username,
                               String normalizedUsername, String fullName, String hostname, String normalizedSrcMachine,
                               String ipAddress, String application, String sourceDirectory,
                               String detailFileSize, String sourceDriveType, String destinationDriveType,
                               String wasClassified, String wasBlocked, String scanValueStatusText, String isAdminActivity,
                               String isRdp, String isRegistryChanged, String dataSource, String lastState, String sourceFile,
                               String destinationFile, String malwarePolicyName) {
        this.dateTime = dateTime;
        this.dateTimeUnix = dateTimeUnix;
        this.eventType = eventType;
        this.eventId = eventId;
        this.username = username;
        this.normalizedUsername = normalizedUsername;
        this.fullName = fullName;
        this.hostname = hostname;
        this.normalizedSrcMachine = normalizedSrcMachine;
        this.ipAddress = ipAddress;
        this.application = application;
        this.sourceDirectory = sourceDirectory;
        this.destinationDirectory = detailFileSize;
        this.detailFileSize = detailFileSize;
        this.sourceDriveType = sourceDriveType;
        this.destinationDriveType = destinationDriveType;
        this.wasClassified = wasClassified;
        this.wasBlocked = wasBlocked;
        this.scanValueStatusText = scanValueStatusText;
        this.isAdminActivity= isAdminActivity;
        this.isRdp = isRdp;
        this.isRegistryChanged = isRegistryChanged;
        this.dataSource = dataSource;
        this.lastState = lastState;
        this.sourceFile = sourceFile;
        this.destinationFile = destinationFile;
        this.malwarePolicyName = malwarePolicyName;
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(",");
        final Field[] fields = this.getClass().getFields();
        for (Field field : fields) {
            try {
                final String newElement = (String) field.get(this);
                switch (newElement) {
                    case "some_hostname":
                    case "some_normalizedSrcMachine":
                        stringJoiner.add("some_computerName");
                        break;
                    case "some_normalizedUsername":
                        stringJoiner.add("");
                        break;
                    case "some_malwarePolicyName":
                        stringJoiner.add("todo");
                        break;
                    default:
                        stringJoiner.add(newElement);
                        break;
                }
            } catch (IllegalAccessException e) {
                return null;
            }
        }

        return stringJoiner.toString();
    }
}

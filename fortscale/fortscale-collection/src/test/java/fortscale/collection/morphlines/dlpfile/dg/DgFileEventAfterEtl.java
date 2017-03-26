package fortscale.collection.morphlines.dlpfile.dg;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
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
                               String ipAddress, String application, String sourceDirectory, String destinationDirectory,
                               String sourceFile, String destinationFile, String detailFileSize, String sourceDriveType,
                               String destinationDriveType, String wasClassified, String wasBlocked, String scanValueStatusText,
                               String malwarePolicyName, String isRdp, String isAdminActivity, String isRegistryChanged,
                               String dataSource, String lastState) {
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
        this.destinationDirectory = destinationDirectory;
        this.sourceFile = sourceFile;
        this.destinationFile = destinationFile;
        this.detailFileSize = detailFileSize;
        this.sourceDriveType = sourceDriveType;
        this.destinationDriveType = destinationDriveType;
        this.wasClassified = wasClassified;
        this.wasBlocked = wasBlocked;
        this.scanValueStatusText = scanValueStatusText;
        this.malwarePolicyName = malwarePolicyName;
        this.isRdp = isRdp;
        this.isAdminActivity = isAdminActivity;
        this.isRegistryChanged = isRegistryChanged;
        this.dataSource = dataSource;
        this.lastState = lastState;
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(",");
        List<String> fields = Arrays.asList("dateTime" ,"dateTimeUnix" ,"eventType" ,"eventId" ,"username" ,"normalizedUsername"
                ,"fullName" ,"hostname" ,"normalizedSrcMachine" ,"ipAddress" ,"application" ,"sourceDirectory" ,"destinationDirectory"
                ,"sourceFile" ,"destinationFile" ,"detailFileSize" ,"sourceDriveType" ,"destinationDriveType" ,"wasClassified"
                ,"wasBlocked" ,"scanValueStatusText" ,"malwarePolicyName" ,"isRdp" ,"isAdminActivity" ,"isRegistryChanged", "dataSource", "lastState");
        for (String fieldName : fields) {
            try {
                final Field field;
                try {
                    field = this.getClass().getField(fieldName);
                } catch (NoSuchFieldException e) {
                    return null;
                }

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

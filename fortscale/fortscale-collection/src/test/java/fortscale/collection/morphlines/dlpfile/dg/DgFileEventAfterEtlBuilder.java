package fortscale.collection.morphlines.dlpfile.dg;

import java.lang.reflect.Field;

public class DgFileEventAfterEtlBuilder {
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
    public String detailFileSize;
    public String sourceDriveType;
    public String destinationDriveType;
    public String wasClassified;
    public String wasBlocked;
    public String scanValueStatusText;
    public String malwarePolicyName;
    public String destinationFile;
    public String isAdminActivity;
    public String isRdp;
    public String isRegistryChanged;
    public String dataSource;
    public String lastState;


    public DgFileEventAfterEtlBuilder() {
    }

    public DgFileEventAfterEtlBuilder setMalwarePolicyName(String malwarePolicyName) {
        this.malwarePolicyName = malwarePolicyName;
        return this;
    }

    public DgFileEventAfterEtlBuilder setDateTime(String dateTime) {
        this.dateTime = dateTime;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setDateTimeUnix(String dateTimeUnix) {
        this.dateTimeUnix = dateTimeUnix;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setEventType(String eventType) {
        this.eventType = eventType;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setEventId(String eventId) {
        this.eventId = eventId;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setUsername(String username) {
        this.username = username;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setFullName(String fullName) {
        this.fullName = fullName;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setHostname(String hostname) {
        this.hostname = hostname;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setNormalizedSrcMachine(String normalizedSrcMachine) {
        this.normalizedSrcMachine = normalizedSrcMachine;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setApplication(String application) {
        this.application = application;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setDestinationDirectory(String destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setDetailFileSize(String detailFileSize) {
        this.detailFileSize = detailFileSize;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setSourceDriveType(String sourceDriveType) {
        this.sourceDriveType = sourceDriveType;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setDestinationDriveType(String destinationDriveType) {
        this.destinationDriveType = destinationDriveType;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setWasClassified(String wasClassified) {
        this.wasClassified = wasClassified;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setWasBlocked(String wasBlocked) {
        this.wasBlocked = wasBlocked;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setScanValueStatusText(String scanValueStatusText) {
        this.scanValueStatusText = scanValueStatusText;
        return  this;
    }

    public DgFileEventAfterEtlBuilder setIsAdminActivity(String isAdminActivity) {
        this.isAdminActivity = isAdminActivity;
        return this;
    }

    public DgFileEventAfterEtlBuilder setIsRdp(String isRdp) {
        this.isRdp = isRdp;
        return this;
    }

    public DgFileEventAfterEtlBuilder setIsRegistryChanged(String isRegistryChanged) {
        this.isRegistryChanged = isRegistryChanged;
        return this;
    }

    public DgFileEventAfterEtlBuilder setDataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public DgFileEventAfterEtlBuilder setLastState(String lastState) {
        this.lastState = lastState;
        return this;
    }

    public DgFileEventAfterEtlBuilder setDestinationFile(String destinationFile) {
        this.destinationFile = destinationFile;
        return this;
    }

    public DgFileEventAfterEtlBuilder setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
        return this;
    }

    public DgFileEventAfterEtl createDgEventAfterEtl() {
        DgFileEventAfterEtl dgFileEventAfterEtl = new DgFileEventAfterEtl( dateTime,  dateTimeUnix,  eventType,  eventId,  username,
                normalizedUsername,  fullName,  hostname,  normalizedSrcMachine,
                ipAddress,  application,  sourceDirectory,  destinationDirectory,
                sourceFile,  destinationFile,  detailFileSize,  sourceDriveType,
                destinationDriveType,  wasClassified,  wasBlocked,  scanValueStatusText,
                malwarePolicyName,  isRdp,  isAdminActivity,  isRegistryChanged,
                dataSource,  lastState);

        dgFileEventAfterEtl = fillGenericValuesForEmptyFields(dgFileEventAfterEtl);
        return dgFileEventAfterEtl;
    }

    public DgFileEventAfterEtl createEmptyDgEventAfterEtl() {
        return new DgFileEventAfterEtl();
    }

    private DgFileEventAfterEtl fillGenericValuesForEmptyFields(DgFileEventAfterEtl dgFileEventAfterEtl) {
        final Field[] fields = dgFileEventAfterEtl.getClass().getFields();
        for (Field field : fields) {
            try {
                final Object fieldValue = field.get(dgFileEventAfterEtl);
                if (fieldValue == null) {
                    field.set(dgFileEventAfterEtl, "some_" + field.getName());
                }

            } catch (IllegalAccessException e) {

                return null;
            }
        }
        return dgFileEventAfterEtl;
    }
}
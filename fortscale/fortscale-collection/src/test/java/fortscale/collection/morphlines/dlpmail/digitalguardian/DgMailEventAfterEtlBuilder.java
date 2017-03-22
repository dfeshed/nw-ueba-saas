package fortscale.collection.morphlines.dlpmail.digitalguardian;


import java.lang.reflect.Field;

public class DgMailEventAfterEtlBuilder {
    private String dateTime;
    private String dateTimeUnix;
    private String eventDescription;
    private String eventType;
    private String eventId;
    private String username;
    private String normalizedUsername;
    private String fullName;
    private String ipAddress;
    private String hostname;
    private String normalizedSrcMachine;
    private String application;
    private String destinationFile;
    private String detailFileSize;
    private String emailRecipient;
    private String emailRecipientDomain;
    private String emailSender;
    private String emailSubject;
    private String isExternal;
    private String numOfRecipients;
    private String isAttachmentExtensionBlacklisted;
    private String destinationDirectory;
    private String destinationFileExtension;
    private String wasClassified;
    private String wasBlocked;
    private String scanValueStatusText;
    private String policyName;
    private String dataSource;
    private String lastState;

    public DgMailEventAfterEtlBuilder setDateTime(String dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public DgMailEventAfterEtlBuilder setDateTimeUnix(String dateTimeUnix) {
        this.dateTimeUnix = dateTimeUnix;
        return this;
    }

    public DgMailEventAfterEtlBuilder setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
        return this;
    }

    public DgMailEventAfterEtlBuilder setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public DgMailEventAfterEtlBuilder setEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public DgMailEventAfterEtlBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public DgMailEventAfterEtlBuilder setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
        return this;
    }

    public DgMailEventAfterEtlBuilder setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public DgMailEventAfterEtlBuilder setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public DgMailEventAfterEtlBuilder setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public DgMailEventAfterEtlBuilder setNormalizedSrcMachine(String normalizedSrcMachine) {
        this.normalizedSrcMachine = normalizedSrcMachine;
        return this;
    }

    public DgMailEventAfterEtlBuilder setApplication(String application) {
        this.application = application;
        return this;
    }

    public DgMailEventAfterEtlBuilder setDestinationFile(String destinationFile) {
        this.destinationFile = destinationFile;
        return this;
    }

    public DgMailEventAfterEtlBuilder setDetailFileSize(String detailFileSize) {
        this.detailFileSize = detailFileSize;
        return this;
    }

    public DgMailEventAfterEtlBuilder setEmailRecipient(String emailRecipient) {
        this.emailRecipient = emailRecipient;
        return this;
    }

    public DgMailEventAfterEtlBuilder setEmailRecipientDomain(String emailRecipientDomain) {
        this.emailRecipientDomain = emailRecipientDomain;
        return this;
    }

    public DgMailEventAfterEtlBuilder setEmailSender(String emailSender) {
        this.emailSender = emailSender;
        return this;
    }

    public DgMailEventAfterEtlBuilder setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
        return this;
    }

    public DgMailEventAfterEtlBuilder setIsExternal(String isExternal) {
        this.isExternal = isExternal;
        return this;
    }

    public DgMailEventAfterEtlBuilder setNumOfRecipients(String numOfRecipients) {
        this.numOfRecipients = numOfRecipients;
        return this;
    }

    public DgMailEventAfterEtlBuilder setIsAttachmentExtensionBlacklisted(String isAttachmentExtensionBlacklisted) {
        this.isAttachmentExtensionBlacklisted = isAttachmentExtensionBlacklisted;
        return this;
    }

    public DgMailEventAfterEtlBuilder setDestinationDirectory(String destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
        return this;
    }

    public DgMailEventAfterEtlBuilder setDestinationFileExtension(String destinationFileExtension) {
        this.destinationFileExtension = destinationFileExtension;
        return this;
    }

    public DgMailEventAfterEtlBuilder setWasClassified(String wasClassified) {
        this.wasClassified = wasClassified;
        return this;
    }

    public DgMailEventAfterEtlBuilder setWasBlocked(String wasBlocked) {
        this.wasBlocked = wasBlocked;
        return this;
    }

    public DgMailEventAfterEtlBuilder setScanValueStatusText(String scanValueStatusText) {
        this.scanValueStatusText = scanValueStatusText;
        return this;
    }

    public DgMailEventAfterEtlBuilder setPolicyName(String policyName) {
        this.policyName = policyName;
        return this;
    }

    public DgMailEventAfterEtlBuilder setDataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public DgMailEventAfterEtlBuilder setLastState(String lastState) {
        this.lastState = lastState;
        return this;
    }

    public DgMailEventAfterEtl createDgEventAfterEtl() {
        DgMailEventAfterEtl dgMailEventAfterEtl = new DgMailEventAfterEtl(dateTime, dateTimeUnix, eventDescription, eventType, eventId, username, normalizedUsername, fullName, ipAddress, hostname, normalizedSrcMachine, application, destinationFile, detailFileSize, destinationDirectory, destinationFileExtension, isAttachmentExtensionBlacklisted, emailRecipient, emailRecipientDomain, emailSender, emailSubject, isExternal, numOfRecipients, wasClassified, wasBlocked, scanValueStatusText, policyName, dataSource, lastState);
        dgMailEventAfterEtl = fillGenericValuesForEmptyFields(dgMailEventAfterEtl);
        return dgMailEventAfterEtl;
    }

    public DgMailEventAfterEtl createEmptyDgEventAfterEtl() {
        return new DgMailEventAfterEtl();
    }

    private DgMailEventAfterEtl fillGenericValuesForEmptyFields(DgMailEventAfterEtl dgMailEventAfterEtl) {
        final Field[] fields = dgMailEventAfterEtl.getClass().getFields();
        for (Field field : fields) {
            try {
                final Object fieldValue = field.get(dgMailEventAfterEtl);
                if (fieldValue == null) {
                    field.set(dgMailEventAfterEtl, "some_" + field.getName());
                }

            } catch (IllegalAccessException e) {

                return null;
            }
        }
        return dgMailEventAfterEtl;
    }
}
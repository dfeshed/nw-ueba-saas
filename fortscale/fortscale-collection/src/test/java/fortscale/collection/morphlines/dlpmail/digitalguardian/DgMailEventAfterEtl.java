package fortscale.collection.morphlines.dlpmail.digitalguardian;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class DgMailEventAfterEtl {

    public String dateTime;
    public String dateTimeUnix;
    public String eventDescription;
    public String eventType;
    public String eventId;
    public String username;
    public String normalizedUsername;
    public String fullName;
    public String ipAddress;
    public String hostname;
    public String normalizedSrcMachine;
    public String application;
    public String destinationFile;
    public Integer detailFileSize;
    public String destinationDirectory;
    public String destinationFileExtension;
    public String isAttachmentExtensionBlacklisted;
    public String emailRecipient;
    public String emailRecipientDomain;
    public String emailSender;
    public String emailSubject;
    public String isExternal;
    public String numOfRecipients;
    public String wasClassified;
    public String wasBlocked;
    public String scanValueStatusText;
    public String policyName;
    public String dataSource;
    public String lastState;

    protected DgMailEventAfterEtl() {
    }

    public DgMailEventAfterEtl(String dateTime, String dateTimeUnix, String eventDescription, String eventType, String eventId, String username, String normalizedUsername, String fullName, String ipAddress, String hostname, String normalizedSrcMachine, String application, String destinationFile, Integer detailFileSize, String destinationDirectory, String destinationFileExtension, String isAttachmentExtensionBlacklisted, String emailRecipient, String emailRecipientDomain, String emailSender, String emailSubject, String isExternal, String numOfRecipients, String wasClassified, String wasBlocked, String scanValueStatusText, String policyName, String dataSource, String lastState) {
        this.dateTime = dateTime;
        this.dateTimeUnix = dateTimeUnix;
        this.eventDescription = eventDescription;
        this.eventType = eventType;
        this.eventId = eventId;
        this.username = username;
        this.normalizedUsername = normalizedUsername;
        this.fullName = fullName;
        this.ipAddress = ipAddress;
        this.hostname = hostname;
        this.normalizedSrcMachine = normalizedSrcMachine;
        this.application = application;
        this.destinationFile = destinationFile;
        this.detailFileSize = detailFileSize;
        this.destinationDirectory = destinationDirectory;
        this.destinationFileExtension = destinationFileExtension;
        this.isAttachmentExtensionBlacklisted = isAttachmentExtensionBlacklisted;
        this.emailRecipient = emailRecipient;
        this.emailRecipientDomain = emailRecipientDomain;
        this.emailSender = emailSender;
        this.emailSubject = emailSubject;
        this.isExternal = isExternal;
        this.numOfRecipients = numOfRecipients;
        this.wasClassified = wasClassified;
        this.wasBlocked = wasBlocked;
        this.scanValueStatusText = scanValueStatusText;
        this.policyName = policyName;
        this.dataSource = dataSource;
        this.lastState = lastState;
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(",");
        final Field[] fields = this.getClass().getFields();
        for (Field field : fields) {
            try {
                final String newElement = field.get(this).toString();
                switch (newElement) {
                    case "some_hostname":
                    case "some_normalizedSrcMachine":
                        stringJoiner.add("some_computerName");
                        break;
                    case "some_normalizedUsername":
                        stringJoiner.add("");
                        break;
                    case "some_policyName":
                        stringJoiner.add("");
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

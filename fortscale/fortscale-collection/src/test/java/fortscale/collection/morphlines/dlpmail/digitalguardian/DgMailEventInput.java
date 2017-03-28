package fortscale.collection.morphlines.dlpmail.digitalguardian;

import java.lang.reflect.Field;
import java.util.StringJoiner;


public class DgMailEventInput {

    public String agentLocalDate;
    public String agentLocalTime;
    public String agentUtcTime;
    public String application;
    public String computerName;
    public String computerType;
    public String customInt4;
    public String customString1;
    public String customString3;
    public String customString4;
    public String customString6;
    public String givenName;
    public String surname;
    public String userId;
    public String username;
    public String eventDisplayName;
    public String companyName;
    public String processSha1Hash;
    public String processSha256Hash;
    public String productName;
    public String productVersion;
    public String scanValueStatus;
    public String scanValueStatusLocalTime;
    public String scanValueStatusText;
    public String dllMD5Hash;
    public String dllName;
    public String dllSHA1Hash;
    public String dllSHA256Hash;
    public String dNSHostname;
    public String emailSender;
    public String emailSubject;
    public String eventDisplayName1;
    public String eventId;
    public String ipAddress;
    public String localPort;
    public String mD5Checksum;
    public String mD5Hash;
    public String networkDirection;
    public String operation;
    public String protocol;
    public String remotePort;
    public String urlPath;
    public String wasBlocked;
    public String wasClassified;
    public String wasFileCaptured;
    public String wasMobileDevice;
    public String wasPkiAuthenticated;
    public String wasPrivateAddress;
    public String wasRemovable;
    public String wasRuleViolation;
    public String wasScreenCaptured;
    public String wasSMIMEEncrypted;
    public String wasSMIMESigned;
    public String wasWireless;
    public String customID;
    public String deviceClass;
    public String deviceID;
    public String driveType;
    public String friendlyName;
    public String productID;
    public String policyName;
    public String serialNumber;
    public String storageBusType;
    public String supportsPredictFailure;
    public String vendor;
    public String vendorID;
    public String blockCode;
    public String bytesRead;
    public String bytesWritten;
    public String destinationDirectory;
    public String destinationFile;
    public String destinationFileEncryption;
    public String destinationFileExtension;
    public String detailEventID;
    public Integer detailFileSize;
    public String detailWasBlocked;
    public String emailDomainName;
    public String emailRecipient;
    public String emailRecipientType;
    public String printer;
    public String printerJobname;
    public String promptSurveyName;
    public String sourceDirectory;
    public String sourceFile;
    public String sourceFileEncryption;
    public String sourceFileExtension;
    public String userResponse;
    public String wasDestFileCaptured;
    public String wasDestinationClassified;
    public String wasDestinationRemovable;
    public String wasDetailRuleViolation;
    public String wasDetailScreenCaptured;
    public String wasSourceClassified;
    public String wasSourceFileCaptured;
    public String wasSourceRemovable;
    public String sourceDriveType;
    public String sourceDeviceID;
    public String destinationDriveType;
    public String destinationDeviceID;
    public String emailAddress;
    public String employeeId;
    public String registryDestinationPath;
    public String registrySourcePath;
    public String registryValue;
    public String registryValueType;


    protected DgMailEventInput() {
    }

    public DgMailEventInput(String agentLocalDate, String agentLocalTime, String agentUtcTime, String application, String computerName, String computerType, String customInt4, String customString1, String customString3, String customString4, String customString6, String givenName, String surname, String userId, String username, String eventDisplayName, String companyName, String processSha1Hash, String processSha256Hash, String productName, String productVersion, String scanValueStatus, String scanValueStatusLocalTime, String scanValueStatusText, String dllMD5Hash, String dllName, String dllSHA1Hash, String dllSHA256Hash, String dNSHostname, String emailSender, String emailSubject, String eventDisplayName1, String eventId, String ipAddress, String localPort, String mD5Checksum, String mD5Hash, String networkDirection, String operation, String protocol, String remotePort, String urlPath, String wasBlocked, String wasClassified, String wasFileCaptured, String wasMobileDevice, String wasPkiAuthenticated, String wasPrivateAddress, String wasRemovable, String wasRuleViolation, String wasScreenCaptured, String wasSMIMEEncrypted, String wasSMIMESigned, String wasWireless, String customID, String deviceClass, String deviceID, String driveType, String friendlyName, String productID, String policyName, String serialNumber, String storageBusType, String supportsPredictFailure, String vendor, String vendorID, String blockCode, String bytesRead, String bytesWritten, String destinationDirectory, String destinationFile, String destinationFileEncryption, String destinationFileExtension, String detailEventID, Integer
            detailFileSize, String detailWasBlocked, String emailDomainName, String emailRecipient, String emailRecipientType, String printer, String printerJobname, String promptSurveyName, String sourceDirectory, String sourceFile, String sourceFileEncryption, String sourceFileExtension, String userResponse, String wasDestFileCaptured, String wasDestinationClassified, String wasDestinationRemovable, String wasDetailRuleViolation, String wasDetailScreenCaptured, String wasSourceClassified, String wasSourceFileCaptured, String wasSourceRemovable, String sourceDriveType, String sourceDeviceID, String destinationDriveType, String destinationDeviceID, String emailAddress, String employeeId, String registryDestinationPath, String registrySourcePath, String registryValue, String registryValueType) {
        this.agentLocalDate = agentLocalDate;
        this.agentLocalTime = agentLocalTime;
        this.agentUtcTime = agentUtcTime;
        this.application = application;
        this.computerName = computerName;
        this.computerType = computerType;
        this.customInt4 = customInt4;
        this.customString1 = customString1;
        this.customString3 = customString3;
        this.customString4 = customString4;
        this.customString6 = customString6;
        this.givenName = givenName;
        this.surname = surname;
        this.userId = userId;
        this.username = username;
        this.eventDisplayName = eventDisplayName;
        this.companyName = companyName;
        this.processSha1Hash = processSha1Hash;
        this.processSha256Hash = processSha256Hash;
        this.productName = productName;
        this.productVersion = productVersion;
        this.scanValueStatus = scanValueStatus;
        this.scanValueStatusLocalTime = scanValueStatusLocalTime;
        this.scanValueStatusText = scanValueStatusText;
        this.dllMD5Hash = dllMD5Hash;
        this.dllName = dllName;
        this.dllSHA1Hash = dllSHA1Hash;
        this.dllSHA256Hash = dllSHA256Hash;
        this.dNSHostname = dNSHostname;
        this.emailSender = emailSender;
        this.emailSubject = emailSubject;
        this.eventDisplayName1 = eventDisplayName1;
        this.eventId = eventId;
        this.ipAddress = ipAddress;
        this.localPort = localPort;
        this.mD5Checksum = mD5Checksum;
        this.mD5Hash = mD5Hash;
        this.networkDirection = networkDirection;
        this.operation = operation;
        this.protocol = protocol;
        this.remotePort = remotePort;
        this.urlPath = urlPath;
        this.wasBlocked = wasBlocked;
        this.wasClassified = wasClassified;
        this.wasFileCaptured = wasFileCaptured;
        this.wasMobileDevice = wasMobileDevice;
        this.wasPkiAuthenticated = wasPkiAuthenticated;
        this.wasPrivateAddress = wasPrivateAddress;
        this.wasRemovable = wasRemovable;
        this.wasRuleViolation = wasRuleViolation;
        this.wasScreenCaptured = wasScreenCaptured;
        this.wasSMIMEEncrypted = wasSMIMEEncrypted;
        this.wasSMIMESigned = wasSMIMESigned;
        this.wasWireless = wasWireless;
        this.customID = customID;
        this.deviceClass = deviceClass;
        this.deviceID = deviceID;
        this.driveType = driveType;
        this.friendlyName = friendlyName;
        this.productID = productID;
        this.policyName = policyName;
        this.serialNumber = serialNumber;
        this.storageBusType = storageBusType;
        this.supportsPredictFailure = supportsPredictFailure;
        this.vendor = vendor;
        this.vendorID = vendorID;
        this.blockCode = blockCode;
        this.bytesRead = bytesRead;
        this.bytesWritten = bytesWritten;
        this.destinationDirectory = destinationDirectory;
        this.destinationFile = destinationFile;
        this.destinationFileEncryption = destinationFileEncryption;
        this.destinationFileExtension = destinationFileExtension;
        this.detailEventID = detailEventID;
        this.detailFileSize = detailFileSize;
        this.detailWasBlocked = detailWasBlocked;
        this.emailDomainName = emailDomainName;
        this.emailRecipient = emailRecipient;
        this.emailRecipientType = emailRecipientType;
        this.printer = printer;
        this.printerJobname = printerJobname;
        this.promptSurveyName = promptSurveyName;
        this.sourceDirectory = sourceDirectory;
        this.sourceFile = sourceFile;
        this.sourceFileEncryption = sourceFileEncryption;
        this.sourceFileExtension = sourceFileExtension;
        this.userResponse = userResponse;
        this.wasDestFileCaptured = wasDestFileCaptured;
        this.wasDestinationClassified = wasDestinationClassified;
        this.wasDestinationRemovable = wasDestinationRemovable;
        this.wasDetailRuleViolation = wasDetailRuleViolation;
        this.wasDetailScreenCaptured = wasDetailScreenCaptured;
        this.wasSourceClassified = wasSourceClassified;
        this.wasSourceFileCaptured = wasSourceFileCaptured;
        this.wasSourceRemovable = wasSourceRemovable;
        this.sourceDriveType = sourceDriveType;
        this.sourceDeviceID = sourceDeviceID;
        this.destinationDriveType = destinationDriveType;
        this.destinationDeviceID = destinationDeviceID;
        this.emailAddress = emailAddress;
        this.employeeId = employeeId;
        this.registryDestinationPath = registryDestinationPath;
        this.registrySourcePath = registrySourcePath;
        this.registryValue = registryValue;
        this.registryValueType = registryValueType;
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(",");
        final Field[] fields = this.getClass().getFields();
        for (Field field : fields) {
            try {
                stringJoiner.add(field.get(this).toString());
            } catch (IllegalAccessException e) {
                return null;
            }
        }

        return stringJoiner.toString();
    }
}

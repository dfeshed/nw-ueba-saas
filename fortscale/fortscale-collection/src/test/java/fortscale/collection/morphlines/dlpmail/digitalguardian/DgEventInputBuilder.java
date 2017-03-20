package fortscale.collection.morphlines.dlpmail.digitalguardian;

import fortscale.utils.logging.Logger;

import java.lang.reflect.Field;


public class DgEventInputBuilder {

    private static final Logger logger = Logger.getLogger(DgEventInputBuilder.class);

    private String agentLocalDate;
    private String agentLocalTime;
    private String agentUtcTime;
    private String application;
    private String computerName; //hostname
    private String computerType;
    private String customInt4;
    private String customString1;
    private String customString3;
    private String customString4;
    private String customString6;
    private String givenName;
    private String surname;
    private String userId;
    private String username;
    private String eventDisplayName;
    private String companyName;
    private String processSha1Hash;
    private String processSha256Hash;
    private String productName;
    private String productVersion;
    private String scanValueStatus;
    private String scanValueStatusLocalTime;
    private String scanValueStatusText;
    private String dllMD5Hash;
    private String dllName;
    private String dllSHA1Hash;
    private String dllSHA256Hash;
    private String dNSHostname;
    private String emailSender;
    private String emailSubject;
    private String eventDisplayName1;
    private String eventId;
    private String ipAddress;
    private String localPort;
    private String mD5Checksum;
    private String mD5Hash;
    private String networkDirection;
    private String operation;
    private String protocol;
    private String remotePort;
    private String urlPath;
    private String wasBlocked;
    private String wasClassified;
    private String wasFileCaptured;
    private String wasMobileDevice;
    private String wasPkiAuthenticated;
    private String wasPrivateAddress;
    private String wasRemovable;
    private String wasRuleViolation;
    private String wasScreenCaptured;
    private String wasSMIMEEncrypted;
    private String wasSMIMESigned;
    private String wasWireless;
    private String customID;
    private String deviceClass;
    private String deviceID;
    private String driveType;
    private String friendlyName;
    private String productID;
    private String removalPolicy;
    private String serialNumber;
    private String storageBusType;
    private String supportsPredictFailure;
    private String vendor;
    private String vendorID;
    private String blockCode;
    private String bytesRead;
    private String bytesWritten;
    private String destinationDirectory;
    private String destinationFile;
    private String destinationFileEncryption;
    private String destinationFileExtension;
    private String detailEventID;
    private String detailFileSize;
    private String detailWasBlocked;
    private String emailDomainName;
    private String emailRecipient;
    private String emailRecipientType;
    private String printer;
    private String printerJobname;
    private String promptSurveyName;
    private String sourceDirectory;
    private String sourceFile;
    private String sourceFileEncryption;
    private String sourceFileExtension;
    private String userResponse;
    private String wasDestFileCaptured;
    private String wasDestinationClassified;
    private String wasDestinationRemovable;
    private String wasDetailRuleViolation;
    private String wasDetailScreenCaptured;
    private String wasSourceClassified;
    private String wasSourceFileCaptured;
    private String wasSourceRemovable;
    private String sourceDriveType;
    private String sourceDeviceID;
    private String destinationDriveType;
    private String destinationDeviceID;
    private String emailAddress;
    private String employeeId;
    private String registryDestinationPath;
    private String registrySourcePath;
    private String registryValue;
    private String registryValueType;

    public DgEventInputBuilder setAgentLocalDate(String agentLocalDate) {
        this.agentLocalDate = agentLocalDate;
        return this;
    }

    public DgEventInputBuilder setAgentLocalTime(String agentLocalTime) {
        this.agentLocalTime = agentLocalTime;
        return this;
    }

    public DgEventInputBuilder setAgentUtcTime(String agentUtcTime) {
        this.agentUtcTime = agentUtcTime;
        return this;
    }

    public DgEventInputBuilder setApplication(String application) {
        this.application = application;
        return this;
    }

    public DgEventInputBuilder setComputerName(String computerName) {
        this.computerName = computerName;
        return this;
    }

    public DgEventInputBuilder setComputerType(String computerType) {
        this.computerType = computerType;
        return this;
    }

    public DgEventInputBuilder setCustomInt4(String customInt4) {
        this.customInt4 = customInt4;
        return this;
    }

    public DgEventInputBuilder setCustomString1(String customString1) {
        this.customString1 = customString1;
        return this;
    }

    public DgEventInputBuilder setCustomString3(String customString3) {
        this.customString3 = customString3;
        return this;
    }

    public DgEventInputBuilder setCustomString4(String customString4) {
        this.customString4 = customString4;
        return this;
    }

    public DgEventInputBuilder setCustomString6(String customString6) {
        this.customString6 = customString6;
        return this;
    }

    public DgEventInputBuilder setGivenName(String givenName) {
        this.givenName = givenName;
        return this;
    }

    public DgEventInputBuilder setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public DgEventInputBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public DgEventInputBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public DgEventInputBuilder setEventDisplayName(String eventDisplayName) {
        this.eventDisplayName = eventDisplayName;
        return this;
    }

    public DgEventInputBuilder setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public DgEventInputBuilder setProcessSha1Hash(String processSha1Hash) {
        this.processSha1Hash = processSha1Hash;
        return this;
    }

    public DgEventInputBuilder setProcessSha256Hash(String processSha256Hash) {
        this.processSha256Hash = processSha256Hash;
        return this;
    }

    public DgEventInputBuilder setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public DgEventInputBuilder setProductVersion(String productVersion) {
        this.productVersion = productVersion;
        return this;
    }

    public DgEventInputBuilder setScanValueStatus(String scanValueStatus) {
        this.scanValueStatus = scanValueStatus;
        return this;
    }

    public DgEventInputBuilder setScanValueStatusLocalTime(String scanValueStatusLocalTime) {
        this.scanValueStatusLocalTime = scanValueStatusLocalTime;
        return this;
    }

    public DgEventInputBuilder setScanValueStatusText(String scanValueStatusText) {
        this.scanValueStatusText = scanValueStatusText;
        return this;
    }

    public DgEventInputBuilder setDllMD5Hash(String dllMD5Hash) {
        this.dllMD5Hash = dllMD5Hash;
        return this;
    }

    public DgEventInputBuilder setDllName(String dllName) {
        this.dllName = dllName;
        return this;
    }

    public DgEventInputBuilder setDllSHA1Hash(String dllSHA1Hash) {
        this.dllSHA1Hash = dllSHA1Hash;
        return this;
    }

    public DgEventInputBuilder setDllSHA256Hash(String dllSHA256Hash) {
        this.dllSHA256Hash = dllSHA256Hash;
        return this;
    }

    public DgEventInputBuilder setDnsHostname(String dNSHostname) {
        this.dNSHostname = dNSHostname;
        return this;
    }

    public DgEventInputBuilder setEmailSender(String emailSender) {
        this.emailSender = emailSender;
        return this;
    }

    public DgEventInputBuilder setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
        return this;
    }

    public DgEventInputBuilder setEventDisplayName1(String eventDisplayName1) {
        this.eventDisplayName1 = eventDisplayName1;
        return this;
    }

    public DgEventInputBuilder setEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public DgEventInputBuilder setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public DgEventInputBuilder setLocalPort(String localPort) {
        this.localPort = localPort;
        return this;
    }

    public DgEventInputBuilder setmD5Checksum(String mD5Checksum) {
        this.mD5Checksum = mD5Checksum;
        return this;
    }

    public DgEventInputBuilder setmD5Hash(String mD5Hash) {
        this.mD5Hash = mD5Hash;
        return this;
    }

    public DgEventInputBuilder setNetworkDirection(String networkDirection) {
        this.networkDirection = networkDirection;
        return this;
    }

    public DgEventInputBuilder setOperation(String operation) {
        this.operation = operation;
        return this;
    }

    public DgEventInputBuilder setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public DgEventInputBuilder setRemotePort(String remotePort) {
        this.remotePort = remotePort;
        return this;
    }

    public DgEventInputBuilder setUrlPath(String urlPath) {
        this.urlPath = urlPath;
        return this;
    }

    public DgEventInputBuilder setWasBlocked(String wasBlocked) {
        this.wasBlocked = wasBlocked;
        return this;
    }

    public DgEventInputBuilder setWasClassified(String wasClassified) {
        this.wasClassified = wasClassified;
        return this;
    }

    public DgEventInputBuilder setWasFileCaptured(String wasFileCaptured) {
        this.wasFileCaptured = wasFileCaptured;
        return this;
    }

    public DgEventInputBuilder setWasMobileDevice(String wasMobileDevice) {
        this.wasMobileDevice = wasMobileDevice;
        return this;
    }

    public DgEventInputBuilder setWasPkiAuthenticated(String wasPkiAuthenticated) {
        this.wasPkiAuthenticated = wasPkiAuthenticated;
        return this;
    }

    public DgEventInputBuilder setWasPrivateAddress(String wasPrivateAddress) {
        this.wasPrivateAddress = wasPrivateAddress;
        return this;
    }

    public DgEventInputBuilder setWasRemovable(String wasRemovable) {
        this.wasRemovable = wasRemovable;
        return this;
    }

    public DgEventInputBuilder setWasRuleViolation(String wasRuleViolation) {
        this.wasRuleViolation = wasRuleViolation;
        return this;
    }

    public DgEventInputBuilder setWasScreenCaptured(String wasScreenCaptured) {
        this.wasScreenCaptured = wasScreenCaptured;
        return this;
    }

    public DgEventInputBuilder setWasSMIMEEncrypted(String wasSMIMEEncrypted) {
        this.wasSMIMEEncrypted = wasSMIMEEncrypted;
        return this;
    }

    public DgEventInputBuilder setWasSMIMESigned(String wasSMIMESigned) {
        this.wasSMIMESigned = wasSMIMESigned;
        return this;
    }

    public DgEventInputBuilder setWasWireless(String wasWireless) {
        this.wasWireless = wasWireless;
        return this;
    }

    public DgEventInputBuilder setCustomID(String customID) {
        this.customID = customID;
        return this;
    }

    public DgEventInputBuilder setDeviceClass(String deviceClass) {
        this.deviceClass = deviceClass;
        return this;
    }

    public DgEventInputBuilder setDeviceID(String deviceID) {
        this.deviceID = deviceID;
        return this;
    }

    public DgEventInputBuilder setDriveType(String driveType) {
        this.driveType = driveType;
        return this;
    }

    public DgEventInputBuilder setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
        return this;
    }

    public DgEventInputBuilder setProductID(String productID) {
        this.productID = productID;
        return this;
    }

    public DgEventInputBuilder setRemovalPolicy(String removalPolicy) {
        this.removalPolicy = removalPolicy;
        return this;
    }

    public DgEventInputBuilder setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public DgEventInputBuilder setStorageBusType(String storageBusType) {
        this.storageBusType = storageBusType;
        return this;
    }

    public DgEventInputBuilder setSupportsPredictFailure(String supportsPredictFailure) {
        this.supportsPredictFailure = supportsPredictFailure;
        return this;
    }

    public DgEventInputBuilder setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public DgEventInputBuilder setVendorID(String vendorID) {
        this.vendorID = vendorID;
        return this;
    }

    public DgEventInputBuilder setBlockCode(String blockCode) {
        this.blockCode = blockCode;
        return this;
    }

    public DgEventInputBuilder setBytesRead(String bytesRead) {
        this.bytesRead = bytesRead;
        return this;
    }

    public DgEventInputBuilder setBytesWritten(String bytesWritten) {
        this.bytesWritten = bytesWritten;
        return this;
    }

    public DgEventInputBuilder setDestinationDirectory(String destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
        return this;
    }

    public DgEventInputBuilder setDestinationFile(String destinationFile) {
        this.destinationFile = destinationFile;
        return this;
    }

    public DgEventInputBuilder setDestinationFileEncryption(String destinationFileEncryption) {
        this.destinationFileEncryption = destinationFileEncryption;
        return this;
    }

    public DgEventInputBuilder setDestinationFileExtension(String destinationFileExtension) {
        this.destinationFileExtension = destinationFileExtension;
        return this;
    }

    public DgEventInputBuilder setDetailEventID(String detailEventID) {
        this.detailEventID = detailEventID;
        return this;
    }

    public DgEventInputBuilder setDetailFileSize(String detailFileSize) {
        this.detailFileSize = detailFileSize;
        return this;
    }

    public DgEventInputBuilder setDetailWasBlocked(String detailWasBlocked) {
        this.detailWasBlocked = detailWasBlocked;
        return this;
    }

    public DgEventInputBuilder setEmailDomainName(String emailDomainName) {
        this.emailDomainName = emailDomainName;
        return this;
    }

    public DgEventInputBuilder setEmailRecipient(String emailRecipient) {
        this.emailRecipient = emailRecipient;
        return this;
    }

    public DgEventInputBuilder setEmailRecipientType(String emailRecipientType) {
        this.emailRecipientType = emailRecipientType;
        return this;
    }

    public DgEventInputBuilder setPrinter(String printer) {
        this.printer = printer;
        return this;
    }

    public DgEventInputBuilder setPrinterJobname(String printerJobname) {
        this.printerJobname = printerJobname;
        return this;
    }

    public DgEventInputBuilder setPromptSurveyName(String promptSurveyName) {
        this.promptSurveyName = promptSurveyName;
        return this;
    }

    public DgEventInputBuilder setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
        return this;
    }

    public DgEventInputBuilder setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
        return this;
    }

    public DgEventInputBuilder setSourceFileEncryption(String sourceFileEncryption) {
        this.sourceFileEncryption = sourceFileEncryption;
        return this;
    }

    public DgEventInputBuilder setSourceFileExtension(String sourceFileExtension) {
        this.sourceFileExtension = sourceFileExtension;
        return this;
    }

    public DgEventInputBuilder setUserResponse(String userResponse) {
        this.userResponse = userResponse;
        return this;
    }

    public DgEventInputBuilder setWasDestFileCaptured(String wasDestFileCaptured) {
        this.wasDestFileCaptured = wasDestFileCaptured;
        return this;
    }

    public DgEventInputBuilder setWasDestinationClassified(String wasDestinationClassified) {
        this.wasDestinationClassified = wasDestinationClassified;
        return this;
    }

    public DgEventInputBuilder setWasDestinationRemovable(String wasDestinationRemovable) {
        this.wasDestinationRemovable = wasDestinationRemovable;
        return this;
    }

    public DgEventInputBuilder setWasDetailRuleViolation(String wasDetailRuleViolation) {
        this.wasDetailRuleViolation = wasDetailRuleViolation;
        return this;
    }

    public DgEventInputBuilder setWasDetailScreenCaptured(String wasDetailScreenCaptured) {
        this.wasDetailScreenCaptured = wasDetailScreenCaptured;
        return this;
    }

    public DgEventInputBuilder setWasSourceClassified(String wasSourceClassified) {
        this.wasSourceClassified = wasSourceClassified;
        return this;
    }

    public DgEventInputBuilder setWasSourceFileCaptured(String wasSourceFileCaptured) {
        this.wasSourceFileCaptured = wasSourceFileCaptured;
        return this;
    }

    public DgEventInputBuilder setWasSourceRemovable(String wasSourceRemovable) {
        this.wasSourceRemovable = wasSourceRemovable;
        return this;
    }

    public DgEventInputBuilder setSourceDriveType(String sourceDriveType) {
        this.sourceDriveType = sourceDriveType;
        return this;
    }

    public DgEventInputBuilder setSourceDeviceID(String sourceDeviceID) {
        this.sourceDeviceID = sourceDeviceID;
        return this;
    }

    public DgEventInputBuilder setDestinationDriveType(String destinationDriveType) {
        this.destinationDriveType = destinationDriveType;
        return this;
    }

    public DgEventInputBuilder setDestinationDeviceID(String destinationDeviceID) {
        this.destinationDeviceID = destinationDeviceID;
        return this;
    }

    public DgEventInputBuilder setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public DgEventInputBuilder setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public DgEventInputBuilder setRegistryDestinationPath(String registryDestinationPath) {
        this.registryDestinationPath = registryDestinationPath;
        return this;
    }

    public DgEventInputBuilder setRegistrySourcePath(String registrySourcePath) {
        this.registrySourcePath = registrySourcePath;
        return this;
    }

    public DgEventInputBuilder setRegistryValue(String registryValue) {
        this.registryValue = registryValue;
        return this;
    }

    public DgEventInputBuilder setRegistryValueType(String registryValueType) {
        this.registryValueType = registryValueType;
        return this;
    }

    public DgMailEventInput createDgEvent() {
        DgMailEventInput dgMailEventInput = new DgMailEventInput(agentLocalDate, agentLocalTime, agentUtcTime, application, computerName, computerType, customInt4, customString1, customString3, customString4, customString6, givenName, surname, userId, username, eventDisplayName, companyName, processSha1Hash, processSha256Hash, productName, productVersion, scanValueStatus, scanValueStatusLocalTime, scanValueStatusText, dllMD5Hash, dllName, dllSHA1Hash, dllSHA256Hash, dNSHostname, emailSender, emailSubject, eventDisplayName1, eventId, ipAddress, localPort, mD5Checksum, mD5Hash, networkDirection, operation, protocol, remotePort, urlPath, wasBlocked, wasClassified, wasFileCaptured, wasMobileDevice, wasPkiAuthenticated, wasPrivateAddress, wasRemovable, wasRuleViolation, wasScreenCaptured, wasSMIMEEncrypted, wasSMIMESigned, wasWireless, customID, deviceClass, deviceID, driveType, friendlyName, productID, removalPolicy, serialNumber, storageBusType, supportsPredictFailure, vendor, vendorID, blockCode, bytesRead, bytesWritten, destinationDirectory, destinationFile, destinationFileEncryption, destinationFileExtension, detailEventID, detailFileSize, detailWasBlocked, emailDomainName, emailRecipient, emailRecipientType, printer, printerJobname, promptSurveyName, sourceDirectory, sourceFile, sourceFileEncryption, sourceFileExtension, userResponse, wasDestFileCaptured, wasDestinationClassified, wasDestinationRemovable, wasDetailRuleViolation, wasDetailScreenCaptured, wasSourceClassified, wasSourceFileCaptured, wasSourceRemovable, sourceDriveType, sourceDeviceID, destinationDriveType, destinationDeviceID, emailAddress, employeeId, registryDestinationPath, registrySourcePath, registryValue, registryValueType);
        dgMailEventInput = fillGenericValuesForEmptyFields(dgMailEventInput);
        return dgMailEventInput;
    }

    public DgMailEventInput createEmptyDgEvent() {
        return new DgMailEventInput();
    }

    private DgMailEventInput fillGenericValuesForEmptyFields(DgMailEventInput dgMailEventInput) {
        final Field[] fields = dgMailEventInput.getClass().getFields();
        for (Field field : fields) {
            try {
                final Object fieldValue = field.get(dgMailEventInput);
                if (fieldValue == null) {
                    field.set(dgMailEventInput, "some_" + field.getName());
                }

            } catch (IllegalAccessException e) {

                return null;
            }
        }
        return dgMailEventInput;
    }
}


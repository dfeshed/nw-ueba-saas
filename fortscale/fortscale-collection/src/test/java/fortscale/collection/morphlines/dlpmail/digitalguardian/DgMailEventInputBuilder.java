package fortscale.collection.morphlines.dlpmail.digitalguardian;

import fortscale.utils.logging.Logger;

import java.lang.reflect.Field;


public class DgMailEventInputBuilder  {

    private static final Logger logger = Logger.getLogger(DgMailEventInputBuilder.class);

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
    private Integer detailFileSize;
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

    public DgMailEventInputBuilder setAgentLocalDate(String agentLocalDate) {
        this.agentLocalDate = agentLocalDate;
        return this;
    }

    public DgMailEventInputBuilder setAgentLocalTime(String agentLocalTime) {
        this.agentLocalTime = agentLocalTime;
        return this;
    }

    public DgMailEventInputBuilder setAgentUtcTime(String agentUtcTime) {
        this.agentUtcTime = agentUtcTime;
        return this;
    }

    public DgMailEventInputBuilder setApplication(String application) {
        this.application = application;
        return this;
    }

    public DgMailEventInputBuilder setComputerName(String computerName) {
        this.computerName = computerName;
        return this;
    }

    public DgMailEventInputBuilder setComputerType(String computerType) {
        this.computerType = computerType;
        return this;
    }

    public DgMailEventInputBuilder setCustomInt4(String customInt4) {
        this.customInt4 = customInt4;
        return this;
    }

    public DgMailEventInputBuilder setCustomString1(String customString1) {
        this.customString1 = customString1;
        return this;
    }

    public DgMailEventInputBuilder setCustomString3(String customString3) {
        this.customString3 = customString3;
        return this;
    }

    public DgMailEventInputBuilder setCustomString4(String customString4) {
        this.customString4 = customString4;
        return this;
    }

    public DgMailEventInputBuilder setCustomString6(String customString6) {
        this.customString6 = customString6;
        return this;
    }

    public DgMailEventInputBuilder setGivenName(String givenName) {
        this.givenName = givenName;
        return this;
    }

    public DgMailEventInputBuilder setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public DgMailEventInputBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public DgMailEventInputBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public DgMailEventInputBuilder setEventDisplayName(String eventDisplayName) {
        this.eventDisplayName = eventDisplayName;
        return this;
    }

    public DgMailEventInputBuilder setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public DgMailEventInputBuilder setProcessSha1Hash(String processSha1Hash) {
        this.processSha1Hash = processSha1Hash;
        return this;
    }

    public DgMailEventInputBuilder setProcessSha256Hash(String processSha256Hash) {
        this.processSha256Hash = processSha256Hash;
        return this;
    }

    public DgMailEventInputBuilder setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public DgMailEventInputBuilder setProductVersion(String productVersion) {
        this.productVersion = productVersion;
        return this;
    }

    public DgMailEventInputBuilder setScanValueStatus(String scanValueStatus) {
        this.scanValueStatus = scanValueStatus;
        return this;
    }

    public DgMailEventInputBuilder setScanValueStatusLocalTime(String scanValueStatusLocalTime) {
        this.scanValueStatusLocalTime = scanValueStatusLocalTime;
        return this;
    }

    public DgMailEventInputBuilder setScanValueStatusText(String scanValueStatusText) {
        this.scanValueStatusText = scanValueStatusText;
        return this;
    }

    public DgMailEventInputBuilder setDllMD5Hash(String dllMD5Hash) {
        this.dllMD5Hash = dllMD5Hash;
        return this;
    }

    public DgMailEventInputBuilder setDllName(String dllName) {
        this.dllName = dllName;
        return this;
    }

    public DgMailEventInputBuilder setDllSHA1Hash(String dllSHA1Hash) {
        this.dllSHA1Hash = dllSHA1Hash;
        return this;
    }

    public DgMailEventInputBuilder setDllSHA256Hash(String dllSHA256Hash) {
        this.dllSHA256Hash = dllSHA256Hash;
        return this;
    }

    public DgMailEventInputBuilder setDnsHostname(String dNSHostname) {
        this.dNSHostname = dNSHostname;
        return this;
    }

    public DgMailEventInputBuilder setEmailSender(String emailSender) {
        this.emailSender = emailSender;
        return this;
    }

    public DgMailEventInputBuilder setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
        return this;
    }

    public DgMailEventInputBuilder setEventDisplayName1(String eventDisplayName1) {
        this.eventDisplayName1 = eventDisplayName1;
        return this;
    }

    public DgMailEventInputBuilder setEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public DgMailEventInputBuilder setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public DgMailEventInputBuilder setLocalPort(String localPort) {
        this.localPort = localPort;
        return this;
    }

    public DgMailEventInputBuilder setmD5Checksum(String mD5Checksum) {
        this.mD5Checksum = mD5Checksum;
        return this;
    }

    public DgMailEventInputBuilder setmD5Hash(String mD5Hash) {
        this.mD5Hash = mD5Hash;
        return this;
    }

    public DgMailEventInputBuilder setNetworkDirection(String networkDirection) {
        this.networkDirection = networkDirection;
        return this;
    }

    public DgMailEventInputBuilder setOperation(String operation) {
        this.operation = operation;
        return this;
    }

    public DgMailEventInputBuilder setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public DgMailEventInputBuilder setRemotePort(String remotePort) {
        this.remotePort = remotePort;
        return this;
    }

    public DgMailEventInputBuilder setUrlPath(String urlPath) {
        this.urlPath = urlPath;
        return this;
    }

    public DgMailEventInputBuilder setWasBlocked(String wasBlocked) {
        this.wasBlocked = wasBlocked;
        return this;
    }

    public DgMailEventInputBuilder setWasClassified(String wasClassified) {
        this.wasClassified = wasClassified;
        return this;
    }

    public DgMailEventInputBuilder setWasFileCaptured(String wasFileCaptured) {
        this.wasFileCaptured = wasFileCaptured;
        return this;
    }

    public DgMailEventInputBuilder setWasMobileDevice(String wasMobileDevice) {
        this.wasMobileDevice = wasMobileDevice;
        return this;
    }

    public DgMailEventInputBuilder setWasPkiAuthenticated(String wasPkiAuthenticated) {
        this.wasPkiAuthenticated = wasPkiAuthenticated;
        return this;
    }

    public DgMailEventInputBuilder setWasPrivateAddress(String wasPrivateAddress) {
        this.wasPrivateAddress = wasPrivateAddress;
        return this;
    }

    public DgMailEventInputBuilder setWasRemovable(String wasRemovable) {
        this.wasRemovable = wasRemovable;
        return this;
    }

    public DgMailEventInputBuilder setWasRuleViolation(String wasRuleViolation) {
        this.wasRuleViolation = wasRuleViolation;
        return this;
    }

    public DgMailEventInputBuilder setWasScreenCaptured(String wasScreenCaptured) {
        this.wasScreenCaptured = wasScreenCaptured;
        return this;
    }

    public DgMailEventInputBuilder setWasSMIMEEncrypted(String wasSMIMEEncrypted) {
        this.wasSMIMEEncrypted = wasSMIMEEncrypted;
        return this;
    }

    public DgMailEventInputBuilder setWasSMIMESigned(String wasSMIMESigned) {
        this.wasSMIMESigned = wasSMIMESigned;
        return this;
    }

    public DgMailEventInputBuilder setWasWireless(String wasWireless) {
        this.wasWireless = wasWireless;
        return this;
    }

    public DgMailEventInputBuilder setCustomID(String customID) {
        this.customID = customID;
        return this;
    }

    public DgMailEventInputBuilder setDeviceClass(String deviceClass) {
        this.deviceClass = deviceClass;
        return this;
    }

    public DgMailEventInputBuilder setDeviceID(String deviceID) {
        this.deviceID = deviceID;
        return this;
    }

    public DgMailEventInputBuilder setDriveType(String driveType) {
        this.driveType = driveType;
        return this;
    }

    public DgMailEventInputBuilder setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
        return this;
    }

    public DgMailEventInputBuilder setProductID(String productID) {
        this.productID = productID;
        return this;
    }

    public DgMailEventInputBuilder setRemovalPolicy(String removalPolicy) {
        this.removalPolicy = removalPolicy;
        return this;
    }

    public DgMailEventInputBuilder setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public DgMailEventInputBuilder setStorageBusType(String storageBusType) {
        this.storageBusType = storageBusType;
        return this;
    }

    public DgMailEventInputBuilder setSupportsPredictFailure(String supportsPredictFailure) {
        this.supportsPredictFailure = supportsPredictFailure;
        return this;
    }

    public DgMailEventInputBuilder setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public DgMailEventInputBuilder setVendorID(String vendorID) {
        this.vendorID = vendorID;
        return this;
    }

    public DgMailEventInputBuilder setBlockCode(String blockCode) {
        this.blockCode = blockCode;
        return this;
    }

    public DgMailEventInputBuilder setBytesRead(String bytesRead) {
        this.bytesRead = bytesRead;
        return this;
    }

    public DgMailEventInputBuilder setBytesWritten(String bytesWritten) {
        this.bytesWritten = bytesWritten;
        return this;
    }

    public DgMailEventInputBuilder setDestinationDirectory(String destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
        return this;
    }

    public DgMailEventInputBuilder setDestinationFile(String destinationFile) {
        this.destinationFile = destinationFile;
        return this;
    }

    public DgMailEventInputBuilder setDestinationFileEncryption(String destinationFileEncryption) {
        this.destinationFileEncryption = destinationFileEncryption;
        return this;
    }

    public DgMailEventInputBuilder setDestinationFileExtension(String destinationFileExtension) {
        this.destinationFileExtension = destinationFileExtension;
        return this;
    }

    public DgMailEventInputBuilder setDetailEventID(String detailEventID) {
        this.detailEventID = detailEventID;
        return this;
    }

    public DgMailEventInputBuilder setDetailFileSize(Integer detailFileSize) {
        this.detailFileSize = Integer.valueOf(detailFileSize);
        return this;
    }

    public DgMailEventInputBuilder setDetailWasBlocked(String detailWasBlocked) {
        this.detailWasBlocked = detailWasBlocked;
        return this;
    }

    public DgMailEventInputBuilder setEmailDomainName(String emailDomainName) {
        this.emailDomainName = emailDomainName;
        return this;
    }

    public DgMailEventInputBuilder setEmailRecipient(String emailRecipient) {
        this.emailRecipient = emailRecipient;
        return this;
    }

    public DgMailEventInputBuilder setEmailRecipientType(String emailRecipientType) {
        this.emailRecipientType = emailRecipientType;
        return this;
    }

    public DgMailEventInputBuilder setPrinter(String printer) {
        this.printer = printer;
        return this;
    }

    public DgMailEventInputBuilder setPrinterJobname(String printerJobname) {
        this.printerJobname = printerJobname;
        return this;
    }

    public DgMailEventInputBuilder setPromptSurveyName(String promptSurveyName) {
        this.promptSurveyName = promptSurveyName;
        return this;
    }

    public DgMailEventInputBuilder setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
        return this;
    }

    public DgMailEventInputBuilder setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
        return this;
    }

    public DgMailEventInputBuilder setSourceFileEncryption(String sourceFileEncryption) {
        this.sourceFileEncryption = sourceFileEncryption;
        return this;
    }

    public DgMailEventInputBuilder setSourceFileExtension(String sourceFileExtension) {
        this.sourceFileExtension = sourceFileExtension;
        return this;
    }

    public DgMailEventInputBuilder setUserResponse(String userResponse) {
        this.userResponse = userResponse;
        return this;
    }

    public DgMailEventInputBuilder setWasDestFileCaptured(String wasDestFileCaptured) {
        this.wasDestFileCaptured = wasDestFileCaptured;
        return this;
    }

    public DgMailEventInputBuilder setWasDestinationClassified(String wasDestinationClassified) {
        this.wasDestinationClassified = wasDestinationClassified;
        return this;
    }

    public DgMailEventInputBuilder setWasDestinationRemovable(String wasDestinationRemovable) {
        this.wasDestinationRemovable = wasDestinationRemovable;
        return this;
    }

    public DgMailEventInputBuilder setWasDetailRuleViolation(String wasDetailRuleViolation) {
        this.wasDetailRuleViolation = wasDetailRuleViolation;
        return this;
    }

    public DgMailEventInputBuilder setWasDetailScreenCaptured(String wasDetailScreenCaptured) {
        this.wasDetailScreenCaptured = wasDetailScreenCaptured;
        return this;
    }

    public DgMailEventInputBuilder setWasSourceClassified(String wasSourceClassified) {
        this.wasSourceClassified = wasSourceClassified;
        return this;
    }

    public DgMailEventInputBuilder setWasSourceFileCaptured(String wasSourceFileCaptured) {
        this.wasSourceFileCaptured = wasSourceFileCaptured;
        return this;
    }

    public DgMailEventInputBuilder setWasSourceRemovable(String wasSourceRemovable) {
        this.wasSourceRemovable = wasSourceRemovable;
        return this;
    }

    public DgMailEventInputBuilder setSourceDriveType(String sourceDriveType) {
        this.sourceDriveType = sourceDriveType;
        return this;
    }

    public DgMailEventInputBuilder setSourceDeviceID(String sourceDeviceID) {
        this.sourceDeviceID = sourceDeviceID;
        return this;
    }

    public DgMailEventInputBuilder setDestinationDriveType(String destinationDriveType) {
        this.destinationDriveType = destinationDriveType;
        return this;
    }

    public DgMailEventInputBuilder setDestinationDeviceID(String destinationDeviceID) {
        this.destinationDeviceID = destinationDeviceID;
        return this;
    }

    public DgMailEventInputBuilder setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public DgMailEventInputBuilder setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public DgMailEventInputBuilder setRegistryDestinationPath(String registryDestinationPath) {
        this.registryDestinationPath = registryDestinationPath;
        return this;
    }

    public DgMailEventInputBuilder setRegistrySourcePath(String registrySourcePath) {
        this.registrySourcePath = registrySourcePath;
        return this;
    }

    public DgMailEventInputBuilder setRegistryValue(String registryValue) {
        this.registryValue = registryValue;
        return this;
    }

    public DgMailEventInputBuilder setRegistryValueType(String registryValueType) {
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
                    final String fieldName = field.getName();
                    if (fieldName.equals("detailFileSize")) {
                        field.set(dgMailEventInput, 0);
                    }
                    else {
                        field.set(dgMailEventInput, "some_" + fieldName);
                    }
                }

            } catch (IllegalAccessException e) {

                return null;
            }
        }
        return dgMailEventInput;
    }
}


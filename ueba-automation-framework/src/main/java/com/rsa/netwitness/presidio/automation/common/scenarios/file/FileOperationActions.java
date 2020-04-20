package com.rsa.netwitness.presidio.automation.common.scenarios.file;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE_CATEGORIES;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.FixedOperationTypeGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;
import presidio.data.generators.common.precentage.OperationResultPercentageGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.OPERATION_RESULT;
import presidio.data.generators.event.file.FileEventsGenerator;
import presidio.data.generators.fileentity.FileEntityGenerator;
import presidio.data.generators.fileentity.SimplePathGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.fileop.FileOperationGeneratorTemplateFactory;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.SingleAdminUserGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileOperationActions {

    public static List<FileEvent> getEventsByOperationName(String opName, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator ) throws GeneratorException {
        switch (opName) {
            case "MoveFileOperation":
                return getFileOperation(FILE_OPERATION_TYPE.FILE_MOVED, eventIdGen, timeGenerator, userGenerator);
            case "MoveFileLocalOperation":
                return getMoveFileLocalOperation(eventIdGen, timeGenerator, userGenerator);
            case "FailedMoveFileOperation":
                return getFailedMoveFileOperation(eventIdGen, timeGenerator, userGenerator);
            case "ProtectedMoveFileOperation":
                return getProtectedMoveFileOperation(eventIdGen, timeGenerator, userGenerator);
            case "LocalSharePermissionsChangedOperation":
                return getSuccessfulLocalSharePermissionsChangedOperation(eventIdGen, timeGenerator, userGenerator);
            case "FailedLocalSharePermissionsChangedOperation":
                return getFailedLocalSharePermissionsChangedOperation(eventIdGen, timeGenerator, userGenerator);
            case "MoveFromSharedFileOperation":
                return getMoveFromSharedFileOperation(eventIdGen, timeGenerator, userGenerator);
            case "MoveToSharedFileOperation":
                return getMoveToSharedFileOperation(eventIdGen, timeGenerator, userGenerator);
            case "DeleteFileOperation":
                return getFileOperation(FILE_OPERATION_TYPE.FILE_DELETED, eventIdGen, timeGenerator, userGenerator);
            case "RenameFileOperation":
                return getFileOperation(FILE_OPERATION_TYPE.FILE_RENAMED, eventIdGen, timeGenerator, userGenerator);
            case "OpenFileOperation":
                return getFileOperation(FILE_OPERATION_TYPE.FILE_OPENED, eventIdGen, timeGenerator, userGenerator);
            case "OpenFolderOperation":
                return getFileOperation(FILE_OPERATION_TYPE.FOLDER_OPENED, eventIdGen, timeGenerator, userGenerator);
            case "OpenFolderManyDistinctOperation":
                return getOpenFolderManyDistinctOperation(eventIdGen, timeGenerator, userGenerator);
            case "FolderAccessRightsChangedOperation":
                return getFileOperation(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED, eventIdGen, timeGenerator, userGenerator);
            case "FileAccessRightsChangedOperation":
                return getFileOperation(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED, eventIdGen, timeGenerator, userGenerator);
            case "FileClassificationChangedOperation":
                return getFileOperation(FILE_OPERATION_TYPE.FILE_CLASSIFICATION_CHANGED, eventIdGen, timeGenerator, userGenerator);
            case "ClassificationChangedOperation":
                return getFileOperation(FILE_OPERATION_TYPE.FOLDER_CLASSIFICATION_CHANGED, eventIdGen, timeGenerator, userGenerator);
            case "FileCentralAccessPolicyChangedOperation":
                return getFileOperation(FILE_OPERATION_TYPE.FILE_CENTRAL_ACCESS_POLICY_CHANGED, eventIdGen, timeGenerator, userGenerator);
            case "NullSrcFilePath":
                return getNullSrcFilePathOperation(eventIdGen, timeGenerator, userGenerator);
            default:
                return null;
        }
    }

    public static List<FileEvent> getFileOperation(FILE_OPERATION_TYPE operationType, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();

        FileEventsGenerator eventGenerator = new FileEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        if (Arrays.asList(FILE_OPERATION_TYPE.FOLDER_OPENED,
                FILE_OPERATION_TYPE.FILE_DELETED,
                FILE_OPERATION_TYPE.FILE_OPENED,
                FILE_OPERATION_TYPE.FILE_RENAMED,
                FILE_OPERATION_TYPE.FILE_MOVED).contains(operationType)){
            eventGenerator.setFileOperationGenerator(createFileActionOperationsGenerator(operationType));
        }
        else {
            eventGenerator.setFileOperationGenerator(createFilePermissionOperationsGenerator(operationType));
        }
        return eventGenerator.generate();
    }

    public static List<FileEvent> getFailedFileOperation(FILE_OPERATION_TYPE operationType, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();

        FileOperationGenerator opGenerator;

        FileEventsGenerator eventGenerator = new FileEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        if (Arrays.asList(FILE_OPERATION_TYPE.FOLDER_OPENED,
                FILE_OPERATION_TYPE.FILE_DELETED,
                FILE_OPERATION_TYPE.FILE_OPENED,
                FILE_OPERATION_TYPE.FILE_RENAMED,
                FILE_OPERATION_TYPE.FILE_MOVED).contains(operationType)){
            opGenerator = createFileActionOperationsGenerator(operationType);
        }
        else {
            opGenerator = createFilePermissionOperationsGenerator(operationType);
        }

        opGenerator.setOperationResultGenerator(new OperationResultPercentageGenerator(new String[]{OPERATION_RESULT.FAILURE.value}, new int[] {100}));
        eventGenerator.setFileOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    /*********************************    File Action Categories:    *********************************/

    public static List<FileEvent> getFailedMoveFileOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator myTimeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        FileEventsGenerator eventGenerator = new FileEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(myTimeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        eventGenerator.setFileOperationGenerator(createFailedMoveFileOperationsGenerator());
        return eventGenerator.generate();
    }

    public static List<FileEvent> getProtectedMoveFileOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator myTimeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        FileEventsGenerator eventGenerator = new FileEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(myTimeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        eventGenerator.setFileOperationGenerator(createProtectedMoveFileOperationsGenerator());
        return eventGenerator.generate();
    }

    public static List<FileEvent> getFailedLocalSharePermissionsChangedOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator myTimeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        FileEventsGenerator eventGenerator = new FileEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(myTimeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        eventGenerator.setFileOperationGenerator(createFailedLocalSharePermissionsChangeOperationsGenerator());
        return eventGenerator.generate();
    }

    public static List<FileEvent> getMoveFileLocalOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator myTimeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        FileEventsGenerator eventGenerator = new FileEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(myTimeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        eventGenerator.setFileOperationGenerator(createMoveFileLocalOperationsGenerator());
        return eventGenerator.generate();
    }

    public static List<FileEvent> getMoveFileLocalOperationUserAdmin(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator myTimeGenerator, SingleAdminUserGenerator userGenerator) throws GeneratorException {
        FileEventsGenerator eventGenerator = new FileEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(myTimeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        eventGenerator.setFileOperationGenerator(createMoveFileLocalOperationsGenerator());
        return eventGenerator.generate();
    }

    public static List<FileEvent> getMoveFromSharedFileOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator myTimeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        FileEventsGenerator eventGenerator = new FileEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(myTimeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        eventGenerator.setFileOperationGenerator(createMoveFileFromSharedOperationsGenerator());
        return eventGenerator.generate();
    }

    public static List<FileEvent> getMoveToSharedFileOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator myTimeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        FileEventsGenerator eventGenerator = new FileEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(myTimeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        eventGenerator.setFileOperationGenerator(createMoveFileToSharedOperationsGenerator());
        return eventGenerator.generate();
    }

    public static List<FileEvent> getNullSrcFilePathOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator myTimeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        FileEventsGenerator eventGenerator = new FileEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(myTimeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        FileOperationGenerator fileOperationGenerator = new FileOperationGenerator();
        FileEntityGenerator srcFileEntityGenerator = new FileEntityGenerator();
        SimplePathGenerator pathGen = new SimplePathGenerator(new String[] {null});
        srcFileEntityGenerator.setFilePathGenerator(pathGen);
        fileOperationGenerator.setSourceFileEntityGenerator(srcFileEntityGenerator);
        eventGenerator.setFileOperationGenerator(fileOperationGenerator);

        return eventGenerator.generate();
    }

    public static List<FileEvent> getOpenFolderManyDistinctOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator myTimeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        FileOperationGeneratorTemplateFactory fileOP = new FileOperationGeneratorTemplateFactory();
        FileEventsGenerator eventGenerator = new FileEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(myTimeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        FileOperationGenerator fileOperationGenerator = (FileOperationGenerator) fileOP.createFolderOpenFileOperationsGenerator();
        FileEntityGenerator fileEntityGenerator = new FileEntityGenerator();

        String[] pathsList = new String[100];
        for (int i = 0; i < 100; i++) pathsList[i] = "/p/user/folder/subfolder" + i;
        SimplePathGenerator filePathGenerator = new SimplePathGenerator(pathsList);

        fileEntityGenerator.setFilePathGenerator(filePathGenerator);
        fileOperationGenerator.setSourceFileEntityGenerator(fileEntityGenerator);
        eventGenerator.setFileOperationGenerator(fileOperationGenerator);
        return eventGenerator.generate();
    }

    private static FileOperationGenerator createMoveFileLocalOperationsGenerator() throws GeneratorException {
        return createMoveFileSharedOperationsGenerator(0, 0);
    }
    private static FileOperationGenerator createMoveFileToSharedOperationsGenerator() throws GeneratorException {
        return createMoveFileSharedOperationsGenerator(0, 100);
    }
    private static FileOperationGenerator createMoveFileFromSharedOperationsGenerator() throws GeneratorException {

        return createMoveFileSharedOperationsGenerator(100, 0);
    }

    private static FileOperationGenerator createMoveFileSharedOperationsGenerator(int sourceShared, int destShared) throws GeneratorException {
        FileOperationGenerator generator = createFileActionOperationsGenerator(FILE_OPERATION_TYPE.FILE_MOVED);

        // Source file - local
        FileEntityGenerator srcFileGenerator = new FileEntityGenerator();
        BooleanPercentageGenerator isSrcDriveSharedGenerator = new BooleanPercentageGenerator(sourceShared);
        srcFileGenerator.setIsDriveSharedGenerator(isSrcDriveSharedGenerator);
        generator.setSourceFileEntityGenerator(srcFileGenerator);

        // Destination file - shared
        FileEntityGenerator dstFileGenerator = new FileEntityGenerator();
        BooleanPercentageGenerator isDstDriveSharedGenerator = new BooleanPercentageGenerator(destShared);
        dstFileGenerator.setIsDriveSharedGenerator(isDstDriveSharedGenerator);
        generator.setDestFileEntityGenerator(dstFileGenerator);

        return generator;
    }

    private static FileOperationGenerator createFailedMoveFileOperationsGenerator() throws GeneratorException {
        FileOperationGenerator generator = createFileActionOperationsGenerator(FILE_OPERATION_TYPE.FILE_MOVED);

        // Result - failure
        generator.setOperationResultGenerator(new OperationResultPercentageGenerator(new String[] {OPERATION_RESULT.FAILURE.value}, new int[] {100}));
        return generator;
    }

    private static FileOperationGenerator createProtectedMoveFileOperationsGenerator() throws GeneratorException {
        FileOperationGenerator generator = createFileActionOperationsGenerator(FILE_OPERATION_TYPE.FILE_MOVED);

        // Result - failure
        generator.setOperationResultGenerator(new OperationResultPercentageGenerator(new String[] {"Protected"}, new int[] {100}));
        return generator;
    }

    private static FileOperationGenerator createFileActionOperationsGenerator(FILE_OPERATION_TYPE operationType) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();

        List<String> fileActionCategory = new ArrayList<>();
        fileActionCategory.add(FILE_OPERATION_TYPE_CATEGORIES.FILE_ACTION.value);

        FixedOperationTypeGenerator operationTypeGenerator = new FixedOperationTypeGenerator(
                new OperationType(operationType.value, fileActionCategory));
        generator.setOperationTypeGenerator(operationTypeGenerator);

        FileEntityGenerator dstFileEntityGenerator = new FileEntityGenerator();
        dstFileEntityGenerator.setFilePathGenerator(new SimplePathGenerator(new String[] {"/temp/bin/1/", "/temp/bin/2/"}));
        generator.setDestFileEntityGenerator(dstFileEntityGenerator);

        FileEntityGenerator srcFileEntityGenerator = new FileEntityGenerator();
        srcFileEntityGenerator.setFilePathGenerator(new SimplePathGenerator(new String[] {"/sys/log/3/", "/sys/log/4/"}));
        generator.setSourceFileEntityGenerator(srcFileEntityGenerator);

        return generator;
    }

    /*********************************    File Permission Change Categories:    *********************************/

    public static List<FileEvent> getSuccessfulLocalSharePermissionsChangedOperation(EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator myTimeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        FileEventsGenerator eventGenerator = new FileEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(myTimeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        eventGenerator.setFileOperationGenerator(createSuccessfulLocalSharePermissionsChangeOperationsGenerator());
        return eventGenerator.generate();
    }

    private static FileOperationGenerator createFilePermissionOperationsGenerator(FILE_OPERATION_TYPE operationType) throws GeneratorException {
        FileOperationGenerator generator = new FileOperationGenerator();

        List<String> fileActionCategory = new ArrayList<>();
        fileActionCategory.add(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value);

        FixedOperationTypeGenerator operationTypeGenerator = new FixedOperationTypeGenerator(
                new OperationType(operationType.value, fileActionCategory));
        generator.setOperationTypeGenerator(operationTypeGenerator);
        return generator;
    }

    private static FileOperationGenerator createSuccessfulLocalSharePermissionsChangeOperationsGenerator() throws GeneratorException {
        return createLocalSharePermissionsChangeOperationsGenerator(100);
    }

    private static FileOperationGenerator createFailedLocalSharePermissionsChangeOperationsGenerator() throws GeneratorException {
        return createLocalSharePermissionsChangeOperationsGenerator(0);
    }

    private static FileOperationGenerator createLocalSharePermissionsChangeOperationsGenerator(int successPct) throws GeneratorException {
        // if invalid parameter provided - generate all successful events
        if (successPct > 100 || successPct < 0) successPct = 100;

        FileOperationGenerator generator = new FileOperationGenerator();

        List<String> fileOperationCategory = new ArrayList<>();
        fileOperationCategory.add(FILE_OPERATION_TYPE_CATEGORIES.FILE_PERMISSION_CHANGE.value);

        FixedOperationTypeGenerator operationTypeGenerator = new FixedOperationTypeGenerator(
                new OperationType(FILE_OPERATION_TYPE.FILE_OWNERSHIP_CHANGED.value, fileOperationCategory));
        generator.setOperationTypeGenerator(operationTypeGenerator);

        OperationResultPercentageGenerator fileOperationResult = new OperationResultPercentageGenerator(
                new String[] {OPERATION_RESULT.SUCCESS.value, OPERATION_RESULT.FAILURE.value}, new int[] {successPct,100 - successPct}
        );

        generator.setOperationResultGenerator(fileOperationResult);

        return generator;
    }

    public static List<FileEvent> alertsSanityTestEvents(int historicalStartDay, int anomalyDay, int sequenceNo) throws GeneratorException {
        List<FileEvent> events = new ArrayList<>();

        final String testUser1 = "file_user1_" + sequenceNo;
        final String testUser2 = "file_user2_" + sequenceNo;
        final String testUser3 = "file_user3_" + sequenceNo;
        final String testCase = "sanity_file";

        /** 5 days of normal activity:
         * User 1, 2, 3:
         * successful file action and permission change operations once per hour
         *
         * 3 days of anomalies:
         * User 1: abnormal time, other operation
         * User 2: other operation type category
         * User 3: high number of operations
         * */

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testCase);

        ITimeGenerator normalTimeGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(8,0), LocalTime.of(16,59), 60, historicalStartDay, anomalyDay);
        ITimeGenerator abnormalTimeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(8,0), LocalTime.of(9,0), 1, anomalyDay, anomalyDay - 1);
        ITimeGenerator abnormalTimeGenerator2 = new MinutesIncrementTimeGenerator(LocalTime.of(8,0), LocalTime.of(9,0), 1, anomalyDay, anomalyDay - 1);
        ITimeGenerator abnormalTimeGenerator3 = new MinutesIncrementTimeGenerator(LocalTime.of(8,0), LocalTime.of(9,0), 1, anomalyDay, anomalyDay - 1);

        SingleUserGenerator userGenerator1 = new SingleUserGenerator(testUser1);
        SingleUserGenerator userGenerator2 = new SingleUserGenerator(testUser2);
        IUserGenerator userGenerator3 = new SingleAdminUserGenerator(testUser3);

        // Normal:
        events.addAll(FileOperationActions.getMoveFileLocalOperation(eventIdGen, normalTimeGenerator, userGenerator1));
        events.addAll(FileOperationActions.getEventsByOperationName("DeleteFileOperation", eventIdGen, normalTimeGenerator, userGenerator1)); //some delete operations
        events.addAll(FileOperationActions.getMoveFromSharedFileOperation(eventIdGen, normalTimeGenerator, userGenerator2));
        events.addAll(FileOperationActions.getOpenFolderManyDistinctOperation(eventIdGen, normalTimeGenerator, userGenerator3));

        // Anomalies:
        events.addAll(FileOperationActions.getEventsByOperationName("DeleteFileOperation", eventIdGen, abnormalTimeGenerator1, userGenerator1)); // many delete operations
        events.addAll(FileHighNumberOfOperations.getPermissionChangeActionsOneHourAnomaly(testUser2, normalTimeGenerator, abnormalTimeGenerator2));
        events.addAll(FileHighNumberOfOperations.getHighNumFailedFileOperations(testUser3, normalTimeGenerator, abnormalTimeGenerator3));

        return events;
    }

}

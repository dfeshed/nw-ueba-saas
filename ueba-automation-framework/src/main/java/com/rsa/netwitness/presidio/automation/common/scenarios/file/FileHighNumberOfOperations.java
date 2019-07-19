package com.rsa.netwitness.presidio.automation.common.scenarios.file;

import presidio.data.domain.User;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.user.SingleAdminUserGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FileHighNumberOfOperations {

    public static List<FileEvent> getHighNumSuccessfulPermissionChange(String testUser, int anomalyDay) throws GeneratorException {
        return getHighNumSuccessfulPermissionChange(testUser, 30, anomalyDay, anomalyDay - 1);
    }

    public static List<FileEvent> getHighNumSuccessfulPermissionChange(String testUser, int historicalStartDay, int anomalyStartDay, int anomalyEndDay) throws GeneratorException {
        /**
         * Normal behavior: user perform only file actions
         * Anomaly: user successfully performs file_permission_change action getSuccessfulLocalSharePermissionsChangedOperation
         *
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, historicalStartDay, anomalyStartDay);
        events.addAll(FileOperationActions.getEventsByOperationName("MoveFileOperation", eventIdGen, timeGenerator1, userGenerator));

        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 60, historicalStartDay, anomalyStartDay);
        events.addAll(FileOperationActions.getEventsByOperationName("DeleteFileOperation", eventIdGen, timeGenerator2, userGenerator));

        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 45, historicalStartDay, anomalyStartDay);
        events.addAll(FileOperationActions.getEventsByOperationName("RenameFileOperation", eventIdGen, timeGenerator3, userGenerator));

        ITimeGenerator timeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(16, 30), 60, historicalStartDay, anomalyStartDay);
        events.addAll(FileOperationActions.getEventsByOperationName("LocalSharePermissionsChangedOperation", eventIdGen, timeGenerator4, userGenerator));

        //Anomaly:
        ITimeGenerator timeGenerator5 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 30), 1, anomalyStartDay, anomalyEndDay);
        events.addAll(FileOperationActions.getEventsByOperationName("LocalSharePermissionsChangedOperation", eventIdGen, timeGenerator5, userGenerator));

        // not Anomaly:
        ITimeGenerator timeGenerator6 =
                new MinutesIncrementTimeGenerator(LocalTime.of(13, 00), LocalTime.of(16, 00), 30, anomalyStartDay, anomalyEndDay);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFolderOperation", eventIdGen, timeGenerator6, userGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumSuccessfulFileAction(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior: user perform low number of file actions and permission change operations
         * Anomaly: user successfully performs large number of file actions
         *
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 10, anomalyDay + 28, anomalyDay + 23);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFileOperation", eventIdGen, timeGenerator1, userGenerator));

        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 10, anomalyDay + 23, anomalyDay + 18);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFolderOperation", eventIdGen, timeGenerator2, userGenerator));

        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 45, anomalyDay + 18, anomalyDay + 13);
        events.addAll(FileOperationActions.getEventsByOperationName("RenameFileOperation", eventIdGen, timeGenerator3, userGenerator));

        ITimeGenerator timeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(12, 30), LocalTime.of(15, 30), 90, anomalyDay + 8, anomalyDay + 3);
        events.addAll(FileOperationActions.getEventsByOperationName("LocalSharePermissionsChangedOperation", eventIdGen, timeGenerator4, userGenerator));

        //Anomaly - many file action operations:
        ITimeGenerator timeGenerator5 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(10, 30), 6, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFileOperation", eventIdGen, timeGenerator5, userGenerator));

        ITimeGenerator timeGenerator6 =
                new MinutesIncrementTimeGenerator(LocalTime.of(15, 30), LocalTime.of(17, 30), 2, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getEventsByOperationName("MoveFileOperation", eventIdGen, timeGenerator6, userGenerator));

        // not Anomaly - some permission change and some file operations:
        ITimeGenerator timeGenerator7 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 30), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getEventsByOperationName("LocalSharePermissionsChangedOperation", eventIdGen, timeGenerator7, userGenerator));

        ITimeGenerator timeGenerator8 =
                new MinutesIncrementTimeGenerator(LocalTime.of(13, 00), LocalTime.of(16, 00), 30, anomalyDay, anomalyDay -1);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFolderOperation", eventIdGen, timeGenerator8, userGenerator));

        return events;
    }

    public static List<FileEvent> getPermissionChangeActionsOneHourAnomaly(String testUser, ITimeGenerator normalTimeGenerator, ITimeGenerator abnormalTimeGenerator) throws GeneratorException {
        /**
         * Anomaly: all events at day back 2. File Actions, successful and failed. Permissions - successful
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        normalTimeGenerator.reset();
        abnormalTimeGenerator.reset();

        //Anomaly - many file action operations:
        events.addAll(FileOperationActions.getEventsByOperationName("FailedLocalSharePermissionsChangedOperation", eventIdGen, normalTimeGenerator, userGenerator));
        events.addAll(FileOperationActions.getEventsByOperationName("LocalSharePermissionsChangedOperation", eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumFailedPermissionChange(String testUser, int anomalyDay) throws GeneratorException {
        return getHighNumFailedPermissionChange(testUser, 30, anomalyDay, anomalyDay - 1);
    }

    public static List<FileEvent> getHighNumFailedPermissionChange(String testUser, int historicalStartDay, int abnormalStartDay, int abnormalEndDay) throws GeneratorException {
        /**
         * Normal behavior: user successfully perform file actions
         * Anomaly: user fails to perform file actions
         *
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, historicalStartDay, abnormalStartDay);
        events.addAll(FileOperationActions.getEventsByOperationName("LocalSharePermissionsChangedOperation", eventIdGen, timeGenerator1, userGenerator));

        // Normal - some failures
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 180, historicalStartDay, abnormalStartDay);
        events.addAll(FileOperationActions.getEventsByOperationName("FailedLocalSharePermissionsChangedOperation", eventIdGen, timeGenerator2, userGenerator));

        // Abnormal - many failures
        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(15, 30), LocalTime.of(16, 30), 1, abnormalStartDay, abnormalEndDay);
        events.addAll(FileOperationActions.getEventsByOperationName("FailedLocalSharePermissionsChangedOperation", eventIdGen, timeGenerator3, userGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumFailedFileOperations(String testUser, int anomalyDay) throws GeneratorException {
        return getHighNumFailedFileOperations(testUser, 30, anomalyDay, anomalyDay - 1);
    }

    public static List<FileEvent> getHighNumFailedFileOperations(String testUser, int historicalStartDay, int abnormalStartDay, int abnormalEndDay) throws GeneratorException {
        /**
         * Normal behavior: user successfully perform file actions
         * Anomaly: user fails to perform file actions
         *
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, historicalStartDay, abnormalStartDay);
        events.addAll(FileOperationActions.getEventsByOperationName("MoveFileOperation", eventIdGen, timeGenerator1, userGenerator));

        // Normal - some failures
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 180, historicalStartDay, abnormalStartDay);
        events.addAll(FileOperationActions.getEventsByOperationName("FailedMoveFileOperation", eventIdGen, timeGenerator2, userGenerator));

        // Abnormal - many failures
        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 30), LocalTime.of(14, 30), 1, abnormalStartDay, abnormalEndDay);
        events.addAll(FileOperationActions.getEventsByOperationName("FailedMoveFileOperation", eventIdGen, timeGenerator3, userGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumProtectedFileOperations(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior: user successfully perform file actions
         * Anomaly: user fails to perform file actions
         *
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, 30, anomalyDay);
        events.addAll(FileOperationActions.getEventsByOperationName("MoveFileOperation", eventIdGen, timeGenerator1, userGenerator));

        // Normal - some failures
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 180, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getEventsByOperationName("ProtectedMoveFileOperation", eventIdGen, timeGenerator2, userGenerator));

        // Abnormal - many failures
        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 00), LocalTime.of(16, 00), 1, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getEventsByOperationName("ProtectedMoveFileOperation", eventIdGen, timeGenerator3, userGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumFailedFileOperations(String testUser, ITimeGenerator normalTimeGenerator, ITimeGenerator abnormalTimeGenerator) throws GeneratorException {
        /**
         * Normal behavior: user successfully perform file actions
         * Anomaly: user fails to perform file actions
         *
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        normalTimeGenerator.reset();
        abnormalTimeGenerator.reset();

        events.addAll(FileOperationActions.getEventsByOperationName("MoveFileOperation", eventIdGen, normalTimeGenerator, userGenerator));

        // Abnormal - many failures
        events.addAll(FileOperationActions.getEventsByOperationName("FailedMoveFileOperation", eventIdGen, abnormalTimeGenerator, userGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumMoveFromSharedDriveOperations(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior: user successfully perform some move file actions
         * Anomaly: user performs large number of Move file actions from Shared Drive
         *
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal - some move file from local to local
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, anomalyDay + 28, anomalyDay);

        events.addAll(FileOperationActions.getEventsByOperationName("MoveFileLocalOperation", eventIdGen, timeGenerator1, userGenerator));

        // Normal - some file move from shared drive to local
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 480, anomalyDay + 30, anomalyDay);
        events.addAll(FileOperationActions.getEventsByOperationName("MoveFromSharedFileOperation", eventIdGen, timeGenerator2, userGenerator));

        // Abnormal - many file move FROM SHARED DRIVE
        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getEventsByOperationName("MoveFromSharedFileOperation", eventIdGen, timeGenerator3, userGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumMoveToSharedDriveOperations(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior: user successfully perform some move file actions
         * Anomaly: user performs large number of Move file actions to Shared Drive
         *
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal - some move file from local to local
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, anomalyDay + 2, anomalyDay);

        events.addAll(FileOperationActions.getEventsByOperationName("MoveFileLocalOperation", eventIdGen, timeGenerator1, userGenerator));

        // Normal - some file move to shared drive
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 120, anomalyDay + 2, anomalyDay);
        events.addAll(FileOperationActions.getEventsByOperationName("MoveToSharedFileOperation", eventIdGen, timeGenerator2, userGenerator));

        // Abnormal - many file move TO SHARED DRIVE
        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getEventsByOperationName("MoveToSharedFileOperation", eventIdGen, timeGenerator3, userGenerator));

        return events;
    }
    public static List<FileEvent> getHighNumFileMoveOperations(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior: user successfully perform some move file actions
         * Anomaly: user performs large number of Move file actions from local drive to local drive
         *
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal - some move operations
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, anomalyDay + 28, anomalyDay);
        events.addAll(FileOperationActions.getEventsByOperationName("MoveFileOperation", eventIdGen, timeGenerator1, userGenerator));

        // Abnormal - many move operations
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getEventsByOperationName("MoveFileOperation", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumLastDayOperations(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior: user successfully perform some move file actions
         * Anomaly: user performs large number of Move file actions from local drive to local drive
         *
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Abnormal - many move operations
        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(6, 30), LocalTime.of(16, 30), 1, anomalyDay - 1, anomalyDay - 2);
        events.addAll(FileOperationActions.getEventsByOperationName("MoveFileOperation", eventIdGen, timeGenerator, userGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumFileOpenOperations(String testUser, int anomalyDay) throws GeneratorException {
        return getHighNumFileOpenOperations(testUser, 30, anomalyDay, anomalyDay - 1);
    }

    public static List<FileEvent> getHighNumFileOpenOperations(String testUser, int firstHistoricalDay, int abnormalStartDay, int abnormalEndDay) throws GeneratorException {
        /**
         * Normal behavior: user successfully perform some file open actions
         * Anomaly: user performs large number of file open actions
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal - some open file operations
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, firstHistoricalDay, abnormalStartDay);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFileOperation", eventIdGen, timeGenerator1, userGenerator));

        // Abnormal - many open file operations
        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(10, 30), 1, abnormalStartDay, abnormalEndDay);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFileOperation", eventIdGen, timeGenerator3, userGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumFolderOpenOperations(String testUser, int anomalyDay) throws GeneratorException {
        return getHighNumFolderOpenOperations(testUser, 30, anomalyDay, anomalyDay - 1);
    }

    public static List<FileEvent> getHighNumFolderOpenOperations(String testUser, int historicalStartDay, int abnormalStartDay, int abnormalEndDay) throws GeneratorException {
        /**
         * Normal behavior: user successfully perform some open folder actions
         * Anomaly: user performs large number of open folder actions.
         * Distinct folders will get high score. The same folders opened many times will get score 0.
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal - some open folder operations
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, historicalStartDay, abnormalStartDay);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFolderOperation", eventIdGen, timeGenerator1, userGenerator));

        // Abnormal - many open DISTINCT folder operations
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(12, 30), 1, abnormalStartDay, abnormalEndDay);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFolderManyDistinctOperation", eventIdGen, timeGenerator2, userGenerator));

        // Not anomaly - many open folder (not distinct) operations. Score 0
        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(12, 30), LocalTime.of(16, 30), 1, abnormalStartDay, abnormalEndDay);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFolderOperation", eventIdGen, timeGenerator3, userGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumDeletionOperations(String testUser, int anomalyDay) throws GeneratorException {
        return getHighNumDeletionOperations(testUser, 30, anomalyDay, anomalyDay - 1);
    }
    public static List<FileEvent> getHighNumDeletionOperations(String testUser, int historicalStartDay, int abnormalStartDay, int abnormalEndDay) throws GeneratorException {
        /**
         * Normal behavior: user successfully perform some file deletion actions
         * Anomaly: user performs large number of file deletion actions
         *
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal - some delete file operations
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, historicalStartDay, abnormalStartDay);
        events.addAll(FileOperationActions.getEventsByOperationName("DeleteFileOperation", eventIdGen, timeGenerator1, userGenerator));

        // Abnormal - many delete file operations
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(13, 30), LocalTime.of(16, 30), 1, abnormalStartDay, abnormalEndDay);
        events.addAll(FileOperationActions.getEventsByOperationName("DeleteFileOperation", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumRenameOperations(String testUser, int anomalyDay) throws GeneratorException {
        return getHighNumRenameOperations(testUser, 30, anomalyDay, anomalyDay - 1);
    }

    public static List<FileEvent> getHighNumRenameOperations(String testUser, int firstHistoricalDay, int abnormalStartDay, int abnormalEndDay) throws GeneratorException {
        /**
         * Normal behavior: user successfully perform some file rename actions
         * Anomaly: user performs large number of file rename actions from local drive to local drive
         *
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal - some rename file operations
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, firstHistoricalDay, abnormalStartDay);
        events.addAll(FileOperationActions.getEventsByOperationName("RenameFileOperation", eventIdGen, timeGenerator1, userGenerator));

        // Abnormal - many rename file operations
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(11, 30), 1, abnormalStartDay, abnormalEndDay);
        events.addAll(FileOperationActions.getEventsByOperationName("RenameFileOperation", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumMoveOperationsUserAdmin(String testUser, int anomalyDay) throws GeneratorException {
        return getHighNumMoveOperationsUserAdmin(testUser, 30, anomalyDay, anomalyDay - 1);
    }

    public static List<FileEvent> getHighNumMoveOperationsUserAdmin(String testUser, int firstHistoricalDay, int abnormalStartDay, int abnormalEndDay) throws GeneratorException {
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleAdminUserGenerator adminUserGenerator = new SingleAdminUserGenerator(testUser);

        // Normal - some rename file operations
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, firstHistoricalDay, abnormalStartDay);
        events.addAll(FileOperationActions.getMoveFileLocalOperationUserAdmin(eventIdGen, timeGenerator1, adminUserGenerator));

        // Abnormal - many rename file operations
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(11, 30), 1, abnormalStartDay, abnormalEndDay);
        events.addAll(FileOperationActions.getMoveFileLocalOperationUserAdmin(eventIdGen, timeGenerator2, adminUserGenerator));

        return events;
    }

    public static List<FileEvent> getHighNumOfFrequentFolderOpenOperations(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Purpose: test pagination on huge number of events per indicator (6000)
         * Normal behavior: user successfully perform some file open actions
         * Anomaly: user performs large number of file open actions
         */
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal - some open file operations
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, anomalyDay + 28, anomalyDay);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFileOperation", eventIdGen, timeGenerator1, userGenerator));

        // Abnormal - many open file operations
        ITimeGenerator timeGenerator2 =
                new TimeGenerator(LocalTime.of(8, 30), LocalTime.of(10, 30), 300, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFileOperation", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<FileEvent> getFileUserAdmin(String testUser, int daysBackFromAdmin, int daysBackToAdmin, int daysBackFromUser, int daysBackToUser) throws GeneratorException {
        List<FileEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);
        SingleAdminUserGenerator adminUserGenerator = new SingleAdminUserGenerator(testUser);

        User user = new User(testUser);
        user.setUserId(testUser);
        user.setFirstName("file");
        user.setLastName("admin");
        user.setAdministrator(true);

        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(17, 0), 60, daysBackFromAdmin, daysBackToAdmin);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFileOperation", eventIdGen, timeGenerator1, adminUserGenerator));

        events.get(events.size() - 1).setUser(user);

        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(20, 0), LocalTime.of(22, 0), 60, daysBackFromUser, daysBackToUser);
        events.addAll(FileOperationActions.getEventsByOperationName("OpenFileOperation", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

}
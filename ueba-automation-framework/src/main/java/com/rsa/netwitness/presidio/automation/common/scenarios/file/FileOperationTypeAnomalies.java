package com.rsa.netwitness.presidio.automation.common.scenarios.file;

import org.testng.collections.Lists;
import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.file.FILE_OPERATION_TYPE;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.file.FileEventsGenerator;
import presidio.data.generators.fileop.CyclicOperationTypeGenerator;
import presidio.data.generators.fileop.FileOperationGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FileOperationTypeAnomalies {

    public static List<FileEvent> createLocalSharePermissionsChangeAnomaly_NoFilePermissionChangeModelYet(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior: user using only in regular file actions
         * Anomaly: user using in file_permission_change action getSuccessfulLocalSharePermissionsChangedOperation (no "model_operationType.userIdFilePermissionChange.file". The model was not built before the anomaly occurred
         * this anomaly created according to the higher model of the two (PermissionChange and FileAction)
         */
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator myTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 28, anomalyDay + 23);
        List<FileEvent> events = FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_MOVED, eventIdGen, myTimeGenerator, userGenerator);

        ITimeGenerator myTimeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 45, anomalyDay + 23, anomalyDay + 18);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_DELETED, eventIdGen, myTimeGenerator2, userGenerator));

        ITimeGenerator myTimeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 45, anomalyDay + 18, anomalyDay + 13);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_RENAMED, eventIdGen, myTimeGenerator3, userGenerator));

        ITimeGenerator myTimeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(7, 30), LocalTime.of(17, 30), 45, anomalyDay + 13, anomalyDay + 8);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_OPENED, eventIdGen, myTimeGenerator4, userGenerator));

        ITimeGenerator myTimeGenerator5 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(16, 45), 45, anomalyDay + 8, anomalyDay + 3);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_OPENED, eventIdGen, myTimeGenerator5, userGenerator));

        //Anomaly:
        ITimeGenerator myTimeGenerator6 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 30), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getSuccessfulLocalSharePermissionsChangedOperation(eventIdGen, myTimeGenerator6, userGenerator));
        // not Anomaly:
        ITimeGenerator myTimeGenerator7 =
                new MinutesIncrementTimeGenerator(LocalTime.of(13, 00), LocalTime.of(16, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_OPENED, eventIdGen, myTimeGenerator7, userGenerator));

        return events;
    }

    public static List<FileEvent> createFileActionAnomaly(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior: user using only in regular file actions
         * Anomaly: user using in different file action
         * this anomaly created according to the higher model of the two (PermissionChange and FileAction)
         */

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator myTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 28, anomalyDay + 21);
        List<FileEvent> events = FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_MOVED, eventIdGen, myTimeGenerator, userGenerator);

        ITimeGenerator myTimeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 45, anomalyDay + 21, anomalyDay + 15);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_DELETED, eventIdGen, myTimeGenerator2, userGenerator));

        ITimeGenerator myTimeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 45, anomalyDay + 15, anomalyDay + 10);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_RENAMED, eventIdGen, myTimeGenerator3, userGenerator));

        ITimeGenerator myTimeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(7, 30), LocalTime.of(17, 30), 45, anomalyDay + 10, anomalyDay + 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_OPENED, eventIdGen, myTimeGenerator4, userGenerator));

        // Anomaly:
        ITimeGenerator myTimeGenerator5 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_OPENED, eventIdGen, myTimeGenerator5, userGenerator));

        // Not Anomaly:
        ITimeGenerator myTimeGenerator6 =
                new MinutesIncrementTimeGenerator(LocalTime.of(13, 00), LocalTime.of(15, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_OPENED, eventIdGen, myTimeGenerator6, userGenerator));

        return events;
    }

    public static List<FileEvent> createFileActionStepOneAnomaly(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior: user using only in regular file actions
         * Anomaly: user using in different file action
         */
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // normal - FILE_MOVED, db 40-3
        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(13, 30), LocalTime.of(16, 30), 45, anomalyDay + 38, anomalyDay + 2);
        List<FileEvent> events = FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_MOVED, eventIdGen, timeGenerator, userGenerator);

        // Anomaly - FILE_OPENED on days 5-3:
        timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(11, 00), 3, anomalyDay + 2, anomalyDay + 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_OPENED, eventIdGen, timeGenerator, userGenerator));

        return events;
    }

    public static List<FileEvent> createFileDeleteAnomaly(String testUser, ITimeGenerator timeGenerator) throws GeneratorException {
        /**
         * Normal behavior: user using only in regular file actions
         * Anomaly: user perform permission change
         */
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Anomaly - FILE_DELETED
        List<FileEvent> events = FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_DELETED, eventIdGen, timeGenerator, userGenerator);

        return events;
    }

    public static List<FileEvent> createCollectorFileActionAnomaly(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior: user using only in regular file actions
         * Anomaly: user using in different file action
         * this anomaly created according to the higher model of the two (PermissionChange and FileAction)
         */

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator myTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 28, anomalyDay + 21 );
        List<FileEvent> events = FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_MOVED, eventIdGen, myTimeGenerator, userGenerator);

        ITimeGenerator myTimeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 45, anomalyDay + 21, anomalyDay + 15);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_DELETED, eventIdGen, myTimeGenerator2, userGenerator));

        ITimeGenerator myTimeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 45, anomalyDay + 15, anomalyDay + 10);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_RENAMED, eventIdGen, myTimeGenerator3, userGenerator));

        ITimeGenerator myTimeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(7, 30), LocalTime.of(17, 30), 45, anomalyDay + 10, anomalyDay + 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_OPENED, eventIdGen, myTimeGenerator4, userGenerator));

        // Anomaly:
        ITimeGenerator myTimeGenerator5 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_OPENED, eventIdGen, myTimeGenerator5, userGenerator));

        // Not Anomaly:
        ITimeGenerator myTimeGenerator6 =
                new MinutesIncrementTimeGenerator(LocalTime.of(13, 00), LocalTime.of(15, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_OPENED, eventIdGen, myTimeGenerator6, userGenerator));

        return events;
    }

    public static List<FileEvent> createFilePermissionChangeAnomalyAndActionAnomaly(String testUser, int anomalyDay) throws GeneratorException {

        /**
         * Normal behavior: user using in "PermissionChange" and "FileAction"
         * Anomaly: user using in different file action and different file_permission_change actions
         * this anomaly created according to the higher model of the two (PermissionChange and FileAction)
         */

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator myTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 28, anomalyDay + 23);
        List<FileEvent> events = FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED, eventIdGen, myTimeGenerator, userGenerator);

        ITimeGenerator myTimeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 45, anomalyDay + 23, anomalyDay + 18);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED, eventIdGen, myTimeGenerator2, userGenerator));

        ITimeGenerator myTimeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 45, anomalyDay + 18, anomalyDay +13);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_RENAMED, eventIdGen, myTimeGenerator3, userGenerator));

        ITimeGenerator myTimeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(7, 30), LocalTime.of(17, 30), 45, anomalyDay + 13, anomalyDay + 8);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_OPENED, eventIdGen, myTimeGenerator4, userGenerator));

        ITimeGenerator myTimeGenerator5 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(16, 45), 45, anomalyDay + 8, anomalyDay + 3);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_CLASSIFICATION_CHANGED, eventIdGen, myTimeGenerator5, userGenerator));

        // Not anomaly :
        ITimeGenerator myTimeGenerator6 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(10, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED, eventIdGen, myTimeGenerator6, userGenerator));

        ITimeGenerator myTimeGenerator7 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(14, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED, eventIdGen, myTimeGenerator7, userGenerator));


        // Anomalies:
        ITimeGenerator myTimeGenerator8 =
                new MinutesIncrementTimeGenerator(LocalTime.of(15, 00), LocalTime.of(16, 30), 07, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_CLASSIFICATION_CHANGED, eventIdGen, myTimeGenerator8, userGenerator));

        ITimeGenerator myTimeGenerator9 =
                new MinutesIncrementTimeGenerator(LocalTime.of(16, 30), LocalTime.of(17, 30), 07, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_MOVED, eventIdGen, myTimeGenerator9, userGenerator));

        ITimeGenerator myTimeGenerator10 =
                new MinutesIncrementTimeGenerator(LocalTime.of(20, 30), LocalTime.of(23, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_CENTRAL_ACCESS_POLICY_CHANGED, eventIdGen, myTimeGenerator10, userGenerator));

        ITimeGenerator myTimeGenerator11 =
                new MinutesIncrementTimeGenerator(LocalTime.of(20, 30), LocalTime.of(23, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFailedFileOperation(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED, eventIdGen, myTimeGenerator11, userGenerator));

        return events;
    }

    public static List<FileEvent> createNoFilePermissionsChangeAnomaly_LocalSharePermissionChangeModelYet(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior: user perform only in PermissionChange operations
         * Anomaly: user perform FileAction (no "model_operationType.userIdFileAction.file")
         * this anomaly created according to the higher model of the two (PermissionChange and FileAction)
         */
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator myTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(9, 00), 60, anomalyDay + 28, anomalyDay + 3);
        List<FileEvent> events = FileOperationActions.getSuccessfulLocalSharePermissionsChangedOperation(eventIdGen, myTimeGenerator, userGenerator);

        ITimeGenerator myTimeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(11, 00), 60, anomalyDay + 23, anomalyDay + 3);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_CLASSIFICATION_CHANGED, eventIdGen, myTimeGenerator2, userGenerator));

        ITimeGenerator myTimeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(10, 00), 60, anomalyDay + 20, anomalyDay + 13);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED, eventIdGen, myTimeGenerator3, userGenerator));

        ITimeGenerator myTimeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(7, 30), LocalTime.of(9, 00), 20, anomalyDay + 13, anomalyDay + 10);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_CLASSIFICATION_CHANGED, eventIdGen, myTimeGenerator4, userGenerator));

        ITimeGenerator myTimeGenerator5 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(12, 45), 60, anomalyDay + 10, anomalyDay + 9);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_CENTRAL_ACCESS_POLICY_CHANGED, eventIdGen, myTimeGenerator5, userGenerator));

        ITimeGenerator myTimeGenerator6 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(9, 45), 60, anomalyDay + 9, anomalyDay + 8);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED, eventIdGen, myTimeGenerator6, userGenerator));

        //Anomaly:
        ITimeGenerator myTimeGenerator7 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 30), 70, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_OPENED, eventIdGen, myTimeGenerator7, userGenerator));

        ITimeGenerator myTimeGenerator8 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(8, 30), 10, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_MOVED, eventIdGen, myTimeGenerator8, userGenerator));

        // low Anomaly:
        ITimeGenerator myTimeGenerator9 =
                new MinutesIncrementTimeGenerator(LocalTime.of(13, 00), LocalTime.of(16, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED, eventIdGen, myTimeGenerator9, userGenerator));

        return events;
    }

    public static List<FileEvent> getAbnormalFilePermissionChange(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior: user perform many file action and one or few PermissionChange operations
         * Anomaly: user perform high number of permission change operation
         */
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        List<FileEvent> events = Lists.newArrayList();
                ITimeGenerator myTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(9, 00), 60, anomalyDay + 28, anomalyDay + 3);
        // events.addAll(FileOperationActions.getSuccessfulLocalSharePermissionsChangedOperation(eventIdGen, myTimeGenerator, userGenerator));

        ITimeGenerator myTimeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(16, 00), 10, anomalyDay + 28, anomalyDay + 3);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_WRITE_DAC_PERMISSION_CHANGED, eventIdGen, myTimeGenerator2, userGenerator));

        //Anomaly:
        ITimeGenerator myTimeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(12, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getSuccessfulLocalSharePermissionsChangedOperation(eventIdGen, myTimeGenerator3, userGenerator));

        return events;
    }

    /** ********************************************** ABNORMAL ACTIONS delete, rename, create ******************************************* **/
    public static List<FileEvent> getAbnormalFileActionOperationType(String testUser, int anomalyDAy) throws GeneratorException {
        List<FileEvent> events = getAbnormalFileActionOperationType( testUser, 30, anomalyDAy + 4, anomalyDAy);
        return events;
    }

    public static List<FileEvent> getAbnormalFileActionOperationType(String testUser, int historicalStartDay, int anomalyStartDay, int anomalyEndDay) throws GeneratorException {
        /**
         * Normal behavior: user perform many file action (open, move)
         * Anomaly: user perform abnormal file action operation type (delete, rename, create) for several consequential days
         */
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        List<FileEvent> events = getFileActionOperationNormalEvents(eventIdGen, userGenerator, historicalStartDay, anomalyStartDay );
        events.addAll(getFileOperationAbnormalEvents(eventIdGen, userGenerator, anomalyStartDay, anomalyEndDay));

        return events;
    }

    private static List<FileEvent> getFileActionOperationNormalEvents(EntityEventIDFixedPrefixGenerator eventIdGen, SingleUserGenerator userGenerator, int firstHistoricalDays, int lastHistoricalDay) throws GeneratorException {
        ITimeGenerator myTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(9, 00), 60, firstHistoricalDays, lastHistoricalDay - 1);
        List<FileEvent> events = FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_OPENED, eventIdGen, myTimeGenerator, userGenerator);

        ITimeGenerator myTimeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(16, 00), 10, firstHistoricalDays, lastHistoricalDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_MOVED, eventIdGen, myTimeGenerator2, userGenerator));
        return events;
    }

    private static List<FileEvent>  getFileOperationAbnormalEvents(EntityEventIDFixedPrefixGenerator eventIdGen, SingleUserGenerator userGenerator, int firstAnomalyDay, int lastAnomalyDay ) throws GeneratorException {
        ITimeGenerator myTimeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(12, 30), 1, firstAnomalyDay, lastAnomalyDay - 1);
        List<FileEvent> events = FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_DELETED, eventIdGen, myTimeGenerator3, userGenerator);

        ITimeGenerator myTimeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(12, 30), 1, firstAnomalyDay, lastAnomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_RENAMED, eventIdGen, myTimeGenerator4, userGenerator));

        ITimeGenerator myTimeGenerator5 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(12, 30), 1, firstAnomalyDay, lastAnomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_CREATED, eventIdGen, myTimeGenerator5, userGenerator));

        return events;
    }
    /** ********************************************** ABNORMAL ACTIONS delete, rename, create ******************************************* **/

    public static List<FileEvent> createFilePermissionChangeAnomalyAndActionAnomalyForEnd2endAndOutputTests(String testUser, int anomalyDay) throws GeneratorException {

        /**
         * Normal behavior: user using in "PermissionChange" and "FileAction"
         * Anomaly: user using in different file action and different file_permission_change actions
         * this anomaly created according to the higher model of the two (PermissionChange and FileAction)
         */

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        ITimeGenerator myTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 45, anomalyDay + 8, anomalyDay + 4);
        List<FileEvent> events = FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED, eventIdGen, myTimeGenerator, userGenerator);

        ITimeGenerator myTimeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 45, anomalyDay + 4, anomalyDay + 3);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED, eventIdGen, myTimeGenerator2, userGenerator));

        ITimeGenerator myTimeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 30), LocalTime.of(17, 30), 45, anomalyDay + 3, anomalyDay + 2);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_RENAMED, eventIdGen, myTimeGenerator3, userGenerator));

        ITimeGenerator myTimeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(7, 30), LocalTime.of(17, 30), 45, anomalyDay + 2, anomalyDay + 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_OPENED, eventIdGen, myTimeGenerator4, userGenerator));

        ITimeGenerator myTimeGenerator5 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(16, 45), 45, anomalyDay + 1, anomalyDay);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_CLASSIFICATION_CHANGED, eventIdGen, myTimeGenerator5, userGenerator));

        // Not anomaly :
        ITimeGenerator myTimeGenerator6 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(10, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED, eventIdGen, myTimeGenerator6, userGenerator));

        ITimeGenerator myTimeGenerator7 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(14, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED, eventIdGen, myTimeGenerator7, userGenerator));


        // Anomalies:
        ITimeGenerator myTimeGenerator8 =
                new MinutesIncrementTimeGenerator(LocalTime.of(15, 00), LocalTime.of(16, 30), 07, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_CLASSIFICATION_CHANGED, eventIdGen, myTimeGenerator8, userGenerator));

        ITimeGenerator myTimeGenerator9 =
                new MinutesIncrementTimeGenerator(LocalTime.of(16, 30), LocalTime.of(17, 30), 07, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_MOVED, eventIdGen, myTimeGenerator9, userGenerator));

        ITimeGenerator myTimeGenerator10 =
                new MinutesIncrementTimeGenerator(LocalTime.of(20, 30), LocalTime.of(23, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FILE_CENTRAL_ACCESS_POLICY_CHANGED, eventIdGen, myTimeGenerator10, userGenerator));

        ITimeGenerator myTimeGenerator11 =
                new MinutesIncrementTimeGenerator(LocalTime.of(20, 30), LocalTime.of(23, 00), 30, anomalyDay, anomalyDay - 1);
        events.addAll(FileOperationActions.getFailedFileOperation(FILE_OPERATION_TYPE.FILE_ACCESS_RIGHTS_CHANGED, eventIdGen, myTimeGenerator11, userGenerator));

        return events;
    }

    public static List<FileEvent> getAbnormalFilePermissionChange(String testUser, int historicalStartDay, int abnormalStartDay, int abnormalEndDay) throws GeneratorException {

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // normal behavior (normal operation)
        ITimeGenerator timeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 30, historicalStartDay, abnormalStartDay);
        List<FileEvent> events = FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED, eventIdGen, timeGenerator, userGenerator);

        // Not anomaly at abnormal days (normal operation):
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 30, abnormalStartDay, abnormalEndDay);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_ACCESS_RIGHTS_CHANGED, eventIdGen, timeGenerator1, userGenerator));

        // Anomaly  (abnormal operation, abnormal number of operations per hour):
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(15, 00), LocalTime.of(16, 30), 07, abnormalStartDay, abnormalEndDay);
        events.addAll(FileOperationActions.getFileOperation(FILE_OPERATION_TYPE.FOLDER_CLASSIFICATION_CHANGED, eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<FileEvent> getCustomOperationTypes(String testUser) throws GeneratorException {
        /**
         * This method is for all operation types (event types) verification, not for anomaly
         */

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser); // Not admin

        List<OperationType> operationTypes = buildOperationTypesList();
        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(operationTypes.toArray(new OperationType[0]));

        FileOperationGenerator operationGenerator = new FileOperationGenerator();
        operationGenerator.setOperationTypeGenerator(opTypeGenerator);

        FileEventsGenerator eventGenerator = new FileEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);
        eventGenerator.setFileOperationGenerator(operationGenerator);

        List<FileEvent> events = eventGenerator.generate();

        return events;
    }

    public static List<FileEvent> getAbnormalFileActionAndPermissionChange(String testUser, int historicalStartDay, int anomalyStartDay, int anomalyEndDay) throws GeneratorException {
        List<FileEvent> events = getAbnormalFileActionOperationType(testUser, historicalStartDay, anomalyStartDay, anomalyEndDay);
        events.addAll(getAbnormalFilePermissionChange(testUser, historicalStartDay, anomalyStartDay, anomalyEndDay));
        return events;
    }

    private static List<OperationType> buildOperationTypesList() {
        List<OperationType> operationTypes = new ArrayList();
        operationTypes.add(new OperationType("Local share removed"));
        operationTypes.add(new OperationType("Local share permissions changed"));
        operationTypes.add(new OperationType("Local share folder path changed"));
        operationTypes.add(new OperationType("Local share added"));
        operationTypes.add(new OperationType("Junction point deleted"));
        operationTypes.add(new OperationType("Junction point created"));
        operationTypes.add(new OperationType("Folder renamed"));
        operationTypes.add(new OperationType("Folder ownership changed"));
        operationTypes.add(new OperationType("Folder opened"));
        operationTypes.add(new OperationType("Folder moved"));
        operationTypes.add(new OperationType("Folder deleted"));
        operationTypes.add(new OperationType("Folder created"));
        operationTypes.add(new OperationType("Folder classification changed"));
        operationTypes.add(new OperationType("Folder central access policy changed"));
        operationTypes.add(new OperationType("Folder auditing changed"));
        operationTypes.add(new OperationType("Folder attribute changed"));
        operationTypes.add(new OperationType("Folder access rights changed"));
        operationTypes.add(new OperationType("File renamed"));
        operationTypes.add(new OperationType("File ownership changed"));
        operationTypes.add(new OperationType("File opened"));
        operationTypes.add(new OperationType("File moved"));
        operationTypes.add(new OperationType("File deleted"));
        operationTypes.add(new OperationType("File created"));
        operationTypes.add(new OperationType("File classification changed"));
        operationTypes.add(new OperationType("File central access policy changed"));
        operationTypes.add(new OperationType("File auditing changed"));
        operationTypes.add(new OperationType("File attribute changed"));
        operationTypes.add(new OperationType("File access rights changed"));
        operationTypes.add(new OperationType("Failed share access (NTFS permissions)"));
        operationTypes.add(new OperationType("Failed share access (Change Auditor Protection)"));
        operationTypes.add(new OperationType("Failed folder access (NTFS permissions)"));
        operationTypes.add(new OperationType("Failed folder access (Change Auditor Protection)"));
        operationTypes.add(new OperationType("Failed file access (NTFS permissions)"));
        operationTypes.add(new OperationType("Failed file access (Change Auditor Protection)"));
        operationTypes.add(new OperationType("NetApp Folder renamed"));
        operationTypes.add(new OperationType("NetApp Folder ownership changed (no from-value)"));
        operationTypes.add(new OperationType("NetApp Folder moved"));
        operationTypes.add(new OperationType("NetApp Folder deleted"));
        operationTypes.add(new OperationType("NetApp Folder created"));
        operationTypes.add(new OperationType("NetApp Folder access rights changed (no from-value)"));
        operationTypes.add(new OperationType("NetApp File renamed"));
        operationTypes.add(new OperationType("NetApp File ownership changed (no from-value)"));
        operationTypes.add(new OperationType("NetApp File opened"));
        operationTypes.add(new OperationType("NetApp File moved"));
        operationTypes.add(new OperationType("NetApp File deleted"));
        operationTypes.add(new OperationType("NetApp File created"));
        operationTypes.add(new OperationType("NetApp File access rights changed (no from-value)"));
        operationTypes.add(new OperationType("EMC Folder renamed"));
        operationTypes.add(new OperationType("EMC Folder ownership changed"));
        operationTypes.add(new OperationType("EMC Folder moved"));
        operationTypes.add(new OperationType("EMC Folder deleted"));
        operationTypes.add(new OperationType("EMC Folder created"));
        operationTypes.add(new OperationType("EMC Folder access rights changed"));
        operationTypes.add(new OperationType("EMC File renamed"));
        operationTypes.add(new OperationType("EMC File ownership changed"));
        operationTypes.add(new OperationType("EMC File opened"));
        operationTypes.add(new OperationType("EMC File moved"));
        operationTypes.add(new OperationType("EMC File deleted"));
        operationTypes.add(new OperationType("EMC File created"));
        operationTypes.add(new OperationType("EMC File access rights changed"));
        operationTypes.add(new OperationType("CEPP configuration changed"));
        operationTypes.add(new OperationType("FluidFS Folder renamed"));
        operationTypes.add(new OperationType("FluidFS Folder ownership changed"));
        operationTypes.add(new OperationType("FluidFS Folder moved"));
        operationTypes.add(new OperationType("FluidFS Folder deleted"));
        operationTypes.add(new OperationType("FluidFS Folder created"));
        operationTypes.add(new OperationType("FluidFS Folder auditing changed"));
        operationTypes.add(new OperationType("FluidFS Folder access rights changed"));
        operationTypes.add(new OperationType("FluidFS File renamed"));
        operationTypes.add(new OperationType("FluidFS File ownership changed"));
        operationTypes.add(new OperationType("FluidFS File opened"));
        operationTypes.add(new OperationType("FluidFS File moved"));
        operationTypes.add(new OperationType("FluidFS File deleted"));
        operationTypes.add(new OperationType("FluidFS File created"));
        operationTypes.add(new OperationType("FluidFS File contents written"));
        operationTypes.add(new OperationType("FluidFS File auditing changed"));
        operationTypes.add(new OperationType("FluidFS File access rights changed"));

        return operationTypes;
    }
}
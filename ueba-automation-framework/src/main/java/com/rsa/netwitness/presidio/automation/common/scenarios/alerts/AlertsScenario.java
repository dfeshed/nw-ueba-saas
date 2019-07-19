package com.rsa.netwitness.presidio.automation.common.scenarios.alerts;

import com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdDateTimeAnomalies;
import com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdHighNumberOfOperations;
import com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdOperationActions;
import com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdOperationTypeAnomalies;
import com.rsa.netwitness.presidio.automation.common.scenarios.authentication.*;
import com.rsa.netwitness.presidio.automation.common.scenarios.file.FileDateTimeAnomalies;
import com.rsa.netwitness.presidio.automation.common.scenarios.file.FileHighNumberOfOperations;
import com.rsa.netwitness.presidio.automation.common.scenarios.file.FileOperationActions;
import com.rsa.netwitness.presidio.automation.common.scenarios.file.FileOperationTypeAnomalies;
import com.rsa.netwitness.presidio.automation.common.scenarios.process.ProcessHighNumberOfOperations;
import com.rsa.netwitness.presidio.automation.common.scenarios.process.ProcessOperationActions;
import com.rsa.netwitness.presidio.automation.common.scenarios.process.ProcessOperationAnomalies;
import com.rsa.netwitness.presidio.automation.common.scenarios.registry.RegistryOperationActions;
import com.rsa.netwitness.presidio.automation.common.scenarios.registry.RegistryOperationAnomalies;
import org.apache.commons.lang.ArrayUtils;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.domain.event.registry.RegistryEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.StringCyclicValuesGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.common.helpers.UserNamesList.*;

public class AlertsScenario {
    private static final int SILENT_USERS_NUM = 350; // put more to enlarge amount of users in the system

    private List<FileEvent> fileEvents = new ArrayList<>();
    private List<ActiveDirectoryEvent> adEvents = new ArrayList<>();
    private List<AuthenticationEvent> authenticationEvents = new ArrayList<>();
    private List<ProcessEvent> processEvents = new ArrayList<>();
    private List<RegistryEvent> registryEvents = new ArrayList<>();

    private int historicalStartDay = 35;
    private int anomalyDay = 2;

    public AlertsScenario(int historicalStartDay, int anomalyDay) throws GeneratorException {
        this.historicalStartDay = historicalStartDay;
        this.anomalyDay = anomalyDay;
        generateEvents();
    }

    private void generateEvents() throws GeneratorException {

        IStringGenerator userNameEPGenerator = new StringCyclicValuesGenerator((String[]) ArrayUtils.addAll(USER_NAMES_DEMO, USER_NAMES_ENDPOINT));
        IStringGenerator userNameGenerator = new StringCyclicValuesGenerator(USER_NAMES);

        fileEvents.addAll(FileOperationActions.alertsSanityTestEvents(historicalStartDay, anomalyDay - 1, 1));
        adEvents = AdOperationActions.alertsSanityTestEvents(historicalStartDay, anomalyDay - 1);
        authenticationEvents = AuthenticationOperationActions.alertsSanityTestEvents(historicalStartDay, anomalyDay - 1);
        processEvents = ProcessOperationActions.alertsSanityTestEvents(historicalStartDay, anomalyDay - 1, 1);
        registryEvents = RegistryOperationActions.alertsSanityTestEvents(historicalStartDay, anomalyDay - 1);

        /** Generate events */
        String someUserName = userNameGenerator.getNext();
        fileEvents.addAll(FileDateTimeAnomalies.getAbnormalTimeActivity("TestContains\\" + someUserName, anomalyDay));
        fileEvents.addAll(FileOperationTypeAnomalies.createFilePermissionChangeAnomalyAndActionAnomaly("TestContains\\" + someUserName, anomalyDay)); //supplement
        fileEvents.addAll(FileDateTimeAnomalies.getFrequentAbnormalTimeActivity(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileOperationTypeAnomalies.createFilePermissionChangeAnomalyAndActionAnomaly(userNameGenerator.getNext(), anomalyDay)); //supplement
        fileEvents.addAll(FileOperationTypeAnomalies.createFilePermissionChangeAnomalyAndActionAnomaly(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumDeletionOperations(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumSuccessfulFileAction(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumFailedFileOperations(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumProtectedFileOperations(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileDateTimeAnomalies.getAbnormalTimeActivity(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumFolderOpenOperations(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumFileOpenOperations(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumRenameOperations(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumMoveOperationsUserAdmin(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumFailedPermissionChange(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumSuccessfulFileAction(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumSuccessfulPermissionChange(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileOperationTypeAnomalies.getAbnormalFilePermissionChange(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileOperationTypeAnomalies.getAbnormalFileActionOperationType(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileOperationTypeAnomalies.createFilePermissionChangeAnomalyAndActionAnomalyForEnd2endAndOutputTests(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileOperationTypeAnomalies.getAbnormalFileActionOperationType(userNameGenerator.getNext(), historicalStartDay, anomalyDay + 2, anomalyDay - 1));
        fileEvents.addAll(FileOperationTypeAnomalies.getAbnormalFileActionAndPermissionChange(userNameGenerator.getNext(), historicalStartDay, anomalyDay + 2, anomalyDay - 1));
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumOfFrequentFolderOpenOperations(userNameGenerator.getNext(), anomalyDay));
        fileEvents.addAll(FileHighNumberOfOperations.getFileUserAdmin(userNameGenerator.getNext(), anomalyDay + 4, anomalyDay + 1, anomalyDay - 1, anomalyDay));
        fileEvents.addAll(FileHighNumberOfOperations.getFileUserAdmin(userNameGenerator.getNext(), anomalyDay + 2, anomalyDay + 1, anomalyDay + 4, anomalyDay + 2));

        adEvents.addAll(AdDateTimeAnomalies.getAbnormalFarFromNormalActivity(userNameGenerator.getNext(), anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getHighNumSuccessfulActiveDirectoryOperations(userNameGenerator.getNext(), anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getHighNumFailedActiveDirectoryEvents(userNameGenerator.getNext(), anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getHighNumProtectedActiveDirectoryEvents(userNameGenerator.getNext(), anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getAdminChangedHisOwnPassword(userNameGenerator.getNext(), anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getHighNumFailedActiveDirectoryInitiatorUserEvents(userNameGenerator.getNext(), anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getHighNumGroupMembershipEvents(userNameGenerator.getNext(), anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getMultipleUserAccountChangesEvents(userNameGenerator.getNext(), anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getHighNumSensitiveGroupMembershipEvents(userNameGenerator.getNext(), anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getHighNumSuccessfulActiveDirectoryOperations(userNameGenerator.getNext(), anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getHighNumSuccessfulSecuritySensitiveOperations(userNameGenerator.getNext(), anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getAbnormalObjectManagementOperations(userNameGenerator.getNext(), anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getAbnormalGroupChangesEvents(userNameGenerator.getNext(), anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getActiveDirectoryUserAdmin(userNameGenerator.getNext(), anomalyDay));

        authenticationEvents.addAll(AuthenticationDateTimeAnomalies.getAnomalyOnTwoNormalIntervalsActivity(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfSuccessfulAuthentications(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfFailedAuthentications(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfProtectedAuthentications(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfFailedAuthenticationsWithNullSrcAndDst(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfDistinctMachines(userNameGenerator.getNext(), false, anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfDistinctMachines(userNameGenerator.getNext(), true, anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfDistinctMachinesAndSameSrcDstMachines(userNameGenerator.getNext(), true, anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfSuccessfulAuthentications(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.getSequenceOfAbnormalMachineActivity(true, userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.getAbnormalDstMachineActivity(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.getAbnormalSrcMachineActivity(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfSuccessfulAuthentications(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.getAbnormalSrcAndDstMachines_emptyModel_onlyIpinNoramlbehvor(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.getAbnormalSrcAndDstMachines_includesSrcAndDestIPs(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.getAbnormalOperationType(false, userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getAuthenticationUserAdmin(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getAbnormalSite(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getLogonAttemptstoMultipleSourceComputersTEMP(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getLogonAttemptstoMultipleSourceComputers(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getAbnormalSite(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getLogonAttemptstoMultipleSourceComputers(userNameGenerator.getNext(), anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getFirstTimeFailedAuthentications(userNameGenerator.getNext(), anomalyDay));

        // registry schema indicator events
        registryEvents.addAll(RegistryOperationAnomalies.getAbnormalProcessModifiedServiceKey(userNameEPGenerator.getNext(), anomalyDay)); // "reg_modif_key_" + 1

        // process schema indicator events
        for (int i = 0; i < 4; i++) {
            processEvents.addAll(ProcessOperationAnomalies.getAbnormalProcessInjectedIntoLSASS(userNameEPGenerator.getNext(), anomalyDay));           //"proc_lsass_" + i
            processEvents.addAll(ProcessOperationAnomalies.getAbnormalProcessInjectedIntoWindowsProcess(userNameEPGenerator.getNext(), anomalyDay));  //"proc_win_" + i
            processEvents.addAll(ProcessOperationAnomalies.getAbnormalReconnaissanceTool(userNameEPGenerator.getNext(), anomalyDay));                 //"proc_abn_recon_" + i
            processEvents.addAll(ProcessOperationAnomalies.getAbnormalProcessExecutesScript(userNameEPGenerator.getNext(), anomalyDay));              //"proc_exec_script_" + i
            processEvents.addAll(ProcessOperationAnomalies.getAbnormalAppTriggeredByScript(userNameEPGenerator.getNext(), anomalyDay));               //"proc_trig_by_script_" + i
            processEvents.addAll(ProcessOperationAnomalies.getAbnormalProcessOpenedByScript(userNameEPGenerator.getNext(), anomalyDay));              // "proc_opened_by_script_" + i
            processEvents.addAll(ProcessHighNumberOfOperations.getHighNumOfDistinctReconnaissanceTools(userNameEPGenerator.getNext(), anomalyDay));   //"proc_recon_high_dist_" + i
            processEvents.addAll(ProcessHighNumberOfOperations.getHighNumOfReconnaissanceTools(userNameEPGenerator.getNext(), anomalyDay));           // "proc_recon_high_" + i
            processEvents.addAll(ProcessHighNumberOfOperations.getHighNumOfReconnaissanceToolsByUserAndTarget(userNameEPGenerator.getNext(), anomalyDay)); //"proc_recon_high_multi_" + i
            processEvents.addAll(ProcessOperationAnomalies.getReconnaissanceToolExecutedFirstTime(userNameEPGenerator.getNext(), anomalyDay));        // "proc_recon_firstuse_" + i
            processEvents.addAll(ProcessOperationAnomalies.getReconnaissanceToolUniqueExecutedFirstTime(userNameEPGenerator.getNext(), anomalyDay));  // "proc_recon_unique_" + i
            processEvents.addAll(ProcessOperationAnomalies.getAbnormalProcessInjectedIntoLSASSFirstTime(userNameEPGenerator.getNext(), anomalyDay));  //"proc_first_lsass_" + i
            processEvents.addAll(ProcessOperationAnomalies.getExecutesScriptFirstTime(userNameEPGenerator.getNext(), anomalyDay));                    // "proc_first_script_exec_" + i
        }

        // Noise reduction tests
        processEvents.addAll(ProcessOperationAnomalies.getAbnormalReconToolLowScore(userNameEPGenerator.getNext(), anomalyDay)); // "proc_recon_user_x"
        processEvents.addAll(ProcessOperationAnomalies.getAbnormalReconToolHighScore(userNameEPGenerator.getNext(), anomalyDay)); // "proc_recon_user_y"

        processEvents.addAll(ProcessOperationAnomalies.getAbnormalProcessExecutesScriptReducedScore(userNameEPGenerator.getNext(), anomalyDay)); //"proc_script_user_x"
        processEvents.addAll(ProcessOperationAnomalies.getAbnormalProcessExecutesScriptHighScore(userNameEPGenerator.getNext(), anomalyDay)); //"proc_script_user_y"

        // MIXED SCHEMAS
        adEvents.addAll(AdDateTimeAnomalies.getAbnormalFarFromNormalActivity("mixed_" + 1, anomalyDay));
        authenticationEvents.addAll(AuthenticationDateTimeAnomalies.getAnomalyOnTwoNormalIntervalsActivity("mixed_" + 1, anomalyDay));

        fileEvents.addAll(FileOperationTypeAnomalies.createFilePermissionChangeAnomalyAndActionAnomaly("mixed_" + 2, anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfSuccessfulAuthentications("mixed_" + 2, anomalyDay, LocalTime.of(20, 00), LocalTime.of(22, 00), 2));

        fileEvents.addAll(FileHighNumberOfOperations.getHighNumDeletionOperations("mixed_" + 3, anomalyDay));
        adEvents.addAll(getEventsForStaticPs("mixed_" + 3, anomalyDay));

        String mixedUser4 = userNameEPGenerator.getNext();
        fileEvents.addAll(FileHighNumberOfOperations.getHighNumDeletionOperations(mixedUser4, anomalyDay));
        processEvents.addAll(ProcessOperationAnomalies.getAbnormalProcessInjectedIntoWindowsProcess(mixedUser4, anomalyDay));
        processEvents.addAll(ProcessOperationAnomalies.getAbnormalProcessInjectedIntoLSASS(mixedUser4, anomalyDay));

        String mixedUser5 = userNameEPGenerator.getNext();
        adEvents.addAll(getEventsForStaticPs(mixedUser5, anomalyDay));
        registryEvents.addAll(RegistryOperationAnomalies.getAbnormalProcessModifiedServiceKey(mixedUser5, anomalyDay));

        String mixedUser6 = userNameEPGenerator.getNext();
        adEvents.addAll(AdHighNumberOfOperations.getAbnormalGroupChangesEvents(mixedUser6, anomalyDay));
        registryEvents.addAll(RegistryOperationAnomalies.getAbnormalProcessModifiedServiceKey(mixedUser6, anomalyDay));

        String mixedUser7 = userNameEPGenerator.getNext();
        adEvents.addAll(AdHighNumberOfOperations.getAdminChangedHisOwnPassword(mixedUser7, anomalyDay));
        processEvents.addAll(ProcessOperationAnomalies.getAbnormalProcessInjectedIntoLSASS(mixedUser7, anomalyDay));
        registryEvents.addAll(RegistryOperationAnomalies.getAbnormalProcessModifiedServiceKey(mixedUser7, anomalyDay));

        /** User with critical severity **/
        String mixedUser8 = userNameEPGenerator.getNext();
        adEvents.addAll(AdDateTimeAnomalies.getAbnormalFarFromNormalActivity(mixedUser8, anomalyDay));
        adEvents.addAll(AdHighNumberOfOperations.getHighNumSensitiveGroupMembershipEvents(mixedUser8, anomalyDay, LocalTime.of(2, 30), LocalTime.of(22, 30), 2));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfSuccessfulAuthentications(mixedUser8, anomalyDay));
        fileEvents.addAll(FileOperationTypeAnomalies.createFilePermissionChangeAnomalyAndActionAnomaly(mixedUser8, anomalyDay));
        registryEvents.addAll(RegistryOperationAnomalies.getAbnormalProcessModifiedServiceKey(mixedUser8, anomalyDay));
        processEvents.addAll(ProcessOperationAnomalies.getAbnormalProcessInjectedIntoWindowsProcess(mixedUser8, anomalyDay));
        processEvents.addAll(ProcessHighNumberOfOperations.getHighNumOfDistinctReconnaissanceTools(mixedUser8, anomalyDay));

        // generate all operations for categories test
        fileEvents.addAll(FileOperationTypeAnomalies.getCustomOperationTypes("file_optypes_and_categories"));
        adEvents.addAll(AdOperationTypeAnomalies.getCustomActiveDirOperations("ad_optypes_and_categories"));

        // statics only - smart will not be created
        adEvents.addAll(getEventsForStaticPs("ad_static_only", anomalyDay));
        // statics and time anomaly - smart will be created
        adEvents.addAll(getEventsForStaticPs("ad_contains_static", anomalyDay));
        adEvents.addAll(AdDateTimeAnomalies.getAbnormalFarFromNormalActivity("ad_contains_static", anomalyDay));

        /** Authentication scenario on Linux **/
        authenticationEvents.addAll(AuthenticationScenarios.getBruteForceScenarioOnLinux("Jane S.", anomalyDay));
        ITimeGenerator hackTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(5, 00), LocalTime.of(8, 00), 1, anomalyDay, anomalyDay - 1);
        ITimeGenerator breachTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(9, 00), 10, anomalyDay, anomalyDay - 1);
        /**  Jane's scenario : the breach happens in abnormal hours **/
        ITimeGenerator janesTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(5, 30), LocalTime.of(15, 30), 60, anomalyDay + 28, anomalyDay-1);
        authenticationEvents.addAll(AuthenticationScenarios.getBruteForceCustomTimeScenarioOnLinux("Jane S.", janesTimeGenerator, hackTimeGenerator, breachTimeGenerator));

        /** Natty's scenario is different from Jane's: Natty works at early hours, the breach happens at her work hours **/
        ITimeGenerator nattysTimeGenerator =
        new MinutesIncrementTimeGenerator(LocalTime.of(5, 30), LocalTime.of(15, 30), 60, anomalyDay + 28, anomalyDay-1);
        authenticationEvents.addAll(AuthenticationScenarios.getBruteForceCustomTimeScenarioOnLinux("Natty W.", nattysTimeGenerator, hackTimeGenerator, breachTimeGenerator));

        
        //      Users with normal activity
        for (int i = 0; i < SILENT_USERS_NUM; i++) processEvents.addAll(ProcessOperationAnomalies.getNormalProcessSchemaActivity(userNameEPGenerator.getNext()));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getMultipleNormalUsersActivity("auth_normal_user_", 50, anomalyDay));
        fileEvents.addAll(FileDateTimeAnomalies.getMultipleNormalUsersActivity("file_normal_user_", 10));
        adEvents.addAll(AdDateTimeAnomalies.getMultipleNormalUsersActivity("ad_normal_user_", 10));

    }

    private List<ActiveDirectoryEvent> getEventsForStaticPs(String testUser, int anomalyDay) throws GeneratorException {

        List<ActiveDirectoryEvent> events = new ArrayList<>();

        // One event at 10:00-11:00 every day
        ITimeGenerator normalTimeGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(9, 0), LocalTime.of(11, 0), 60, 35, anomalyDay + 3);

        // All static ops at 10:00-11:00, day -5
        ITimeGenerator abnormalTimeGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(9, 0), LocalTime.of(11, 0), 1, anomalyDay + 3, anomalyDay + 2);

        /** Generate events */
        events.addAll(com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdOperationTypeAnomalies.getNormalOperation4StaticPs(testUser, normalTimeGenerator)); //days -35-5
        normalTimeGenerator.reset();
        events.addAll(com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdOperationTypeAnomalies.getAllOperation4StaticPs(testUser, normalTimeGenerator));
        events.addAll(com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdOperationTypeAnomalies.getAllOperation4StaticPs(testUser, abnormalTimeGenerator));
        events.addAll(com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdOperationTypeAnomalies.getAdminChangedHisPassword(testUser, abnormalTimeGenerator));
        return events;
    }


    public List<FileEvent> getFileEvents() {
        return fileEvents;
    }

    public List<ActiveDirectoryEvent> getAdEvents() {
        return adEvents;
    }

    public List<AuthenticationEvent> getAuthenticationEvents() {
        return authenticationEvents;
    }

    public List<ProcessEvent> getProcessEvents() {
        return processEvents;
    }

    public List<AuthenticationEvent> getSortedAuthenticationEvents() {
        Comparator<Event> comparing = Comparator.comparing(Event::getDateTime, Comparator.naturalOrder());
        authenticationEvents.sort(comparing);
        return authenticationEvents;
    }

    public List<ActiveDirectoryEvent> getSortedActiveDirectoryEvents() {
        Comparator<Event> comparing = Comparator.comparing(Event::getDateTime, Comparator.naturalOrder());
        adEvents.sort(comparing);
        return adEvents;
    }

    public List<FileEvent> getSortedFileEvents() {
        Comparator<Event> comparing = Comparator.comparing(Event::getDateTime, Comparator.naturalOrder());
        fileEvents.sort(comparing);
        return fileEvents;
    }

    public List<ProcessEvent> getSortedProcessEvents() {
        Comparator<Event> comparing = Comparator.comparing(Event::getDateTime, Comparator.naturalOrder());
        processEvents.sort(comparing);
        return processEvents;
    }
    public List<RegistryEvent> getSortedRegistryEvents() {
        Comparator<Event> comparing = Comparator.comparing(Event::getDateTime, Comparator.naturalOrder());
        registryEvents.sort(comparing);
        return registryEvents;
    }
}

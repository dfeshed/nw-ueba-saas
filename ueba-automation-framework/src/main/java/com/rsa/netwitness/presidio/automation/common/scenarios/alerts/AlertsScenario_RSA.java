package com.rsa.netwitness.presidio.automation.common.scenarios.alerts;

import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import com.rsa.netwitness.presidio.automation.common.scenarios.activedirectory.AdOperationTypeAnomalies;
import com.rsa.netwitness.presidio.automation.common.scenarios.authentication.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AlertsScenario_RSA {
    private List<AuthenticationEvent> authenticationEvents = new ArrayList<>();

    private int historicalStartDay = 35;
    private int anomalyDay = 2;

    public AlertsScenario_RSA(int historicalStartDay, int anomalyDay) throws GeneratorException {
        this.historicalStartDay = historicalStartDay;
        this.anomalyDay = anomalyDay;
        generateEvents();
    }

    private void generateEvents() throws GeneratorException {
        String username = "qa";

        authenticationEvents = AuthenticationOperationActions.alertsSanityTestEvents(historicalStartDay, anomalyDay);

        /** Generate events */
        String scenarioUser = username + "_1_";
        authenticationEvents.addAll(AuthenticationDateTimeAnomalies.getAnomalyOnTwoNormalIntervalsActivity("auth_" + scenarioUser + 1, anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfSuccessfulAuthentications("auth_" + scenarioUser + 2, anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfFailedAuthentications("auth_" + scenarioUser + 3, anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfDistinctMachines("auth_" + scenarioUser + 4,false, anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfDistinctMachines("auth_" + scenarioUser + 5,true, anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfDistinctMachinesAndSameSrcDstMachines("auth_" + scenarioUser + 6,true, anomalyDay));
        authenticationEvents.addAll(AuthenticationHighNumberOfOperations.getHighNumOfSuccessfulAuthentications("auth_" + scenarioUser + 7, anomalyDay));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.getSequenceOfAbnormalMachineActivity(true, "auth_" + scenarioUser + 8, anomalyDay));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.getAbnormalDstMachineActivity("auth_" + scenarioUser + 9, anomalyDay));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.getAbnormalSrcMachineActivity("auth_" + scenarioUser + 10, anomalyDay));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.getAbnormalSrcAndDstMachines_emptyModel_onlyIpinNoramlbehvor("auth_" + scenarioUser + 11, anomalyDay));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.getAbnormalSrcAndDstMachines_includesSrcAndDestIPs("auth_" + scenarioUser + 12, anomalyDay));
        authenticationEvents.addAll(AuthenticationMachineAnomalies.getAbnormalOperationType(false,"auth_" + scenarioUser + 13, anomalyDay));
        authenticationEvents.addAll(AuthenticationOperationActions.getAllOperationTypes("auth_" + scenarioUser + 14));

        authenticationEvents.addAll(AuthenticationDateTimeAnomalies.getNormalTimeActivity(username + "_authentication_normal"));

        /** Events for filtering variations **/
        // 1. Destination (User@Domain) will include dollar ($) in the machine name. All events may be filtered by collector.
        ITimeGenerator happyHourTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(0, 00), LocalTime.of(0, 59), 10, anomalyDay - 1, anomalyDay - 2);
        authenticationEvents.addAll(AuthenticationFilterings_RSA.getDestMachineNameWithDollar("dollar_filter", happyHourTimeGenerator));

        // 2. Include only <Interactive, RemoteInteractive, CachedInteractive, Unlock>
        ITimeGenerator interactiveHourTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(1, 00), LocalTime.of(1, 59), 10, anomalyDay - 1, anomalyDay - 2);
        authenticationEvents.addAll(AuthenticationFilterings_RSA.getInteractiveOperations("interactive_filter", interactiveHourTimeGenerator));

        // 3. Do not include <Logoff, TGT>
        ITimeGenerator logoffHourTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(2, 00), LocalTime.of(2, 59), 10, anomalyDay - 1, anomalyDay - 2);
        authenticationEvents.addAll(AuthenticationFilterings_RSA.getInteractiveOperations("logoff_filter", logoffHourTimeGenerator));
        ITimeGenerator tgtHourTimeGenerator =
                new MinutesIncrementTimeGenerator(LocalTime.of(2, 05), LocalTime.of(2, 59), 10, anomalyDay - 1, anomalyDay - 2);
        authenticationEvents.addAll(AuthenticationFilterings_RSA.getInteractiveOperations("tgt_filter", tgtHourTimeGenerator));
    }

    private List<ActiveDirectoryEvent> getEventsForStaticPs(String testUser) throws GeneratorException {

        List<ActiveDirectoryEvent> events = new ArrayList<>();

        // One event at 10:00-11:00 every day
        ITimeGenerator normalTimeGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(9, 0), LocalTime.of(11, 0), 60, anomalyDay + 33, anomalyDay + 3);

        // All static ops at 10:00-11:00, day -5
        ITimeGenerator abnormalTimeGenerator = new MinutesIncrementTimeGenerator(LocalTime.of(9, 0), LocalTime.of(11, 0), 1, anomalyDay + 3, anomalyDay + 2);

        /** Generate events */
        events.addAll(AdOperationTypeAnomalies.getNormalOperation4StaticPs(testUser, normalTimeGenerator)); //days -35-5
        normalTimeGenerator.reset();
        events.addAll(AdOperationTypeAnomalies.getAllOperation4StaticPs(testUser, normalTimeGenerator));
        events.addAll(AdOperationTypeAnomalies.getAllOperation4StaticPs(testUser, abnormalTimeGenerator));
        events.addAll(AdOperationTypeAnomalies.getAdminChangedHisPassword(testUser, abnormalTimeGenerator));
        return events;
    }

    public List<AuthenticationEvent> getAuthenticationEvents() {
        return authenticationEvents;
    }
}

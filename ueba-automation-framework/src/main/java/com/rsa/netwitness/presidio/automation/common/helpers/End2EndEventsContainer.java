package com.rsa.netwitness.presidio.automation.common.helpers;

import com.rsa.netwitness.presidio.automation.common.scenarios.alerts.AlertsScenario;
import fortscale.utils.data.Pair;
import org.slf4j.LoggerFactory;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.domain.event.registry.RegistryEvent;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.common.helpers.DateTimeHelperUtils.getEventsInTimeRangeIdx;

public class End2EndEventsContainer {
    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(End2EndEventsContainer.class.getName());

    private End2EndEventsContainer end2EndEventsContainer;

//    private static List<PrintEvent> alertsScenarioPrintEvents;
    private List<AuthenticationEvent> alertsScenarioAuthenticationEvents;
    private List<ActiveDirectoryEvent> alertsScenarioActiveDirectoryEvents;
    private List<FileEvent> alertsScenarioFileEvents;
    private List<ProcessEvent> alertsScenarioProcessEvents;
    private List<RegistryEvent> alertsScenarioRegistryEvents;
//    private static List<IocEvent> alertsScenarioIocEvents;

    public void generateEvents(int historicalDaysBack, int anomalyDay) {
        LOGGER.info("\n===================== Events Generation Start =======================");
        //generate all test events data
        try {
            AlertsScenario alertsScenario = new AlertsScenario(historicalDaysBack, anomalyDay);
            alertsScenarioAuthenticationEvents = alertsScenario.getSortedAuthenticationEvents();
            alertsScenarioActiveDirectoryEvents = alertsScenario.getSortedActiveDirectoryEvents();
            alertsScenarioFileEvents = alertsScenario.getSortedFileEvents();
            alertsScenarioProcessEvents = alertsScenario.getSortedProcessEvents();
            alertsScenarioRegistryEvents = alertsScenario.getSortedRegistryEvents();
//            alertsScenarioIocEvents = alertsScenario.getSortedIocEvents();
        } catch (GeneratorException e) {
            e.printStackTrace();
        }
    }

    public List<? extends Event> getEvents (String schema, Instant start, Instant end) {
        if (schema.equalsIgnoreCase("ACTIVE_DIRECTORY")) return getActiveDirectoryEvents(start, end);
        if (schema.equalsIgnoreCase("AUTHENTICATION")) return getAuthenticationEvents(start, end);
        if (schema.equalsIgnoreCase("FILE")) return getFileEvents(start, end);
        if (schema.equalsIgnoreCase("PROCESS")) return getProcessEvents(start, end);
        if (schema.equalsIgnoreCase("REGISTRY")) return getRegistryEvents(start, end);
        System.out.println("Schema " + schema + "is not supported." );
        return null;
    }

    public List<Event> getAllEvents (Instant start, Instant end) {
        List<Event> allEvents = new ArrayList<>();
        allEvents.addAll(getActiveDirectoryEvents(start, end));
        allEvents.addAll(getAuthenticationEvents(start, end));
        allEvents.addAll(getFileEvents(start, end));
        allEvents.addAll(getProcessEvents(start, end));
        allEvents.addAll(getRegistryEvents(start, end));
        return allEvents;
    }


    public List<AuthenticationEvent> getAuthenticationEvents(Instant start, Instant end) {

        Pair<Integer,Integer> range = getEventsInTimeRangeIdx(alertsScenarioAuthenticationEvents, start, end);
        return alertsScenarioAuthenticationEvents.subList(range.getKey(), range.getValue());
    }

    public List<ActiveDirectoryEvent> getActiveDirectoryEvents(Instant start, Instant end) {

        Pair<Integer,Integer> range = getEventsInTimeRangeIdx(alertsScenarioActiveDirectoryEvents, start, end);
        return alertsScenarioActiveDirectoryEvents.subList(range.getKey(), range.getValue());
    }

    public List<FileEvent> getFileEvents(Instant start, Instant end) {

        Pair<Integer,Integer> range = getEventsInTimeRangeIdx(alertsScenarioFileEvents, start, end);
        return alertsScenarioFileEvents.subList(range.getKey(), range.getValue());
    }
   public List<ProcessEvent> getProcessEvents(Instant start, Instant end) {

        Pair<Integer,Integer> range = getEventsInTimeRangeIdx(alertsScenarioProcessEvents, start, end);
        return alertsScenarioProcessEvents.subList(range.getKey(), range.getValue());
    }
   public List<RegistryEvent> getRegistryEvents(Instant start, Instant end) {

        Pair<Integer,Integer> range = getEventsInTimeRangeIdx(alertsScenarioRegistryEvents, start, end);
        return alertsScenarioRegistryEvents.subList(range.getKey(), range.getValue());
    }

    public Instant getLatestEventTime(){
        Instant returnTime = Instant.MIN;
        Instant testTime = alertsScenarioAuthenticationEvents.get(alertsScenarioAuthenticationEvents.size()-1).getDateTime();
        if (returnTime.isBefore(testTime))  returnTime = testTime;
        testTime = alertsScenarioActiveDirectoryEvents.get(alertsScenarioActiveDirectoryEvents.size()-1).getDateTime();
        if (returnTime.isBefore(testTime))  returnTime = testTime;
        testTime = alertsScenarioFileEvents.get(alertsScenarioFileEvents.size()-1).getDateTime();
        if (returnTime.isBefore(testTime))  returnTime = testTime;
        testTime = alertsScenarioFileEvents.get(alertsScenarioProcessEvents.size()-1).getDateTime();
        if (returnTime.isBefore(testTime))  returnTime = testTime;
        testTime = alertsScenarioFileEvents.get(alertsScenarioRegistryEvents.size()-1).getDateTime();
        if (returnTime.isBefore(testTime))  returnTime = testTime;
//        testTime = alertsScenarioFileEvents.get(alertsScenarioIocEvents.size()-1).getDateTime();
//        if (returnTime.isBefore(testTime))  returnTime = testTime;

        return returnTime;
    }

}

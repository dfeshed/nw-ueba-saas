package com.rsa.netwitness.presidio.automation.common.scenarios.registry;

import presidio.data.domain.event.registry.RegistryEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.StringCyclicValuesGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.process.CyclicOperationTypeGenerator;
import presidio.data.generators.event.registry.RegistryEventsGenerator;
import presidio.data.generators.processentity.IProcessEntityGenerator;
import presidio.data.generators.registryentry.RegistryEntryGenerator;
import presidio.data.generators.registryop.RegistryOperationGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.common.helpers.UserNamesList.USER_NAMES;

public class RegistryOperationActions {

    public static List<RegistryEvent> getEventsByRegistryOperationName(String opName, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, IUserGenerator userGenerator) throws GeneratorException {
        timeGenerator.reset();
        RegistryEventsGenerator eventGenerator = new RegistryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        RegistryOperationGenerator opGenerator = new RegistryOperationGenerator();
        String[] operationTypeNames = {opName};
        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(operationTypeNames);
        opGenerator.setOperationTypeGenerator(opTypeGenerator);
        eventGenerator.setRegistryOperationGenerator(opGenerator);

        return eventGenerator.generate();
    }

    /*********************************    Registry operations:    *********************************/
    public static List<RegistryEvent> getRegistryKeyChangeOperations(IProcessEntityGenerator processEntityGenerator, EntityEventIDFixedPrefixGenerator eventIdGen, ITimeGenerator timeGenerator, SingleUserGenerator userGenerator) throws GeneratorException {


        RegistryEventsGenerator eventGenerator = new RegistryEventsGenerator();
        eventGenerator.setUserGenerator(userGenerator);
        eventGenerator.setTimeGenerator(timeGenerator);
        eventGenerator.setEventIdGenerator(eventIdGen);

        // The operation is "MODIFY_REGISTRY_VALUE"
        RegistryOperationGenerator opGenerator = new RegistryOperationGenerator();
        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(new String[]{"MODIFY_REGISTRY_VALUE"});
        opGenerator.setOperationTypeGenerator(opTypeGenerator);

        opGenerator.setProcessEntityGenerator(processEntityGenerator);

        // The registry entry - given list of entries
        RegistryEntryGenerator registryEntryGenerator = new RegistryEntryGenerator();
        StringCyclicValuesGenerator keysGenerator = new StringCyclicValuesGenerator(new String[] {
                "HKEY_LOCAL_MACHINE\\Software\\Microsoft\\Windows\\CurrentVersion\\Run",
                "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\some_service",
                "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\services\\SharedAccess\\Parameters\\FirewallPolicy\\FirewallRules",
//                "HKEY_LOCAL_MACHINE\\Software\\Microsoft\\Windows\\CurrentVersion\\policies\\system\\EnableLUA\\@DependOnService",
//                "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\services\\wscsvc\\Parameters\\@ServiceDll",
//                "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\Current Version\\Policies\\System\\@HideLegacyLogonScripts",
//                "HKEY_LOCAL_MACHINE\\SOFTWARE\\Policies\\Microsoft\\Windows\\WindowsUpdate\\AU\\@NoAutoRebootWithLoggedOnUsers",
//                "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\0\\@DisplayName",
                "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Office\\Delivery\\SourceEngine\\Downloads\\microsoft.watson.watsonrc16.data\\Sources\\watsonrcsrc"});

        /*
        * HKEY_LOCAL_MACHINE\Software\Microsoft\Windows\CurrentVersion\Run.
        * HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Run.
        * HKEY_LOCAL_MACHINE\Software\Microsoft\Windows\CurrentVersion\RunOnce.
        * HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\RunOnce.
        * */
        StringCyclicValuesGenerator keyGroupsGenerator = new StringCyclicValuesGenerator(new String[] {
                "RUN_KEY",
                "SERVICES_IMAGE_PATH",
                "FIREWALL_POLICY",
//                "LUA_SETTING_REGISTRY_EDITOR_SETTING",
//                "SECURITY_CENTER_CONFIGURATION",
//                "TASK_MANAGER_SETTING",
//                "WINDOWS_SYSTEM_POLICY",
//                "INTERNET_ZONE_SETTINGS",
                "BAD_CERTIFICATE_WARNING_SETTING"});
        StringCyclicValuesGenerator keyValueNamesGenerator = new StringCyclicValuesGenerator(new String[] {"\\@EmsService","\\@Owners","\\@{00636E44-6CD8-42AB-9575-A2DCA366C009}", "\\@Path"});

        registryEntryGenerator.setRegistryKeyGenerator(keysGenerator);
        registryEntryGenerator.setRegistryKeyGroupGenerator(keyGroupsGenerator);
        registryEntryGenerator.setRegistryValueNameGenerator(keyValueNamesGenerator);

        opGenerator.setRegistryEntryGenerator(registryEntryGenerator);

        eventGenerator.setRegistryOperationGenerator(opGenerator);
        return eventGenerator.generate();
    }

    public static List<RegistryEvent> alertsSanityTestEvents(int historicalStartDay, int anomalyDay) throws GeneratorException {
        List<RegistryEvent> events = new ArrayList<>();

        final String testUser1 = USER_NAMES[USER_NAMES.length-2];
        final String testCase = "e2e_registry_alerts";

        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testCase);

        ITimeGenerator normalTimeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(3,0), LocalTime.of(21,59), 30, historicalStartDay, anomalyDay);
        ITimeGenerator abnormalTimeGenerator1 = new MinutesIncrementTimeGenerator(LocalTime.of(0,0), LocalTime.of(1,0), 5, anomalyDay, anomalyDay - 1);

        SingleUserGenerator userGenerator1 = new SingleUserGenerator(testUser1);

        // Normal:
        events.addAll(RegistryOperationActions.getEventsByRegistryOperationName("CREATE_REGISTRY_VALUE", eventIdGen, normalTimeGenerator1, userGenerator1));

        // Anomalies:
        events.addAll(RegistryOperationActions.getEventsByRegistryOperationName("MODIFY_REGISTRY_VALUE", eventIdGen, abnormalTimeGenerator1, userGenerator1));

        return events;
    }
}

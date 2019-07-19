package com.rsa.netwitness.presidio.automation.common.scenarios.authentication;

import presidio.data.domain.User;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.user.SingleAdminUserGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationHighNumberOfOperations {
    public static List<AuthenticationEvent> getHighNumOfSuccessfulAuthentications(String testUser, int anomalyDay) throws GeneratorException {
        return getHighNumOfSuccessfulAuthentications(testUser, anomalyDay, LocalTime.of(10, 00), LocalTime.of(12, 30), 1);
    }

    public static List<AuthenticationEvent> getHighNumOfSuccessfulAuthentications(String testUser, int anomalyDay, LocalTime anomalyStartTime, LocalTime anomalyEndTime, int intervalMin) throws GeneratorException {
        List<AuthenticationEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 420, anomalyDay + 28, anomalyDay);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("SuccessfulAuthenticationOperation", eventIdGen, timeGenerator1, userGenerator));

        //Anomaly:
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(anomalyStartTime, anomalyEndTime, intervalMin, anomalyDay, anomalyDay - 1);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("SuccessfulAuthenticationOperation", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<AuthenticationEvent> getHighNumOfFailedAuthentications(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior:
         * Anomaly:
         *
         */
        List<AuthenticationEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, anomalyDay + 28, anomalyDay);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("FailedAuthenticationOperation", eventIdGen, timeGenerator1, userGenerator));

        //Anomaly:
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("FailedAuthenticationOperation", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<AuthenticationEvent> getFirstTimeFailedAuthentications(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior:
         * Anomaly:
         *
         */
        List<AuthenticationEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, anomalyDay + 28, anomalyDay);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("SuccessfulAuthenticationOperation", eventIdGen, timeGenerator1, userGenerator));

        //Anomaly:
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("FailedAuthenticationOperation", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<AuthenticationEvent> getHighNumOfFailedAuthenticationsWithNullSrcAndDst(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior:
         * Anomaly:
         *
         */
        List<AuthenticationEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, anomalyDay + 28, anomalyDay);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("FailedAuthenticationOperation", eventIdGen, timeGenerator1, userGenerator));

        //Anomaly:
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("FailedAuthenticationOperation", eventIdGen, timeGenerator2, userGenerator));

        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 00), LocalTime.of(9, 00), 60, anomalyDay, anomalyDay - 1);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("NullSrcMachineId", eventIdGen, timeGenerator3, userGenerator));

        ITimeGenerator timeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 00), LocalTime.of(10, 00), 60, anomalyDay, anomalyDay - 1);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("NullDstMachineId", eventIdGen, timeGenerator4, userGenerator));

        return events;
    }

    public static List<AuthenticationEvent> getHighNumOfProtectedAuthentications(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior:
         * Anomaly:
         *
         */
        List<AuthenticationEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, anomalyDay + 28, anomalyDay);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("ProtectedAuthenticationOperation", eventIdGen, timeGenerator1, userGenerator));

        //Anomaly:
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("ProtectedAuthenticationOperation", eventIdGen, timeGenerator2, userGenerator));

        return events;
    }

    public static List<AuthenticationEvent> getMediumNumOfFailedAuthentications(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior:
         *  - failed authentications - max 7, mean 3, sd 2
         *
         * Anomaly:
         *  - 7, 8, 9, failed authentications
         */
        List<AuthenticationEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 240, anomalyDay + 28, anomalyDay + 23);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("FailedAuthenticationOperation", eventIdGen, timeGenerator1, userGenerator));
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(11, 31), LocalTime.of(16, 30), 240, anomalyDay + 28, anomalyDay + 23);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("FailedAuthenticationOperation", eventIdGen, timeGenerator2, userGenerator));

        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 45), LocalTime.of(16, 30), 120, anomalyDay + 28, anomalyDay + 23);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("FailedAuthenticationOperation", eventIdGen, timeGenerator3, userGenerator));
        ITimeGenerator timeGenerator4 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 40), LocalTime.of(16, 30), 60, anomalyDay + 28, anomalyDay + 23);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("FailedAuthenticationOperation", eventIdGen, timeGenerator4, userGenerator));
        ITimeGenerator timeGenerator5 =
                new MinutesIncrementTimeGenerator(LocalTime.of(9, 00), LocalTime.of(10, 30), 10, anomalyDay + 28, anomalyDay + 23);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("FailedAuthenticationOperation", eventIdGen, timeGenerator5, userGenerator));

        //Anomaly: before work during 2h, 15 connections per hour
        ITimeGenerator timeGenerator6 =
                new MinutesIncrementTimeGenerator(LocalTime.of(6, 30), LocalTime.of(8, 30), 4, anomalyDay, anomalyDay - 1);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("FailedAuthenticationOperation", eventIdGen, timeGenerator6, userGenerator));

        return events;
    }

    public static List<AuthenticationEvent> getHighNumOfDistinctMachines(String testUser, boolean isSrcMachine, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior:
         * Anomaly:
         *
         */
        List<AuthenticationEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 30), LocalTime.of(16, 30), 60, anomalyDay + 28, anomalyDay);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("SuccessfulAuthenticationOperation", eventIdGen, timeGenerator1, userGenerator));

        //Anomaly:
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 00), LocalTime.of(12, 30), 1, anomalyDay, anomalyDay - 1);
        if (isSrcMachine) {
            events.addAll(AuthenticationOperationActions.getEventsByOperationName("DistinctSrcDomainsAuthenticationOperation", eventIdGen, timeGenerator2, userGenerator));
        } else {
            events.addAll(AuthenticationOperationActions.getEventsByOperationName("DistinctDstDomainsAuthenticationOperation", eventIdGen, timeGenerator2, userGenerator));
        }

        return events;
    }

    public static List<AuthenticationEvent> getHighNumOfDistinctMachinesAndSameSrcDstMachines(String testUser, boolean isSrcMachine, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior:
         * Anomaly:
         *
         */
        List<AuthenticationEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(17, 0), 60, anomalyDay + 28, anomalyDay);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("SuccessfulAuthenticationOperation", eventIdGen, timeGenerator1, userGenerator));

        //Anomaly:
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 0), LocalTime.of(12, 30), 1, anomalyDay, anomalyDay - 1);
        if (isSrcMachine) {
            events.addAll(AuthenticationOperationActions.getEventsByOperationName("DistinctSrcDomainsAuthenticationOperation", eventIdGen, timeGenerator2, userGenerator));
        } else {
            events.addAll(AuthenticationOperationActions.getEventsByOperationName("DistinctDstDomainsAuthenticationOperation", eventIdGen, timeGenerator2, userGenerator));
        }

        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(13, 0), LocalTime.of(15, 30), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AuthenticationOperationActions.getSameSrcDstMachinesAuthenticationOperation(eventIdGen, timeGenerator3, userGenerator));

        return events;
    }

    public static List<AuthenticationEvent> getAbnormalSite(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior:
         * Anomaly:
         *
         */

        String sites[]  = new String[]{"abnormal_site_a", "abnormal_site_b", "abnormal_site_c", "abnormal_site_d", "abnormal_site_e", "abnormal_site_f", "abnormal_site_g", "abnormal_site_h"};
        List<AuthenticationEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(17, 0), 60, anomalyDay + 28, anomalyDay);
        events.addAll(AuthenticationOperationActions.getAbnormalSiteAuthenticationOperation(eventIdGen,timeGenerator1,userGenerator, new String[] {"normal_site_home", "normal_site_work"}));

        //Anomaly:
        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(21, 0), LocalTime.of(23, 0), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AuthenticationOperationActions.getAbnormalSiteAuthenticationOperation(eventIdGen,timeGenerator2,userGenerator, new String[] {"normal_site_home", "normal_site_work"}));


        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 0), LocalTime.of(13, 0), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AuthenticationOperationActions.getAbnormalSiteAuthenticationOperation(eventIdGen,timeGenerator3,userGenerator, sites));

        return events;
    }

    public static List<AuthenticationEvent> getLogonAttemptstoMultipleSourceComputers(String testUser, int anomalyDay) throws GeneratorException {
        /**
         * Normal behavior:
         * Anomaly:
         *
         */

        String srcMachines[]  = new String[]{"abnormal_src_a", "abnormal_src_b", "abnormal_src_c", "abnormal_src_d", "abnormal_src_e", "abnormal_src_f", "abnormal_src_g", "abnormal_src_h", "abnormal_src_i", "abnormal_src_j", "normal_src_home", "normal_src_work"};
        List<AuthenticationEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(17, 0), 60, anomalyDay + 28, anomalyDay);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("SuccessfulAuthenticationOperation", eventIdGen, timeGenerator1, userGenerator));

        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(17, 0), 60, anomalyDay + 28, anomalyDay);
        events.addAll(AuthenticationOperationActions.getNumberOfDistinctSrcMachineNameRegexClusterAuthenticationOperation(eventIdGen,timeGenerator2,userGenerator, new String[] {"normal_src_home", "normal_src_work"}));

        //Anomaly:
        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 0), LocalTime.of(13, 0), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AuthenticationOperationActions.getNumberOfDistinctSrcMachineNameRegexClusterAuthenticationOperation(eventIdGen,timeGenerator3,userGenerator, srcMachines));

        return events;
    }

    public static List<AuthenticationEvent> getLogonAttemptstoMultipleSourceComputersTEMP(String testUser, int anomalyDay) throws GeneratorException {
        String srcMachines[]  = new String[]{"abnormal_src_a", "abnormal_src_b", "abnormal_src_c", "abnormal_src_d", "abnormal_src_e", "abnormal_src_f", "abnormal_src_g", "abnormal_src_h", "abnormal_src_i", "abnormal_src_j", "normal_src_home", "normal_src_work"};
        List<AuthenticationEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);
        SingleUserGenerator otherUserGenerator = new SingleUserGenerator(testUser + "_other");

        // Normal:
        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(17, 0), 60, anomalyDay + 28, anomalyDay);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("SuccessfulAuthenticationOperation", eventIdGen, timeGenerator1, userGenerator));

        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(17, 0), 60, anomalyDay + 28, anomalyDay);
        events.addAll(AuthenticationOperationActions.getNumberOfDistinctSrcMachineNameRegexClusterAuthenticationOperation(eventIdGen,timeGenerator2,otherUserGenerator, srcMachines));

        //Anomaly:
        ITimeGenerator timeGenerator3 =
                new MinutesIncrementTimeGenerator(LocalTime.of(10, 0), LocalTime.of(13, 0), 1, anomalyDay, anomalyDay - 1);
        events.addAll(AuthenticationOperationActions.getNumberOfDistinctSrcMachineNameRegexClusterAuthenticationOperation(eventIdGen,timeGenerator3,userGenerator, srcMachines));

        return events;
    }

    public static List<AuthenticationEvent> getAuthenticationUserAdmin(String testUser, int anomalyDay) throws GeneratorException {
        List<AuthenticationEvent> events = new ArrayList<>();
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testUser);
        SingleUserGenerator userGenerator = new SingleUserGenerator(testUser);
        SingleAdminUserGenerator adminUserGenerator = new SingleAdminUserGenerator(testUser);

        User user = new User(testUser);
        user.setUserId(testUser);
        user.setFirstName("authentication");
        user.setLastName("admin");
        user.setAdministrator(true);

        ITimeGenerator timeGenerator1 =
                new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(17, 0), 60, anomalyDay + 4, anomalyDay + 2);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("SuccessfulAuthenticationOperation", eventIdGen, timeGenerator1, userGenerator));

        ITimeGenerator timeGenerator2 =
                new MinutesIncrementTimeGenerator(LocalTime.of(20, 0), LocalTime.of(22, 0), 60, anomalyDay + 2, anomalyDay + 1);
        events.addAll(AuthenticationOperationActions.getEventsByOperationName("SuccessfulAuthenticationOperation", eventIdGen, timeGenerator2, adminUserGenerator));

        events.get(events.size() - 1).setUser(user);

        return events;
    }

    public static List<AuthenticationEvent> getMultipleNormalUsersActivity(String testUsersPrefix, int numberOfUsers, int anomalyDay) throws GeneratorException {
        List<AuthenticationEvent> events = new ArrayList<>();
        testUsersPrefix = testUsersPrefix + "_";
        for(int i=0 ; i < numberOfUsers ; i++) {
            String username = testUsersPrefix + i;
            EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(username);
            SingleUserGenerator userGenerator = new SingleUserGenerator(username);

            // Normal:
            ITimeGenerator timeGenerator1 =
                    new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(17, 0), 60, anomalyDay + 5, anomalyDay + 4);
            events.addAll(AuthenticationOperationActions.getEventsByOperationName("SuccessfulAuthenticationOperation", eventIdGen, timeGenerator1, userGenerator));
        }


        return events;
    }

}

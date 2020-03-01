package presidio.integration.performance.utils;

import com.rsa.netwitness.presidio.automation.enums.GeneratorFormat;

import java.time.Instant;

public class TestProperties {

    public int NUM_OF_GLOBAL_SERVER_MACHINES = -1;
    public int NUM_OF_GLOBAL_SERVER_MACHINES_CLUSTERS = -1;
    public String GLOBAL_SERVER_MACHINES_CLUSTER_PREFIX = null;
    public int NUM_OF_GLOBAL_SERVER_MACHINES_PER_CLUSTER = -1;
    public int NUM_OF_ADMIN_SERVER_MACHINES = -1;
    public int NUM_OF_ADMIN_SERVER_MACHINES_CLUSTERS = -1;
    public String ADMIN_SERVER_MACHINES_CLUSTER_PREFIX = null;
    public int NUM_OF_ADMIN_SERVER_MACHINES_PER_CLUSTER = -1;
    public int NUM_OF_LOCAL_SERVER_MACHINES = -1;
    public int NUM_OF_LOCAL_SERVER_MACHINES_CLUSTERS = -1;
    public String LOCAL_SERVER_MACHINES_CLUSTER_PREFIX = null;
    public int NUM_OF_LOCAL_SERVER_MACHINES_PER_CLUSTER = -1;


    public int AUTHENTICATION_NUM_OF_NORMAL_USERS = -1;
    public int AUTHENTICATION_NUM_OF_ADMIN_USERS = -1;
    public int AUTHENTICATION_NUM_OF_SERVICE_ACCOUNT_USERS = -1;
    public int FILE_NUM_OF_NORMAL_USERS = -1;
    public int FILE_NUM_OF_ADMIN_USERS = -1;
    public int FILE_NUM_OF_SERVICE_ACCOUNT_USERS = -1;
    public int ACTIVE_DIRECTORY_NUM_OF_NORMAL_USERS = -1;
    public int ACTIVE_DIRECTORY_NUM_OF_ADMIN_USERS = -1;
    public int ACTIVE_DIRECTORY_NUM_OF_SERVICE_ACCOUNT_USERS = -1;
    public int PROCESS_NUM_OF_NORMAL_USERS = -1;
    public int PROCESS_NUM_OF_ADMIN_USERS = -1;
    public int PROCESS_NUM_OF_SERVICE_ACCOUNT_USERS = -1;
    public int REGISTRY_NUM_OF_NORMAL_USERS = -1;
    public int REGISTRY_NUM_OF_ADMIN_USERS = -1;
    public int REGISTRY_NUM_OF_SERVICE_ACCOUNT_USERS = -1;
    public Instant startInstant = null;
    public Instant endInstant = null;
    public GeneratorFormat generatorFormat;
    public double usersProbabilityMultiplier;
    public double usersMultiplier;
    public double tlsAlertsProbability;
    public int tlsGroupsToCreate;
    public double tlsEventsPerDayPerGroup;
    public String schemas;


    public void print() {
        System.out.println("=================== TEST PARAMETERS =============== ");
        System.out.println("start_time: " + startInstant);
        System.out.println("end_time: " + endInstant);
        System.out.println("users_probability_multiplier: " + usersProbabilityMultiplier);
        System.out.println("users_multiplier: " + usersMultiplier);
        System.out.println("tls_alerts_probability: " + tlsAlertsProbability);
        System.out.println("tls_groups_to_create: " + tlsGroupsToCreate);
        System.out.println("tls_events_per_day_per_group: " + tlsEventsPerDayPerGroup);
        System.out.println("schemas: " + schemas);
        System.out.println("generatorFormat: " + generatorFormat);
        System.out.println("=================================================== ");
    }
    
}

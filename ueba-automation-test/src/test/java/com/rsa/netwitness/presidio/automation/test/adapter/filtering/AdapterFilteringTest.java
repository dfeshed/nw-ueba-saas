package com.rsa.netwitness.presidio.automation.test.adapter.filtering;

import com.rsa.netwitness.presidio.automation.common.validator.VerificationUtils;
import com.rsa.netwitness.presidio.automation.converter.events.ConverterEventBase;
import com.rsa.netwitness.presidio.automation.converter.events.MongoKeyValueEvent;
import com.rsa.netwitness.presidio.automation.converter.producers.MongoInputProducerImpl;
import com.rsa.netwitness.presidio.automation.converter.producers.NetwitnessEventsProducer;
import com.rsa.netwitness.presidio.automation.domain.activedirectory.AdapterActiveDirectoryStoredData;
import com.rsa.netwitness.presidio.automation.domain.authentication.AdapterAuthenticationStoredData;
import com.rsa.netwitness.presidio.automation.domain.config.HostConf;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.file.AdapterFileStoredData;
import com.rsa.netwitness.presidio.automation.domain.process.AdapterProcessStoredData;
import com.rsa.netwitness.presidio.automation.domain.process.AdapterRegistryStoredData;
import com.rsa.netwitness.presidio.automation.domain.repository.AdapterActiveDirectoryStoredDataRepository;
import com.rsa.netwitness.presidio.automation.domain.repository.AdapterAuthenticationStoredDataRepository;
import com.rsa.netwitness.presidio.automation.domain.repository.AdapterFileStoredDataRepository;
import com.rsa.netwitness.presidio.automation.domain.repository.AdapterProcessStoredDataRepository;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import com.rsa.netwitness.presidio.automation.utils.adapter.AdapterTestManager;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import fortscale.common.general.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.*;
import presidio.data.generators.common.GeneratorException;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class AdapterFilteringTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private AdapterTestManager adapterTestManager;
    @Autowired
    private AdapterActiveDirectoryStoredDataRepository activeDirectoryRepository;
    @Autowired
    private AdapterAuthenticationStoredDataRepository authenticationRepository;
    @Autowired
    private AdapterFileStoredDataRepository fileRepository;
    @Autowired
    private AdapterProcessStoredDataRepository processRepository;

    @Autowired
    private NetwitnessEventStore netwitnessEventStore;

    private Instant startDate = Instant.now();
    private Instant endDate = Instant.now();
    private int historicalDaysBack;
    private int anomalyDay;

    private static final String EXPECTED_RESULTS_DIR = "src/test/resources/ExpectedResultFiles/";
    private static Instant testStartTime;
    private String testName;
    private String expectedJsonAdapterProcess;
    private String expectedJsonAdapterRegistry;
    private String expectedJsonAdapterActiveDirectory;
    private String expectedJsonAdapterAuthentication;
    private String expectedJsonAdapterFile;

    private static final String HISTORICAL_DAYS_BACK_CONFIG_KEY = "historicalDaysBack";
    private static final String ANOMALY_DAY_CONFIG_KEY = "anomalyDay";

    private NetwitnessEventsProducer producer;

    @Parameters({"historical_days_back", "anomaly_day"})
    @BeforeClass
    public void setup(@Optional("10") int historicalDaysBack, @Optional("1") int anomalyDay) throws GeneratorException {
        this.endDate     = Instant.now().truncatedTo(ChronoUnit.DAYS);
        this.startDate   = Instant.now().minus(historicalDaysBack, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        this.historicalDaysBack = historicalDaysBack;
        this.anomalyDay = anomalyDay;

        adapterTestManager.clearAllCollections();
        producer = new MongoInputProducerImpl(netwitnessEventStore);
    }

    private static final Map<String, Object> validADEvent4741;
    static {
        Map<String, Object> authEvent = new HashMap<String, Object>();
        authEvent.put("event_source_id","4741");
        authEvent.put("event_time", Instant.now().minus(1,ChronoUnit.DAYS).toEpochMilli());
        authEvent.put("mongo_source_event_time",Instant.now().minus(1, ChronoUnit.DAYS));
        authEvent.put("user_dst","ad-test-user-1");
        authEvent.put("reference_id","4741");
        authEvent.put("event_type","SUCCESS");
        authEvent.put("user_src","user_src_4741");
        authEvent.put("device_type", "winevent_nic");
        validADEvent4741 = Collections.unmodifiableMap(authEvent);
    }

    private static final Map<String, Object> validADEvent4733;
    static {
        Map<String, Object> authEvent = new HashMap<String, Object>();
        authEvent.put("event_source_id","4733");
        authEvent.put("event_time", Instant.now().minus(1,ChronoUnit.DAYS).toEpochMilli());
        authEvent.put("mongo_source_event_time",Instant.now().minus(1, ChronoUnit.DAYS));
        authEvent.put("user_dst","ad-test-user-1");
        authEvent.put("reference_id","4733");
        authEvent.put("event_type","SUCCESS");
        authEvent.put("user_src","user_src_4733");
        authEvent.put("group","group_4733");
        authEvent.put("device_type", "winevent_nic");
        validADEvent4733 = Collections.unmodifiableMap(authEvent);
    }

    private static final Map<String, Object> validAuthenticationEvent4769;
    static {
        Map<String, Object> authEvent = new HashMap<String, Object>();
        authEvent.put("event_source_id","4769");
        authEvent.put("event_time", Instant.now().minus(1,ChronoUnit.DAYS).toEpochMilli());
        authEvent.put("mongo_source_event_time",Instant.now().minus(1, ChronoUnit.DAYS));
        authEvent.put("user_dst","auth-test-user-1");
        authEvent.put("reference_id","4769");
        authEvent.put("event_type","SUCCESS");
        authEvent.put("result_code","12345");
        authEvent.put("device_type", "winevent_nic");
        authEvent.put("service_name", "WIN-PY3ZJZTXPIL$");
        authEvent.put("service_id", "ACME\\WIN-PY3ZJZTXPIL$");
        validAuthenticationEvent4769 = Collections.unmodifiableMap(authEvent);
    }

    private static final Map<String, Object> validAuthenticationEventSecureId;
    static {
        Map<String, Object> authEvent = new HashMap<String, Object>();
        authEvent.put("device_type", "rsaacesrv");
        authEvent.put("sessionid","21654235");
        authEvent.put("event_time", Instant.now().minus(1,ChronoUnit.DAYS).toEpochMilli());
        authEvent.put("mongo_source_event_time",Instant.now().minus(1, ChronoUnit.DAYS));
        authEvent.put("user_dst","auth-secureId-user-1");
        authEvent.put("reference_id","rsaacesrv");
        authEvent.put("ec_outcome","Success");
        authEvent.put("host_src","auth_secureId_src_machine");
        validAuthenticationEventSecureId = Collections.unmodifiableMap(authEvent);
    }

    private static final Map<String, Object> validAuthenticationEventFailedSecureId;
    static {
        Map<String, Object> authEvent = new HashMap<String, Object>();
        authEvent.put("device_type", "rsaacesrv");
        authEvent.put("sessionid","21654236");
        authEvent.put("event_time", Instant.now().minus(1,ChronoUnit.DAYS).toEpochMilli());
        authEvent.put("mongo_source_event_time",Instant.now().minus(1, ChronoUnit.DAYS));
        authEvent.put("user_dst","auth-secureId-user-2");
        authEvent.put("reference_id","rsaacesrv");
        authEvent.put("ec_outcome","Failure");
        authEvent.put("host_src","auth_secureId_src_machine");
        validAuthenticationEventFailedSecureId = Collections.unmodifiableMap(authEvent);
    }

    private static final Map<String, Object> validAuthenticationEventSecureIdSystemUser;
    // service account user name should be join of user_dst @ host_src
    static {
        Map<String, Object> authEvent = new HashMap<String, Object>();
        authEvent.put("device_type", "rsaacesrv");
        authEvent.put("sessionid","21654237");
        authEvent.put("event_time", Instant.now().minus(1,ChronoUnit.DAYS).toEpochMilli());
        authEvent.put("mongo_source_event_time",Instant.now().minus(1, ChronoUnit.DAYS));
        authEvent.put("user_dst","system");
        authEvent.put("reference_id","rsaacesrv");
        authEvent.put("ec_outcome","Failure");
        authEvent.put("host_src","auth_secureId_src_machine");
        validAuthenticationEventSecureIdSystemUser = Collections.unmodifiableMap(authEvent);
    }

    private static final Map<String, Object> validAuthenticationEventSecureIdAnonymousUser;
    // service account user name should be join of user_dst @ host_src
    static {
        Map<String, Object> authEvent = new HashMap<String, Object>();
        authEvent.put("device_type", "rsaacesrv");
        authEvent.put("sessionid","21654238");
        authEvent.put("event_time", Instant.now().minus(1,ChronoUnit.DAYS).toEpochMilli());
        authEvent.put("mongo_source_event_time",Instant.now().minus(1, ChronoUnit.DAYS));
        authEvent.put("user_dst","anonymous logon");
        authEvent.put("reference_id","rsaacesrv");
        authEvent.put("ec_outcome","Failure");
        authEvent.put("host_src","auth_secureId_src_machine");
        validAuthenticationEventSecureIdAnonymousUser = Collections.unmodifiableMap(authEvent);
    }

    private static final Map<String, Object> validAuthenticationEventRhlinux;
    static {
        Map<String, Object> authEvent = new HashMap<String, Object>();
        authEvent.put("mongo_source_event_time",Instant.now().minus(2, ChronoUnit.DAYS));

        // Following 7 fields are mapped
        authEvent.put("sessionid","780521460486"); //eventId
        authEvent.put("event_time", Instant.now().minus(2,ChronoUnit.DAYS).toEpochMilli()); //dateTime
        authEvent.put("action", "/usr/sbin/sshd");  // dataSource
        authEvent.put("user_src", "terri");         // userId, userName, userDisplayName
        authEvent.put("event_type", "USER_AUTH");   // operationType, filter:cred_acq,user_auth,user_login
        authEvent.put("result", "success");         // result, resultCode
        authEvent.put("host_src", "axpreglfs1n02"); // srcMachineId, srcMachineName

        // used in query, with action
        authEvent.put("device_type", "rhlinux");

        // these fields are taken from real event, but not used by UEBA
        authEvent.put("ip_src", "10.100.99.11");
        authEvent.put("lc_cid","abcname1");
        authEvent.put("forward_ip", "127.0.0.1");
        authEvent.put("device_ip", "10.100.99.15");
        authEvent.put("medium", "32");
        authEvent.put("device_class", "Unix");
        authEvent.put("header_id" , "11111");
        authEvent.put("client" , "cname1");
        authEvent.put("event_desc" , "node=abc.grp type=USER_AUTH msg=audit(123456789.123:65578): pid=15699 uid=0 auid=4444444444444 ses=44444444445 subj=system_u:system_r:sshd_t:s0-s0:c0.c1023 msg='op=success acct=\"acc_my\" exe=\"/usr/sbin/sshd\" hostname=? addr=10.110.33.");
        authEvent.put("alias_host", "axpreglfs1n02");
        authEvent.put("msg", "cname1: node=abcname02.central.office.grp type=USER_AUTH msg=audit(1542777391.483:65578): pid=15619 uid=0 auid=4294967295 ses=4294967295 subj=system_u:system_r:sshd_t:s0-s0:c0.c1023 msg='op=success acct=\"terri\" exe=\"/usr/sbin/sshd\" hostname=? addr=1");
        authEvent.put("msg_id", "03810");
        authEvent.put("alias_host", "bcname02.central.office.grp");
        authEvent.put("net_block", "SOMETHING_IN_UPPER_CASE");
        authEvent.put("net_subnet", "SOMETHING_OTHER_IN_UPPER_CASE");
        authEvent.put("dst_net_block" , "SOMETHING_IN_UPPER_CASE");
        authEvent.put("dst_net_subnet" , "SOMETHING_OTHER_IN_UPPER_CASE");
        authEvent.put("process", "ssh");
        authEvent.put("event_cat_name", "System.Audit");
        authEvent.put("device_disc", "98");
        authEvent.put("device_disc_type", "rhlinux");
        authEvent.put("alert", "something strange");
        authEvent.put("did", "abcname1");
        authEvent.put("rid", "164247018940");
        authEvent.put("ip_all", "127.0.0.1");
        authEvent.put("ip_all", "10.100.99.12");
        authEvent.put("host_all", "bcname02");
        authEvent.put("host_all", "bcname02.central.office.grp");
        authEvent.put("user_all", "terri");
        authEvent.put("ip_all", "10.100.99.11");

        validAuthenticationEventRhlinux = Collections.unmodifiableMap(authEvent);
    }

    private static final Map<String, Object> validAuthenticationEvent4624;
    static {
        Map<String, Object> authEvent = new HashMap<String, Object>();
        authEvent.put("event_source_id","4624");
        authEvent.put("event_time", Instant.now().minus(2,ChronoUnit.DAYS).toEpochMilli());
        authEvent.put("mongo_source_event_time",Instant.now().minus(2, ChronoUnit.DAYS));
        authEvent.put("user_dst","auth-test-user-2");
        authEvent.put("reference_id","4624");
        authEvent.put("logon_type", "10");
        authEvent.put("event_type","SUCCESS");
        authEvent.put("alias_host","auth_test_src_machine_1");
        authEvent.put("result_code","12345");
        authEvent.put("device_type", "winevent_nic");
        authEvent.put("service_name", "WIN-PY3ZJZTXPIL$");
        authEvent.put("service_id", "ACME\\WIN-PY3ZJZTXPIL$");

        validAuthenticationEvent4624 = Collections.unmodifiableMap(authEvent);
    }

    private static final Map<String, Object> validAuthenticationEvent4625;
    static {
        Map<String, Object> authEvent = new HashMap<String, Object>();
        authEvent.put("event_source_id","4625");
        authEvent.put("event_time", Instant.now().minus(3,ChronoUnit.DAYS).toEpochMilli());
        authEvent.put("mongo_source_event_time",Instant.now().minus(3, ChronoUnit.DAYS));
        authEvent.put("user_dst","auth-test-user-3");
        authEvent.put("reference_id","4625");
        authEvent.put("logon_type", "2");
        authEvent.put("event_type","SUCCESS");
        authEvent.put("alias_host","auth_test_src_machine_3");
        authEvent.put("result_code","12345");
        authEvent.put("device_type", "winevent_nic");
        authEvent.put("service_name", "WIN-PY3ZJZTXPIL$");
        authEvent.put("service_id", "ACME\\WIN-PY3ZJZTXPIL$");
        validAuthenticationEvent4625 = Collections.unmodifiableMap(authEvent);
    }


    private static final Map<String, Object> validAuthenticationEvent4648;
    static {
        Map<String, Object> authEvent = new HashMap<String, Object>();
        authEvent.put("event_source_id","4648");
        authEvent.put("event_time", Instant.now().minus(3,ChronoUnit.DAYS).toEpochMilli());
        authEvent.put("mongo_source_event_time",Instant.now().minus(3, ChronoUnit.DAYS));
        authEvent.put("user_dst","auth-test-user-4");
        authEvent.put("reference_id","4648");
        authEvent.put("event_type","SUCCESS");
        authEvent.put("ip_src","auth_test_src_machine_4.somecompany.com");
        authEvent.put("result_code","12345");
        authEvent.put("device_type", "winevent_nic");
        authEvent.put("service_name", "WIN-PY3ZJZTXPIL$");
        authEvent.put("service_id", "ACME\\WIN-PY3ZJZTXPIL$");
        validAuthenticationEvent4648 = Collections.unmodifiableMap(authEvent);
    }

    private static final Map<String, Object> validFileEvent4660;
    static {
        Map<String, Object> fileEvent = new HashMap<String, Object>();
        fileEvent.put("event_time", Instant.now().minus(1,ChronoUnit.DAYS).toEpochMilli());
        fileEvent.put("mongo_source_event_time",Instant.now().minus(1, ChronoUnit.DAYS));
        fileEvent.put("user_dst","sometestuser1");
        fileEvent.put("reference_id","4660");
        fileEvent.put("category", "File System");
        fileEvent.put("event_type","SUCCESS");
        fileEvent.put("result_code","12345");
        fileEvent.put("event_source_id","4660");
        fileEvent.put("device_type", "winevent_nic");
        validFileEvent4660 = Collections.unmodifiableMap(fileEvent);
    }

    private static final Map<String, Object> validFileEvent4663;
    static {
        Map<String, Object> fileEvent = new HashMap<String, Object>();
        fileEvent.put("event_time", Instant.now().minus(2,ChronoUnit.DAYS).toEpochMilli());
        fileEvent.put("mongo_source_event_time",Instant.now().minus(2, ChronoUnit.DAYS));
        fileEvent.put("user_dst","sometestuser2");
        fileEvent.put("reference_id","4663");
        fileEvent.put("accesses", "WRITE_DAC");
        fileEvent.put("category", "File System");
        fileEvent.put("obj_name", "/path/to/object/file.name");
        fileEvent.put("event_type","SUCCESS");
        fileEvent.put("result_code","12345");
        fileEvent.put("event_source_id","4663");
        fileEvent.put("device_type", "winevent_nic");
        validFileEvent4663 = Collections.unmodifiableMap(fileEvent);
    }

    private static final Map<String, Object> validFileEvent4670;
    static {
        Map<String, Object> fileEvent = new HashMap<String, Object>();
        fileEvent.put("event_time", Instant.now().minus(3,ChronoUnit.DAYS).toEpochMilli());
        fileEvent.put("mongo_source_event_time",Instant.now().minus(3, ChronoUnit.DAYS));
        fileEvent.put("user_dst","sometestuser3");
        fileEvent.put("reference_id","4670");
        fileEvent.put("obj_type", "File");
        fileEvent.put("obj_name", "/path/to/object/file.name");
        fileEvent.put("event_type","SUCCESS");
        fileEvent.put("result_code","12345");
        fileEvent.put("event_source_id","4670");
        fileEvent.put("device_type", "winevent_nic");
        validFileEvent4670 = Collections.unmodifiableMap(fileEvent);
    }

    private static final Map<String, Object> validProcessEvent;
    static {
        Map<String, Object> processEvent = new HashMap<String, Object>();
        processEvent.put("event_source_id","process_ev_001");
        processEvent.put("event_time", Instant.now().minus(3,ChronoUnit.DAYS).toEpochMilli());
        processEvent.put("mongo_source_event_time",Instant.now().minus(3, ChronoUnit.DAYS));
        processEvent.put("device_type", "nw_endpoint_event");
        processEvent.put("user_src","sometestuser4");
        processEvent.put("action",new String[] {"OPEN_PROCESS"});
        processEvent.put("alias_host", new String[]{"tst_machine"});
        processEvent.put("owner", "tst_machine_owner");
        processEvent.put("directory_src","c:\\temp");
        processEvent.put("directory_group",new String[] {"TMP"});
        processEvent.put("filename_src","test_app.exe");
        processEvent.put("context","TEMP");
        processEvent.put("process_category","WORD_PROCESSOR");
        processEvent.put("cert_common","SomeCertAuthority");
        processEvent.put("directory_dst","c:\\Windows\\System32");
        processEvent.put("filename_dst","expand.exe");
        processEvent.put("context_target","SYSTEM32");
//        processEvent.put("process_category","WORD_PROCESSOR");
        validProcessEvent = Collections.unmodifiableMap(processEvent);
    }

    private static final Map<String, Object> validProcessEventMachineNorm;
    static {
        Map<String, Object> processEvent = new HashMap<String, Object>();
        processEvent.put("event_source_id","process_ev_002");
        processEvent.put("event_time", Instant.now().minus(3,ChronoUnit.DAYS).toEpochMilli());
        processEvent.put("mongo_source_event_time",Instant.now().minus(3, ChronoUnit.DAYS));
        processEvent.put("device_type", "nw_endpoint_event");
        processEvent.put("user_src","sometestuser4");
        processEvent.put("action",new String[] {"openProcess"});
        processEvent.put("alias_host", new String[]{"\\\\MACHINE\\NAME"}); // transformer will change to lowercase
        processEvent.put("owner", "tst_machine_owner");
        processEvent.put("directory_src","c:\\temp");
        processEvent.put("directory_group",new String[] {"tmp"});
        processEvent.put("filename_src","test_app.exe");
        processEvent.put("context","TEMP");
        processEvent.put("process_category","word processor");
        processEvent.put("cert_common","SomeCertAuthority");
        processEvent.put("directory_dst","c:\\Windows\\System32");
        processEvent.put("filename_dst","expand.exe");
        processEvent.put("context_target","system32");
//        processEvent.put("process_category","WORD_PROCESSOR");
        validProcessEventMachineNorm = Collections.unmodifiableMap(processEvent);
    }

    private List<Map<String, Object>> getValidFileEvents() {
        List<Map<String, Object>> validFileEvents = new ArrayList<>(6);
        int eventId = 0;

        /**  VALID  **/
        validFileEvents.add(new HashMap<>(validFileEvent4660)); eventId++;
        validFileEvents.add(new HashMap<>(validFileEvent4663)); eventId++;
        validFileEvents.add(new HashMap<>(validFileEvent4670)); eventId++;

        validFileEvents.add(mutateMetaField(validFileEvent4660, "device_type", "winevent_snare", eventId++));
        validFileEvents.add(mutateMetaField(validFileEvent4663, "device_type", "winevent_snare", eventId++));
        validFileEvents.add(mutateMetaField(validFileEvent4670, "device_type", "winevent_snare", eventId++));

        return validFileEvents;
    }

    private List<Map<String, Object>> getValidRegistryEvents() {
        List<Map<String, Object>> validProcessEvents = new ArrayList<>(1);
        int eventId = 0;

        /**  VALID  **/
        validProcessEvents.add(new HashMap<>(validProcessEvent)); eventId++;
        validProcessEvents.add(new HashMap<>(validProcessEventMachineNorm)); eventId++;

        return validProcessEvents;
    }

    private List<Map<String, Object>> getValidProcessEvents() {
        List<Map<String, Object>> validProcessEvents = new ArrayList<>(1);
        int eventId = 0;

        /**  VALID  **/
        validProcessEvents.add(new HashMap<>(validProcessEvent)); eventId++;
        validProcessEvents.add(new HashMap<>(validProcessEventMachineNorm)); eventId++;

        return validProcessEvents;
    }

    private List<Map<String, Object>> getValidAuthenticationEvents() {
        List<Map<String, Object>> validAuthenticationEvents = new ArrayList<>(40);
        int eventId = 0;

        /**  VALID  **/

        validAuthenticationEvents.add(new HashMap<>(validAuthenticationEvent4769)); eventId++;
        validAuthenticationEvents.add(new HashMap<>(validAuthenticationEvent4624)); eventId++;
        validAuthenticationEvents.add(new HashMap<>(validAuthenticationEvent4624)); eventId++;
        validAuthenticationEvents.add(new HashMap<>(validAuthenticationEvent4648)); eventId++;

        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEvent4769, "device_type", "winevent_snare", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEvent4624, "device_type", "winevent_snare", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEvent4625, "device_type", "winevent_snare", eventId++));

        // service name for 4769 must end with $
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEvent4624, "service_name", "krbtgt", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEvent4625, "service_name", "krbtgt", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEvent4769, "service_name", "krbtgt$", eventId++));

        // success/failure by event.type or result.code
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEvent4624, "event_type", "?succ?", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEvent4625, "event_type", "Failing all the time", eventId++));

        Map<String, Object> eventWithoutEventType = mutateMetaField(validAuthenticationEvent4769, "event_type", null, eventId++);
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEvent4769, "event_type", null, eventId++));

        validAuthenticationEvents.add(mutateMetaField(eventWithoutEventType, "result_code", "0x0", eventId++));
        validAuthenticationEvents.add(mutateMetaField(eventWithoutEventType, "result_code", "0x1", eventId++));
        validAuthenticationEvents.add(mutateMetaField(eventWithoutEventType, "result_code", "1x1", eventId++));
        validAuthenticationEvents.add(mutateMetaField(eventWithoutEventType, "result_code", "0x123", eventId++));
        validAuthenticationEvents.add(mutateMetaField(eventWithoutEventType, "result_code", null, eventId++));

        /* new in 11.3 - secureId */
        validAuthenticationEvents.add(new HashMap<>(validAuthenticationEventSecureId)); eventId++;
        validAuthenticationEvents.add(new HashMap<>(validAuthenticationEventFailedSecureId)); eventId++;
        validAuthenticationEvents.add(new HashMap<>(validAuthenticationEventSecureIdAnonymousUser)); eventId++;
        validAuthenticationEvents.add(new HashMap<>(validAuthenticationEventSecureIdSystemUser)); eventId++;

        /** new in 11.3 - rhlinux
         * ((action = '/usr/sbin/sshd' || action='/usr/bin/login') && device.type = 'rhlinux')
        * **/
        validAuthenticationEvents.add(new HashMap<>(validAuthenticationEventRhlinux)); eventId++;

        // sessionid - any string, mandatory
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "sessionid", "123", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "sessionid", "abc", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "sessionid", "$#_!*()-=+@%^&`~/\\\"", eventId++));

        // event_time - mandatory, Instant
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "event_time", String.valueOf(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli()), eventId++));

        // event_type (operationType) enum: CRED_ACQ, USER_AUTH, USER_LOGIN, case sensitive, required
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "event_type", "CRED_ACQ", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "event_type", "USER_AUTH", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "event_type", "USER_LOGIN", eventId++));

        // action - only two values possible, according to the query
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "action", "/usr/sbin/sshd", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "action", "/usr/bin/login", eventId++));

        // user_src - any string, including other language, mandatory, transformed to lowercase
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "user_src", "John Dow Jun'", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "user_src", "Uncle Mosheדוד משה", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "user_src", "LDAP://cn=Joe Smith,ou=East,dc=MyDomain,dc=com", eventId++));


        // result - must be SUCCESS or FAILURE, not case sensitive, or failXXXXXX
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "result", "sUccEss", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "result", "succeeded", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "result", "failed", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "result", "failing", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "result", null, eventId++));

        // host_src - any string, may be missing
        // normalized: removed '-', '.*:.*', ip address, network path (\\..\..), changed to lowercase
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "host_src", "abcd01", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "host_src", "abcd01.middle.ind.grp", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "host_src", "Z:\\myfolder", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "host_src", "\\\\mylan\\myfolder", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "host_src", null, eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "host_src", "host$-home\\++", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "host_src", "HOST-home", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "host_src", "home.a.b.cd", eventId++));
        validAuthenticationEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "host_src", "10.4.63.110", eventId++));

        return validAuthenticationEvents;
    }

    private List<Map<String, Object>> getValidActiveDirectoryEvents() {
        List<Map<String, Object>> validEvents = new ArrayList<>(6);
        int eventId = 0;

        /**  VALID  **/
        validEvents.add(new HashMap<>(validADEvent4741)); eventId++;
        validEvents.add(new HashMap<>(validADEvent4733)); eventId++;

        validEvents.add(mutateMetaField(validADEvent4741, "device_type", "winevent_snare", eventId++));
        validEvents.add(mutateMetaField(validADEvent4733, "device_type", "winevent_snare", eventId++));

        return validEvents;
    }

    private List<Map<String, Object>> getInvalidFileEvents() {
        List<Map<String, Object>> invalidFileEvents = new ArrayList<>(20);
        int eventId = 0;

        /**  INVALID  **/
        // device.type invalid
        invalidFileEvents.add(mutateMetaField (validFileEvent4660, "device_type", "some_unsupported_device_type", eventId++));
        invalidFileEvents.add(mutateMetaField (validFileEvent4663, "device_type", "some_unsupported_device_type", eventId++));

        // user.dst not contain $
        invalidFileEvents.add(mutateMetaField(validFileEvent4660, "user_dst", "user$", eventId++));
        invalidFileEvents.add(mutateMetaField(validFileEvent4663, "user_dst", "user$", eventId++));
        invalidFileEvents.add(mutateMetaField(validFileEvent4670, "user_dst", "user$", eventId++));

        // 4660: category must be 'File System'
        invalidFileEvents.add(mutateMetaField(validFileEvent4660, "category", "Registry", eventId++));

        // 4663: acesses can't be DELETE or DeleteChild
        invalidFileEvents.add(mutateMetaField(validFileEvent4663, "accesses", "DELETE", eventId++));
        invalidFileEvents.add(mutateMetaField(validFileEvent4663, "accesses","DeleteChild", eventId++));

        // 4670: obj.type must be 'File'
        invalidFileEvents.add(mutateMetaField(validFileEvent4670, "obj_type", "Not File", eventId++));

        // 4670: event_type must be 'SUCCESS' or 'FAIL..'
        // TODO: Need to add verification that event is inserted into collection, but "result" field is absent
        //invalidFileEvents.add(mutateMetaField(validFileEvent4670, "event_type", "STRANGE", eventId++));

        return invalidFileEvents;
    }

    private List<Map<String, Object>> getInvalidAuthenticationEvents() {
        List<Map<String, Object>> invalidEvents = new ArrayList<>(20);
        int eventId = 0;

        /**  INVALID  **/
        // device.type invalid
        invalidEvents.add(mutateMetaField (validAuthenticationEvent4624, "device_type", "some_unsupported_device_type", eventId++));
        invalidEvents.add(mutateMetaField (validAuthenticationEvent4625, "device_type", "some_unsupported_device_type", eventId++));
        invalidEvents.add(mutateMetaField (validAuthenticationEvent4769, "device_type", "some_unsupported_device_type", eventId++));
        invalidEvents.add(mutateMetaField (validAuthenticationEvent4648, "device_type", "some_unsupported_device_type", eventId++));

        // user.dst not contain $
        invalidEvents.add(mutateMetaField(validAuthenticationEvent4624, "user_dst", "user$", eventId++));
        invalidEvents.add(mutateMetaField(validAuthenticationEvent4625, "user_dst", "user$", eventId++));
        invalidEvents.add(mutateMetaField(validAuthenticationEvent4769, "user_dst", "user$", eventId++));
        invalidEvents.add(mutateMetaField(validAuthenticationEvent4648, "user_dst", "user$", eventId++));

        // incorrect format - 4663 fields for 4769
        // invalidEvents.add(mutateMetaField(validFileEvent4663, "reference_id", "4769", eventId++));

        // service name for 4769 must end with $
        invalidEvents.add(mutateMetaField(validAuthenticationEvent4769, "service_name", "Service_name_without_dollar", eventId++));


        // logon.type can be only 2 or 10 for events 4624, 4625
        invalidEvents.add(mutateMetaField(validAuthenticationEvent4624, "logon_type", "3", eventId++));
        invalidEvents.add(mutateMetaField(validAuthenticationEvent4625, "logon_type", null, eventId++));

        //secure ID - service account with empty source machine
       // invalidEvents.add(mutateMetaField(validAuthenticationEventSecureIdAnonymousUser, "host_src", "", eventId++));

        /** device_type rhlinux **/
        // user_src must exist
        invalidEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "user_src", null, eventId++));
        // user name can not be "(unknown)"
        invalidEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "user_src", "(unknown)", eventId++));

        // action values are limited to /user/bin/login and /usr/bin/sshd
        invalidEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "action", null, eventId++));
        invalidEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "action", "/usr/bin/ssh", eventId++));

        // event type - only enumerator value allowed, in UPPERCASE
        invalidEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "event_type", "user_auth", eventId++));
        invalidEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "event_type", null, eventId++));

        // event_time - restricted object type - epoch
        invalidEvents.add(mutateMetaField(validAuthenticationEventRhlinux, "event_time", "abcd", eventId++));

        return invalidEvents;
    }

    private List<Map<String, Object>> getInvalidActiveDirectoryEvents() {
        List<Map<String, Object>> invalidEvents = new ArrayList<>(6);
        int eventId = 0;

        /**  INVALID  **/
        // device.type invalid
        invalidEvents.add(mutateMetaField (validADEvent4741, "device_type", "some_unsupported_device_type", eventId++));
        invalidEvents.add(mutateMetaField (validADEvent4733, "device_type", "some_unsupported_device_type", eventId++));

        // user.dst not contain $
        invalidEvents.add(mutateMetaField(validADEvent4741, "user_dst", "user$", eventId++));
        invalidEvents.add(mutateMetaField(validADEvent4733, "user_dst", "user$", eventId++));

        return invalidEvents;
    }

    private Map<String,Object> mutateMetaField(Map<String,Object> validFileEvent, String metaFieldKey, String mutationValue, int eventNo) {
        Map<String, Object> event = new HashMap<>(validFileEvent);
        if (mutationValue == null) {
            event.remove(metaFieldKey);
        } else {
            event.replace(metaFieldKey, mutationValue);
        }
        event.replace("event_source_id", validFileEvent.get("event_source_id") + "_" + Integer.toString(eventNo) + "_" + metaFieldKey + "_" + mutationValue);
        return event;
    }

    @BeforeMethod
    public void nameBefore(Method method) {
        testName = method.getName();
        expectedJsonAdapterProcess = EXPECTED_RESULTS_DIR + testName + ".json";
        expectedJsonAdapterRegistry = EXPECTED_RESULTS_DIR + testName + ".json";
        expectedJsonAdapterActiveDirectory = EXPECTED_RESULTS_DIR + testName + ".json";
        expectedJsonAdapterAuthentication = EXPECTED_RESULTS_DIR + testName + ".json";
        expectedJsonAdapterFile = EXPECTED_RESULTS_DIR + testName + ".json";
        HostConf.isGenerate = true;
    }

    @Test
    public void adapterFileFilteringTest(){
        // prepare events that should be filtered, call adapter utils to insert into mongo collection
        List<Map<String, Object>> validFileEvents = getValidFileEvents();
        List<Map<String, Object>> invalidFileEvents = getInvalidFileEvents();

        List<ConverterEventBase> eventsToSend = convertToProducerFormat(Stream.of(validFileEvents, invalidFileEvents), Schema.FILE);
        producer.send(eventsToSend);

        // process the data
        adapterTestManager.process(startDate, endDate, "hourly", "FILE");

        // verify the events filtered as expected
//        long actualEventsCount = fileRepository.count();
//        Assert.assertEquals(actualEventsCount, validFileEvents.size(), "File events where not filtered correctly");

        // Verify expected json
        List<AdapterFileStoredData> events = fileRepository.findAll();

        VerificationUtils vu = new VerificationUtils<AdapterFileStoredData>();
        vu.verify(events, expectedJsonAdapterFile);

    }

    @Test
    public void adapterAuthenticationTest(){
        // prepare events that should be filtered, call adapter utils to insert into mongo collection
        List<Map<String, Object>> validEvents = getValidAuthenticationEvents();

        Iterator<Map<String, Object>> iterator = validEvents.iterator();

        IntStream.range(0, validEvents.size()).sequential()
                .forEach(i -> {
                    // creates unique event_time for valid events in order to compare them to output
                    Map<String, Object> next = iterator.next();
                    long time = Long.parseLong(next.get("event_time").toString());
                    Instant event_time = Instant.ofEpochMilli(time);
                    Instant truncatedTime = event_time.truncatedTo(ChronoUnit.MILLIS);
                    Instant uniqueEvantTime = truncatedTime.plusMillis(i);
                    next.put("event_time", uniqueEvantTime.toEpochMilli());
                    next.put("mongo_source_event_time", uniqueEvantTime);

                    // 'action' must be array
                    if (next.containsKey("action")) next.put("action", Collections.singletonList(next.get("action")));
                });

        List<Map<String, Object>> invalidEvents = getInvalidAuthenticationEvents();
        invalidEvents.forEach(e-> e.remove("action"));

        List<ConverterEventBase> eventsToSend = convertToProducerFormat(Stream.of(validEvents, invalidEvents), Schema.AUTHENTICATION);
        producer.send(eventsToSend);

        // process the data
        adapterTestManager.process(startDate, endDate, "hourly", "AUTHENTICATION");

        // verify the events filtered as expected
        List<Instant> validEventsInput = validEvents.stream().map(e -> Instant.ofEpochMilli(Long.parseLong(e.get("event_time").toString()))).collect(Collectors.toList());
        List<Instant> validEventsOutput =authenticationRepository.findAll().stream().map(e -> e.getDateTime()).collect(Collectors.toList());

        List<Instant> validEventTimeInOutput = authenticationRepository.findAll().stream()
                .map(e -> e.getDateTime())
                .collect(Collectors.toList());

        Function<String,Boolean> f =  s -> validEventTimeInOutput.stream().map(Instant::toString).map(e->e.contains(s)).reduce(false, (agg,e)-> agg |=e );

        List<Map<String, Object>> validMissingInOutput = validEvents.stream()
                .filter(e -> !f.apply(e.get("mongo_source_event_time").toString()))
                .collect(Collectors.toList());

        if (!validMissingInOutput.isEmpty()) System.err.println(Arrays.toString(validMissingInOutput.toArray()));

        long actualEventsCount = authenticationRepository.count();
        Assert.assertEquals(actualEventsCount, validEvents.size(), "Authentication events where not filtered correctly");
        // Verify expected json
        List<AdapterAuthenticationStoredData> events = authenticationRepository.findAll();

        VerificationUtils vu = new VerificationUtils<AdapterAuthenticationStoredData>();
        vu.verify(events, expectedJsonAdapterAuthentication);
    }

    @Test
    public void adapterActiveDirectoryFilteringTest(){
        // prepare events that should be filtered, call adapter utils to insert into mongo collection
        List<Map<String, Object>> validEvents = getValidActiveDirectoryEvents();
        List<Map<String, Object>> invalidEvents = getInvalidActiveDirectoryEvents();

        List<ConverterEventBase> eventsToSend = convertToProducerFormat(Stream.of(validEvents, invalidEvents), Schema.ACTIVE_DIRECTORY);
        producer.send(eventsToSend);

        // process the data
        adapterTestManager.process(startDate, endDate, "hourly", "ACTIVE_DIRECTORY");

        // Verify expected json
        List<AdapterActiveDirectoryStoredData> adEvents = activeDirectoryRepository.findAll();

        VerificationUtils vu = new VerificationUtils<AdapterActiveDirectoryStoredData>();
        vu.verify(adEvents, expectedJsonAdapterActiveDirectory);
    }

    @Test
    public void adapterProcessFilteringTest(){
        // prepare events that should be filtered, call adapter utils to insert into mongo collection
        List<Map<String, Object>> validEvents = getValidProcessEvents();

        List<ConverterEventBase> eventsToSend = convertToProducerFormat(Stream.of(validEvents), Schema.PROCESS);
        producer.send(eventsToSend);

        // process the data
        adapterTestManager.process(startDate, endDate, "hourly", "PROCESS");

        // Verify expected json
        List<AdapterProcessStoredData> processEvents = processRepository.findAll();

        VerificationUtils vu = new VerificationUtils<AdapterProcessStoredData>();
        vu.verify(processEvents, expectedJsonAdapterProcess);
    }

    @Test
    public void adapterRegistryFilteringTest(){
        // prepare events that should be filtered, call adapter utils to insert into mongo collection
        List<Map<String, Object>> validEvents = getValidRegistryEvents();

        List<ConverterEventBase> eventsToSend = convertToProducerFormat(Stream.of(validEvents), Schema.REGISTRY);
        producer.send(eventsToSend);

        // process the data
        adapterTestManager.process(startDate, endDate, "hourly", "REGISTRY");

        // Verify expected json
        List<AdapterProcessStoredData> processEvents = processRepository.findAll();

        VerificationUtils vu = new VerificationUtils<AdapterRegistryStoredData>();
        vu.verify(processEvents, expectedJsonAdapterRegistry);
    }


    private List<ConverterEventBase> convertToProducerFormat(Stream<List<Map<String, Object>>> eventMaps, Schema schema) {
        return  eventMaps.flatMap(eventMap -> eventMap.stream())
                .map(e -> (ConverterEventBase) new MongoKeyValueEvent(e, Schema.FILE))
                .collect(Collectors.toList());
    }
}

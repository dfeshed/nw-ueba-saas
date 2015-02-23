package fortscale.streaming.service.vpn;

import fortscale.domain.events.VpnSession;
import fortscale.domain.schema.VpnEvents;
import fortscale.geoip.GeoIPInfo;
import fortscale.geoip.IpToLocationGeoIPService;
import fortscale.services.event.VpnService;
import fortscale.services.notifications.VpnGeoHoppingNotificationGenerator;
import fortscale.utils.junit.SpringAware;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Created by rans on 02/02/15.
 */
@RunWith(Parameterized.class)
@SuppressWarnings("InstanceMethodNamingConvention")
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:META-INF/spring/vpn-enrich-context-test.xml")
//used to clean spring context for next class:
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class VpnSessionUpdateServiceTest extends AbstractJUnit4SpringContextTests {
    //rules used to set JUnit parameters in SpringAware
    @ClassRule
    public static final SpringAware SPRING_AWARE = SpringAware.forClass(VpnSessionUpdateServiceTest.class);
    private static final String DATABUCKET_FIELD = "databucket";
    private static final String DURATION_FIELD = "duration";
    @Rule
    public TestRule springAwareMethod = SPRING_AWARE.forInstance(this);
    @Rule
    public TestName testName = new TestName();

    @Autowired
    VpnEnrichService vpnEnrichService;

    @Autowired
    @ReplaceWithMock
    IpToLocationGeoIPService geoIPServiceMock;
    @Autowired
    private VpnEvents vpnEvents;
    @Autowired
    @ReplaceWithMock
    VpnService vpnService;
    @Autowired
    private RecordToVpnSessionConverter recordToVpnSessionConverter;
    @Autowired
    @ReplaceWithMock
    private VpnGeoHoppingNotificationGenerator vpnGeoHoppingNotificationGenerator;

    List<VpnSession> vpnSessions;

    @Before
    public void runBefore(){
        vpnSessions = getVpnSessions();
    }

    //geolocation fields:
    private String inputTopic = "input-1";
    private String outputTopic = "output-1";
    private String partitionField = "partition-1";
    private String ipField = "ip_field";
//    private String countryFieldName = "country_field";
    private String countryIsoCodeFieldName = "country_code_field";
    private String regionFieldName = "region_field";
//    private String cityFieldName = "city_field";
    private String ispFieldName = "isp_field";
    private String usageTypeFieldName = "usage_type_field";
//    private String longtitudeFieldName = "longtitude_field";
//    private String latitudeFieldName = "latitude_field";

    private static String IP = "172.16.0.0";
    private static String PARTITION = "part-1";
    private static Long READ_BYTES;
    private static Long TOTAL_BYTES;
    private static Long DURATUIN;

    //data buckets fields:
//    private String totalbytesFieldName = "totalbytesFieldName";
    private String readbytesFieldName = "readbytesFieldName";
    private String durationFieldName = "durationFieldName";
    private String databucketFieldName = "databucketFieldName";

    //session update field:
    private String sessionIdFieldName = "session_id_field";
    private String addSessionDataName = "addsessiondata";
    private String countryCodeFieldName = "country_code_field";
    private String longtitudeFieldName = "longtitude_field";
    private String latitudeFieldName = "latitude_field";
    private String dateTimeUnixFieldName = "date_time_unix";
    private String statusFieldName = "status";
    private String cityFieldName = "city";
    private String countryFieldName = "country";
    private String usernameFieldName = "username";
    private String normalizedUsernameFieldName = "normalized_username";
//    private String

    GeoIPInfo geoIPInfo = new GeoIPInfo("");

    private String hostnameFieldName;
    private String localIpFieldName;
    private String sourcepFieldName;
    private String totalbytesFieldName;
    private String writebytesFieldName;

    //Constructor setting:
    String TEST_CASE;
    String EVENT;
    String UPDATED_LOCAL_IP;
    String STATUS;

    public VpnSessionUpdateServiceTest(String TEST_CASE, String eventObj, String updatedLocalIp, String status) {
        this.TEST_CASE = TEST_CASE;
        this.EVENT = eventObj;
        this.UPDATED_LOCAL_IP = updatedLocalIp;
        this.STATUS = status;

    }



    @Test
    @Parameters(name = "Run test: {index} {1})")
    public void testSessionUpdate() throws UnknownHostException {
        //stubs:
        when(vpnService.findByUsernameAndCreatedAtEpochGreaterThan(anyString(), anyLong())). thenReturn(vpnSessions);
        //init
        JSONObject event = (JSONObject)JSONValue.parse(EVENT);


        //run test:
        event = vpnEnrichService.processSessionUpdate(event);

        //Validations
//        verify(geoIPServiceMock).getGeoIPInfo(IP);

        assertEquals(inputTopic, vpnEnrichService.getInputTopic());
        assertEquals(outputTopic, vpnEnrichService.getOutputTopic());
        assertEquals(PARTITION, vpnEnrichService.getPartitionKey(event));
        assertEquals(UPDATED_LOCAL_IP, event.get("local_ip"));
        assertEquals(STATUS, event.get("status"));
        //assert session update fields
        reset(geoIPServiceMock);
    }

    private List<VpnSession> getVpnSessions() {
        List<VpnSession> vpnSessions = new ArrayList<VpnSession>();
        VpnSession vpnSession1 = new VpnSession();
        vpnSession1.setCreatedAtEpoch(1424695980000L);
        vpnSession1.setLocalIp("171.19.1.14");
        vpnSessions.add(vpnSession1);
        VpnSession vpnSession2 = new VpnSession();
        vpnSession2.setCreatedAtEpoch(1424695990000L);
        vpnSession2.setLocalIp("171.19.1.16");
        vpnSessions.add(vpnSession2);
        return vpnSessions;
    }

    @Parameters()
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]
                {
                        {
                                "VPN Session Update: Open",
                                "{'local_ip':'171.19.1.4','status':'SUCCESS','hostname':'my-pc1','writebytes':1200211,'durationFieldName':null,'date_time_unix':1424700169626,'city':'Jerusalem','country':'Israel','session_id_field':'12345AAA','duration':24,'username':'John Dow','ip_field':'172.16.0.0','source_ip':'10.19.121.11','partition-1':'part-1','normalized_username':'John Dow','readbytesFieldName':null,'databucket':23,'totalbytes':null}",
                                "171.19.1.4",
                                "SUCCESS"
                        },
                        {
                                "VPN Session Update: Close",
                                "{'local_ip':'171.19.1.4','status':'CLOSED','hostname':'my-pc1','writebytes':1200211,'durationFieldName':null,'date_time_unix':1424700169626,'city':'Jerusalem','country':'Israel','session_id_field':'12345AAA','duration':24,'username':'John Dow','ip_field':'172.16.0.0','source_ip':'10.19.121.11','partition-1':'part-1','normalized_username':'John Dow','readbytesFieldName':null,'databucket':23,'totalbytes':null}",
                                "171.19.1.4",
                                "CLOSED"
                        },
                        {
                                "VPN Session Update: Close no session-id",
                                "{'local_ip':null,'status':'CLOSED','hostname':'my-pc1','writebytes':1200211,'durationFieldName':null,'date_time_unix':1424700169626,'city':'Jerusalem','country':'Israel','session_id_field':null,'duration':24,'username':'John Dow','ip_field':'172.16.0.0','source_ip':'10.19.121.11','partition-1':'part-1','normalized_username':'John Dow','readbytesFieldName':null,'databucket':23,'totalbytes':null}",
                                "171.19.1.16",
                                "CLOSED"
                        }
                }
        );
    }

}

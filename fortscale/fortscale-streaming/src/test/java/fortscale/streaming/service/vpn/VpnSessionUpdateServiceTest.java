package fortscale.streaming.service.vpn;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.reset;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

import net.minidev.json.JSONObject;

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

import fortscale.domain.schema.VpnEvents;
import fortscale.geoip.GeoIPInfo;
import fortscale.geoip.GeoIPService;
import fortscale.services.event.VpnService;
import fortscale.services.notifications.VpnGeoHoppingNotificationGenerator;
import fortscale.utils.junit.SpringAware;

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
    private GeoIPService geoIPServiceMock;
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
    String STATUS;

    public VpnSessionUpdateServiceTest(String TEST_CASE, String STATUS) {
        this.TEST_CASE = TEST_CASE;
        this.STATUS = STATUS;
    }

    @Test
    @Parameters(name = "Run test: {index} {1})")
    public void testSessionUpdate() throws UnknownHostException {
        //stubs:
//        when(geoIPServiceMock.getGeoIPInfo(anyString())).thenReturn(geoIPInfo);
        //init
        JSONObject event = new JSONObject();
        event.put(ipField, IP);
        event.put(partitionField, PARTITION);
        event.put(readbytesFieldName, READ_BYTES);
        event.put(durationFieldName, DURATUIN);
        event.put(totalbytesFieldName, TOTAL_BYTES);
        event.put(dateTimeUnixFieldName, new Date().getTime());
        event.put(statusFieldName, "CLOSED");
        event.put(cityFieldName, "Jerusalem");
        event.put(countryFieldName, "Israel");
        event.put(DATABUCKET_FIELD, 23);
        event.put(DURATION_FIELD, 24);
        event.put(usernameFieldName, "John Dow");
        event.put(normalizedUsernameFieldName, "John Dow");
        event.put(sessionIdFieldName, "123456AAA");
        hostnameFieldName = "hostname";
        event.put(hostnameFieldName, "my-pc1");
        localIpFieldName = "local_ip";
        event.put(localIpFieldName, "168.51.12.3");
        sourcepFieldName = "source_ip";
        event.put(sourcepFieldName, "171.19.1.4");
        totalbytesFieldName = "totalbytes";
        event.put(totalbytesFieldName, TOTAL_BYTES);
        writebytesFieldName = "writebytes";
        event.put(writebytesFieldName, 1200211L);

        //run test:
        event = vpnEnrichService.processSessionUpdate(event);

        //Validations
//        verify(geoIPServiceMock).getGeoIPInfo(IP);

        assertEquals(vpnEnrichService.getInputTopic(), inputTopic);
        assertEquals(vpnEnrichService.getOutputTopic(), outputTopic);
        assertEquals(vpnEnrichService.getPartitionKey(event), PARTITION);
        //assert session update fields
        reset(geoIPServiceMock);
    }

    @Parameters()
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]
                {
                        {
                                "VPN Session Update: Open & Close",
                                "OPEN"
                        },
                        {
                                "VPN Session Update: Open & Close",
                                "CLOSE"
                        }
                }
        );
    }

}

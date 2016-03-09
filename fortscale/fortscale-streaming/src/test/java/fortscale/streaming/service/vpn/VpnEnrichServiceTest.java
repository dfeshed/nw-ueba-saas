package fortscale.streaming.service.vpn;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
public class VpnEnrichServiceTest extends AbstractJUnit4SpringContextTests {
    //rules used to set JUnit parameters in SpringAware
    @ClassRule
    public static final SpringAware SPRING_AWARE = SpringAware.forClass(VpnEnrichServiceTest.class);
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
    private VpnService vpnService;
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
    private String countryFieldName = "country_field";
    private String countryIsoCodeFieldName = "country_code_field";
    private String regionFieldName = "region_field";
    private String cityFieldName = "city_field";
    private String ispFieldName = "isp_field";
    private String usageTypeFieldName = "usage_type_field";
    private String longtitudeFieldName = "longtitude_field";
    private String latitudeFieldName = "latitude_field";

    private static String IP = "172.16.0.0";
    private static String PARTITION = "part-1";

    //data buckets fields:
    private String totalbytesFieldName = "totalbytesFieldName";
    private String readbytesFieldName = "readbytesFieldName";
    private String durationFieldName = "durationFieldName";
    private String databucketFieldName = "databucketFieldName";

    //session update field:
//    private String

    GeoIPInfo geoIPInfo = new GeoIPInfo("");

    //Constructor setting:
    String TEST_CASE;
    Long READ_BYTES;
    Long TOTAL_BYTES;
    Long DURATUIN;
    Long DATA_BUCKET;

    public VpnEnrichServiceTest(String testCase, Long readBytes, Long totalBytes, Long duration, Long dataBucket) {
        this.TEST_CASE = testCase;
        this.READ_BYTES = readBytes;
        this.TOTAL_BYTES = totalBytes;
        this.DURATUIN = duration;
        this.DATA_BUCKET = dataBucket;
    }

    @Test
    @Parameters(name = "Run test: {index} {1})")
    public void testGeoLocation() throws UnknownHostException {
        //stubs:
        when(geoIPServiceMock.getGeoIPInfo(anyString())).thenReturn(geoIPInfo);
        //init
        JSONObject event = new JSONObject();
        event.put(ipField, IP);
        event.put(partitionField, PARTITION);
        event.put(readbytesFieldName, READ_BYTES);
        event.put(durationFieldName, DURATUIN);
        event.put(totalbytesFieldName, TOTAL_BYTES);
        event.put("date_time_unix", new Date().getTime());
        event.put("status", "CLOSED");

        //run test:
        event = vpnEnrichService.processVpnEvent(event, null);

        //Validations
        verify(geoIPServiceMock).getGeoIPInfo(IP);

      //  assertEquals(vpnEnrichService.getInputTopic(), inputTopic);
        assertEquals(vpnEnrichService.getOutputTopic(), outputTopic);
        assertEquals(vpnEnrichService.getPartitionKey(event), PARTITION);
        //assert geolocation fields
        assertEquals(event.get(ipField), IP);
        assertTrue(event.containsKey(countryFieldName));
        assertTrue(event.containsKey(countryIsoCodeFieldName));
        assertTrue(event.containsKey(regionFieldName));
        assertTrue(event.containsKey(cityFieldName));
        assertTrue(event.containsKey(ispFieldName));
        assertTrue(event.containsKey(usageTypeFieldName));
        //assertTrue(event.containsKey(longtitudeFieldName));
        //assertTrue(event.containsKey(latitudeFieldName));
        //assert data buckets fields:
        assertEquals(event.get(durationFieldName), DURATUIN);
        assertEquals(event.get(totalbytesFieldName), TOTAL_BYTES);
        assertEquals(event.get(readbytesFieldName), READ_BYTES);
        assertEquals(event.get(databucketFieldName), DATA_BUCKET);

        //reset the mock so it clears counters for the sake of next test
        reset(geoIPServiceMock);
    }

    @Parameterized.Parameters()
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]
                {
                        {
                                "VPN Data Bucket: Zero bucket",
                                4800L,
                                5000L,
                                124800L,
                                0L
                        },
                        {
                                "VPN Data Bucket: positive value",
                                480000L,
                                5000L,
                                124800L,
                                3L
                        },
                        {
                                "VPN Data Bucket: null read bytes",
                                null,
                                500000L,
                                124800L,
                                3L
                        },
                        {
                                "VPN Data Bucket: zero duration",
                                480000L,
                                500000L,
                                0L,
                                null
                        }
                }
        );
    }


}

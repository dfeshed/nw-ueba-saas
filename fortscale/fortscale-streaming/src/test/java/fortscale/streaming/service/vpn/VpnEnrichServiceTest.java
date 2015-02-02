package fortscale.streaming.service.vpn;

import fortscale.geoip.GeoIPInfo;
import fortscale.geoip.IpToLocationGeoIPService;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.net.UnknownHostException;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Created by rans on 02/02/15.
 */
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:META-INF/spring/vpn-enrich-context-test.xml")
//used to clean spring context for next class:
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class VpnEnrichServiceTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    VpnEnrichService vpnEnrichService;

    @Autowired
    @ReplaceWithMock
    IpToLocationGeoIPService geoIPServiceMock;

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

    GeoIPInfo geoIPInfo = new GeoIPInfo("");

    @Test
    public void testGeoLocation() throws UnknownHostException {
        //stubs:
        when(geoIPServiceMock.getGeoIPInfo(anyString())).thenReturn(geoIPInfo);
        //init
        JSONObject event = new JSONObject();
        event.put(ipField, IP);
        event.put(partitionField, PARTITION);

        //run test:
        event = vpnEnrichService.processGeolocation(event);

        //Validations
        verify(geoIPServiceMock).getGeoIPInfo(IP);

        assertEquals(vpnEnrichService.getInputTopic(), inputTopic);
        assertEquals(vpnEnrichService.getOutputTopic(), outputTopic);
        assertEquals(vpnEnrichService.getPartitionKey(event), PARTITION);
        assertTrue(event.containsKey(ipField));
        assertTrue(event.containsKey(countryFieldName));
        assertTrue(event.containsKey(countryIsoCodeFieldName));
        assertTrue(event.containsKey(regionFieldName));
        assertTrue(event.containsKey(cityFieldName));
        assertTrue(event.containsKey(ispFieldName));
        assertTrue(event.containsKey(longtitudeFieldName));
        assertTrue(event.containsKey(latitudeFieldName));

        //reset the mock so it clears counters for the sake of next test
        reset(geoIPServiceMock);
    }

    @Test
    public void testGeoLocationWithException() throws UnknownHostException {
        //stubs:
        when(geoIPServiceMock.getGeoIPInfo(anyString())).thenReturn(null);
        //init
        JSONObject event = new JSONObject();
        event.put(ipField, IP);
        event.put(partitionField, PARTITION);

        //run test:
        event = vpnEnrichService.processGeolocation(event);

        //Validations
        verify(geoIPServiceMock).getGeoIPInfo(IP);

        assertEquals(vpnEnrichService.getInputTopic(), inputTopic);
        assertEquals(vpnEnrichService.getOutputTopic(), outputTopic);
        assertEquals(vpnEnrichService.getPartitionKey(event), PARTITION);
        assertTrue(event.containsKey(ipField));
        assertTrue(!event.containsKey(countryFieldName));
        assertTrue(!event.containsKey(countryIsoCodeFieldName));
        assertTrue(!event.containsKey(regionFieldName));
        assertTrue(!event.containsKey(cityFieldName));
        assertTrue(!event.containsKey(ispFieldName));
        assertTrue(!event.containsKey(longtitudeFieldName));
        assertTrue(!event.containsKey(latitudeFieldName));

        //reset the mock so it clears counters for the sake of next test
        reset(geoIPServiceMock);
    }
}

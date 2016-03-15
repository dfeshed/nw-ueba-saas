package fortscale.streaming.service.vpn;


import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fortscale.services.UserSupportingInformationService;
import fortscale.services.impl.EvidencesServiceImpl;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import fortscale.domain.events.VpnSession;
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
    private String outputTopic = "output-1";

    private static String IP = "172.16.0.0";
    private static String PARTITION = "part-1";

    GeoIPInfo geoIPInfo = new GeoIPInfo("");

    //Constructor setting:
    String TEST_CASE;
    String EVENT;
    String UPDATED_LOCAL_IP;
    String UPDATED_SOURCE_IP;
    String STATUS;
    Long StartSessionTime1;
    Long StartSessionTime2;
    JSONObject event;
    String username;

    public VpnSessionUpdateServiceTest(String TEST_CASE, String eventObj, String updatedLocalIp, String updatedSourceIp, String status, Long StartSessionTime1, Long StartSessionTime2, String username) {
        this.TEST_CASE = TEST_CASE;
        this.EVENT = eventObj;
        this.UPDATED_LOCAL_IP = updatedLocalIp;
        this.UPDATED_SOURCE_IP = updatedSourceIp;
        this.STATUS = status;
        this.StartSessionTime1 = StartSessionTime1;
        this.StartSessionTime2 = StartSessionTime2;
        this.username = username;

    }



    @Test
    @Parameters(name = "Run test: {index} {1})")
    public void testSessionUpdate() throws UnknownHostException {
        //stubs:
        event = (JSONObject)JSONValue.parse(EVENT);
        Long startSessionTime = (Long)event.get("date_time_unix") - (Integer)event.get("duration") * 1000;
        when(vpnService.findOpenVpnSession(any(VpnSession.class))). thenAnswer(new Answer() {


            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {

                return getVpnSession(StartSessionTime1, StartSessionTime2, username);
            }
        });
        //init

        //run test:
        event = vpnEnrichService.processSessionUpdate(event, null);

        //Validations

        assertEquals(outputTopic, vpnEnrichService.getOutputTopic());
        assertEquals(PARTITION, vpnEnrichService.getPartitionKey(event));
        assertEquals(UPDATED_LOCAL_IP, event.get("local_ip"));
        assertEquals(UPDATED_SOURCE_IP, event.get("source_ip"));
        assertEquals(STATUS, event.get("status"));
        //assert session update fields
        reset(geoIPServiceMock);
    }

    /**
     * helper function to create stub list of 2 VPN open session events
     * @param StartSessionStartSearchTimeFrom
     * @param StartSessionStartSearchTimeTo
     * @param username
     * @return
     */
    private List<VpnSession> getVpnSessions(Long StartSessionStartSearchTimeFrom, Long StartSessionStartSearchTimeTo, String username) {
        List<VpnSession> vpnSessions = new ArrayList<VpnSession>();
        if (StartSessionStartSearchTimeFrom != null && StartSessionStartSearchTimeTo != null) {
            VpnSession vpnSession1 = new VpnSession();
            vpnSession1.setCreatedAtEpoch(StartSessionStartSearchTimeFrom);
            vpnSession1.setLocalIp("171.19.1.14");
            vpnSession1.setSourceIp("171.181.1.14");
            vpnSessions.add(vpnSession1);
            VpnSession vpnSession2 = new VpnSession();
            vpnSession2.setCreatedAtEpoch(StartSessionStartSearchTimeTo);
            vpnSession2.setLocalIp("171.19.1.16");
            vpnSession2.setSourceIp("171.181.1.16");
            vpnSessions.add(vpnSession2);
        }
        return vpnSessions;
    }

    private VpnSession getVpnSession(Long StartSessionStartSearchTimeFrom, Long StartSessionStartSearchTimeTo, String username) {
        if (StartSessionStartSearchTimeFrom != null && StartSessionStartSearchTimeTo != null) {
            VpnSession vpnSession1 = new VpnSession();
            vpnSession1.setCreatedAtEpoch(StartSessionStartSearchTimeFrom);
            vpnSession1.setLocalIp("171.19.1.14");
            vpnSession1.setSourceIp("171.181.1.14");
            return vpnSession1;
        }
        return null;
    }

    @Parameters()
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]
                {
                        {
                                "VPN Session Update: Open",
                                "{'local_ip':'171.19.1.4','status':'SUCCESS','hostname':'my-pc1','writebytes':1200211,'durationFieldName':null,'date_time_unix':1424700169626,'city':'Jerusalem','country':'Israel','session_id_field':'12345AAA','duration':24,'username':'John Dow','ip_field':'172.16.0.0','source_ip':'10.19.121.11','partition-1':'part-1','normalized_username':'John Dow','readbytesFieldName':null,'databucket':23,'totalbytes':null}",
                                "171.19.1.4", //expected local ip
                                "10.19.121.11",//expected source ip
                                "SUCCESS", //expected status
                                1424700115626L,
                                1424700175626L,
                                "John Dow" //expected username
                        },
                        {
                                "VPN Session Update: Close",
                                "{'local_ip':'171.19.1.4','status':'CLOSED','hostname':'my-pc1','writebytes':1200211,'durationFieldName':null,'date_time_unix':1424700169626,'city':'Jerusalem','country':'Israel','session_id_field':'12345AAA','duration':104,'username':'Martin K','ip_field':'172.16.0.0','source_ip':'10.19.121.11','partition-1':'part-1','normalized_username':'John Dow','readbytesFieldName':null,'databucket':23,'totalbytes':null}",
                                "171.19.1.4",
                                "171.181.1.14",
                                "CLOSED",
                                1424700115626L,
                                1424700175626L,
                                "Martin K"
                        },

                        {
                                "VPN Cisco ASA: Retrieve local_ip for close session from start session event",
                                "{'local_ip':null,'status':'CLOSED','hostname':'my-pc1','writebytes':1200211,'durationFieldName':null,'date_time_unix':1424700169626,'city':'Jerusalem','country':'Israel','session_id_field':null,'duration':24,'username':'Martin K','ip_field':'172.16.0.0','source_ip':'10.19.121.11','partition-1':'part-1','normalized_username':'John Dow','readbytesFieldName':null,'databucket':23,'totalbytes':null}",
                                (String)null,
                                "",
                                "CLOSED",
                                (Long)null,
                                (Long)null,
                                "Martin K"
                        }
                }
        );
    }

}

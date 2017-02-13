package fortscale.services.event.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import fortscale.domain.events.VpnSession;
import fortscale.domain.events.dao.VpnSessionRepository;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * Created by rans on 14/04/15.
 */
public class VpnServiceImpleTest {


    public static String OPEN_EVENT_1 = "{'local_ip':'171.19.1.4','hostname':'my-pc1','writebytes':1200211,'date_time_unix':1424700169626,'session_id_field':'12345AAA','city':'Jerusalem','country':'Israel','username':'John Dow','ip_field':'82.166.88.99','source_ip':'10.80.229.33','partition-1':'part-1'}";
    public static String CLOSE_EVENT_1 = "{'local_ip':'21.13.180.118','status':'Session disconnected','writebytes':1200211,'date_time_unix':1424700169626,'session_id_field':'12345AAA','duration':24,'username':'John Dow','ip_field':'82.166.88.99','source_ip':'10.80.229.33','partition-1':'part-1','normalized_username':'John Dow','readbytesFieldName':null,'databucket':23,'totalbytes':8700211,'closedAt':1424700169626}";
    public static String OPEN_EVENT_Cisco_ASA = "{'local_ip':'171.19.1.4','hostname':'my-pc1','writebytes':1200211,'date_time_unix':1424700169626,'city':'Jerusalem','country':'Israel','username':'John Dow','ip_field':'82.166.88.99','source_ip':'10.80.229.33','partition-1':'part-1','createdAt':1424701169626}";
    public static String OPEN_EVENT_Cisco_ASA2 = "{'local_ip':'171.19.1.4','hostname':'my-pc1','writebytes':1200211,'date_time_unix':1424700169626,'city':'Jerusalem','country':'Japan','username':'John Dow','ip_field':'82.166.88.88','source_ip':'10.80.229.52','partition-1':'part-1','createdAt':1424700269626}";
    public static String CLOSE_EVENT_Cisco_ASA = "{'local_ip':'21.13.180.118','status':'Session disconnected','writebytes':1200211,'date_time_unix':1424700169626,'duration':24,'username':'John Dow','ip_field':'172.16.0.0','source_ip':'10.19.121.11','city':'Boston','USA':'Israel','partition-1':'part-1','normalized_username':'John Dow','readbytesFieldName':null,'databucket':23,'closedAt':1424700169626}";
    public static String OPEN_EVENT_USER_IP = "{'local_ip':'171.19.1.4','hostname':'my-pc1','writebytes':1200211,'date_time_unix':1424700169626,'city':'Jerusalem','country':'Israel','username':'John Dow','ip_field':'82.166.88.99','source_ip':'10.80.229.33','partition-1':'part-1'}";
    public static String CLOSE_EVENT_USER_IP = "{'local_ip':'171.19.1.4','status':'Session disconnected','writebytes':1200211,'date_time_unix':1424700169626,'username':'John Dow','ip_field':'82.166.88.99','source_ip':'10.80.229.33','partition-1':'part-1','normalized_username':'John Dow','readbytesFieldName':null,'databucket':23}";


    @Mock
    private VpnSessionRepository vpnSessionRepository;

    @InjectMocks
    private VpnServiceImpl service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service.timeGapForResolveIpFrom = 30L;
        service.timeGapForResolveIpTo = 30L;
    }

    private VpnSession createSession(JSONObject event){
        VpnSession vpnSession = new VpnSession();
        vpnSession.setLocalIp((String) event.get("local_ip"));
        vpnSession.setHostname((String) event.get("hostname"));
        vpnSession.setWriteBytes(event.get("writebytes") != null ? ((Integer) event.get("writebytes")).longValue() : null);
        vpnSession.setCity((String) event.get("city"));
        vpnSession.setCountry((String) event.get("country"));
        vpnSession.setDuration((Integer) event.get("duration"));
        vpnSession.setSessionId((String) event.get("session_id_field"));
        vpnSession.setSourceIp((String) event.get("source_ip"));
        vpnSession.setDataBucket((Integer) event.get("databucket"));
        vpnSession.setTotalBytes(event.get("totalbytes") != null ?  ((Integer)event.get("totalbytes")).longValue() : null);
        vpnSession.setUsername((String) event.get("username"));
        Long closedAt = (Long)event.get("closedAt");
        if (closedAt != null) {
            vpnSession.setClosedAtEpoch(closedAt);
            vpnSession.setClosedAt(new DateTime(closedAt));
        }
        Long createdAt = (Long)event.get("createdAt");
        if (createdAt != null) {
            vpnSession.setCreatedAtEpoch(createdAt);
            vpnSession.setCreatedAt(new DateTime(createdAt));
        }

        return vpnSession;
    }

    @Test
    public void findOpenVpnSession_bySessionId(){
        System.out.println("findOpenVpnSession_bySessionId");
        JSONObject openEvent = (JSONObject) JSONValue.parse(OPEN_EVENT_1);
        JSONObject closeEvent = (JSONObject) JSONValue.parse(CLOSE_EVENT_1);

        VpnSession vpnOpenSession = createSession(openEvent);
        VpnSession vpnCloseSession = createSession(closeEvent);

        when(vpnSessionRepository.findBySessionId(anyString())).thenReturn(vpnOpenSession);

        VpnSession result =  service.findOpenVpnSession(vpnCloseSession);
        assertEquals("my-pc1", (String) result.getHostname());
        assertEquals("John Dow", (String) result.getUsername());
    }
    @Test
    public void findOpenVpnSession_byUserAndIP(){
        System.out.println("findOpenVpnSession_byUserAndIP");
        JSONObject openEvent = (JSONObject) JSONValue.parse(OPEN_EVENT_USER_IP);
        JSONObject closeEvent = (JSONObject) JSONValue.parse(CLOSE_EVENT_USER_IP);

        VpnSession vpnOpenSession = createSession(openEvent);
        VpnSession vpnCloseSession = createSession(closeEvent);
        List<VpnSession> vpnSessions = new ArrayList<VpnSession>();
        vpnSessions.add(vpnOpenSession);

        when(vpnSessionRepository.findByUsernameAndSourceIp(anyString(), anyString(),any(PageRequest.class))).thenReturn(vpnSessions);

        VpnSession result =  service.findOpenVpnSession(vpnCloseSession);
        assertEquals("my-pc1", (String) result.getHostname());
        assertEquals("John Dow", (String) result.getUsername());
        assertEquals("10.80.229.33", (String) result.getSourceIp());
        assertEquals("Israel", (String) result.getCountry());
    }
    @Test
    public void findOpenVpnSession_byUser(){
        System.out.println("findOpenVpnSession_byUser");
        JSONObject openEvent = (JSONObject) JSONValue.parse(OPEN_EVENT_Cisco_ASA);
        JSONObject closeEvent = (JSONObject) JSONValue.parse(CLOSE_EVENT_Cisco_ASA);

        VpnSession vpnOpenSession = createSession(openEvent);
        VpnSession vpnCloseSession = createSession(closeEvent);
        List<VpnSession> vpnSessions = new ArrayList<VpnSession>();
        vpnSessions.add(vpnOpenSession);

        when(vpnSessionRepository.findByUsernameAndCreatedAtEpochBetween(anyString(), anyLong(), anyLong(), any(PageRequest.class))).thenReturn(vpnSessions);

        VpnSession result =  service.findOpenVpnSession(vpnCloseSession);
        assertEquals("my-pc1", (String) result.getHostname());
        assertEquals("John Dow", (String) result.getUsername());
        assertEquals("10.80.229.33", (String) result.getSourceIp());
        assertEquals("Israel", (String) result.getCountry());
    }
    @Test
    public void findOpenVpnSession_byUser_withMoreThanOneOpenSession(){
        System.out.println("findOpenVpnSession_byUser_withMoreThanOneOpenSession");
        JSONObject openEvent = (JSONObject) JSONValue.parse(OPEN_EVENT_Cisco_ASA);
        JSONObject openEvent2 = (JSONObject) JSONValue.parse(OPEN_EVENT_Cisco_ASA2);
        JSONObject closeEvent = (JSONObject) JSONValue.parse(CLOSE_EVENT_Cisco_ASA);

        VpnSession vpnOpenSession = createSession(openEvent);
        VpnSession vpnOpenSession2 = createSession(openEvent2);
        VpnSession vpnCloseSession = createSession(closeEvent);
        List<VpnSession> vpnSessions = new ArrayList<VpnSession>();
        vpnSessions.add(vpnOpenSession);
        vpnSessions.add(vpnOpenSession2);

        when(vpnSessionRepository.findByUsernameAndCreatedAtEpochBetween(anyString(), anyLong(), anyLong(), any(PageRequest.class))).thenReturn(vpnSessions);

        VpnSession result =  service.findOpenVpnSession(vpnCloseSession);
        assertEquals("my-pc1", (String) result.getHostname());
        assertEquals("John Dow", (String) result.getUsername());
        assertEquals("10.80.229.52", (String) result.getSourceIp());
        assertEquals("Japan", (String) result.getCountry());
    }

    @Test
    // The flag for creating vpn session when received only closed session is false
    public void updateCloseVpnSession_WithOpenSession(){
        JSONObject openEvent = (JSONObject) JSONValue.parse(OPEN_EVENT_1);
        JSONObject closeEvent = (JSONObject) JSONValue.parse(CLOSE_EVENT_1);

        VpnSession vpnOpenSession = createSession(openEvent);
        VpnSession vpnCloseSession = createSession(closeEvent);

        when(vpnSessionRepository.findBySessionId(anyString())).thenReturn(vpnOpenSession);

        VpnSession result = service.updateCloseVpnSession(vpnCloseSession, false);
        assertEquals(1424700169626L, result.getClosedAt().getMillis());
        assertEquals((Integer)23, result.getDataBucket());
        assertEquals((Integer)24, result.getDuration());
        assertEquals((Long)1200211L, result.getWriteBytes());
        assertEquals((Long)8700211L, result.getTotalBytes());
    }

    @Test
    // The flag for creating vpn session when received only closed session is false
    public void updateCloseVpnSession_NoOpenSession(){
        JSONObject closeEvent = (JSONObject) JSONValue.parse(CLOSE_EVENT_1);

        VpnSession vpnCloseSession = createSession(closeEvent);

        when(vpnSessionRepository.findBySessionId(anyString())).thenReturn(null);

        VpnSession result = service.updateCloseVpnSession(vpnCloseSession, false);
        assertNull(result);
    }


    @Test
    // The flag for creating vpn session when received only closed session is true
    public void updateCloseVpnSession_CreateVpnSessionWithNoOpenSession(){
        JSONObject closeEvent = (JSONObject) JSONValue.parse(CLOSE_EVENT_Cisco_ASA);

        VpnSession vpnCloseSession = createSession(closeEvent);

        when(vpnSessionRepository.findBySessionId(anyString())).thenReturn(null);
        DateTime startTime = vpnCloseSession.getClosedAt().minus(Duration.standardSeconds(vpnCloseSession.getDuration()));

        VpnSession result = service.updateCloseVpnSession(vpnCloseSession, true);
        assertEquals(1424700169626L, result.getClosedAt().getMillis());
        assertEquals((Integer)23, result.getDataBucket());
        assertEquals((Integer)24, result.getDuration());
        assertEquals((Long)1200211L, result.getWriteBytes());
        assertEquals(startTime, result.getCreatedAt());
        assertEquals(Long.valueOf(startTime.getMillis() / 1000), result.getCreatedAtEpoch());
    }

    @Test
    // The flag for creating vpn session when received only closed session is true
    public void updateCloseVpnSession_CreateVpnSessionWithNoOpenSession_NoDuration(){
        System.out.println("updateCloseVpnSession");
        JSONObject closeEvent = (JSONObject) JSONValue.parse(CLOSE_EVENT_Cisco_ASA);

        VpnSession vpnCloseSession = createSession(closeEvent);
        vpnCloseSession.setDuration(null);

        when(vpnSessionRepository.findBySessionId(anyString())).thenReturn(null);

        VpnSession result = service.updateCloseVpnSession(vpnCloseSession, true);
        Assert.assertNull(result);
    }

    // Test the function getGeoHoppingVpnSessions when we have regular geo hoping
    // We get china -> greece -> qatar and want to mark geo hopping on greece&qatar
    @Test
    public void getGeoHoppingVpnSessions_basicGeoHopping(){

        VpnSession chinaSession = createVpnSession(DateTime.now().minus(Duration.standardMinutes(30)), DateTime.now(), "China", "China");
        VpnSession greeceSession = createVpnSession(DateTime.now().minus(Duration.standardMinutes(30)), DateTime.now(), "Greece", "Greece");
        VpnSession qatarSession = createVpnSession(DateTime.now().minus(Duration.standardMinutes(30)), DateTime.now(), "Qatar", "Qatar");

        List<VpnSession> mongoResponse = new ArrayList<>();
        mongoResponse.add(greeceSession);
        mongoResponse.add(chinaSession);

        service.afterPropertiesSet();

        when(vpnSessionRepository.findByUsernameAndCreatedAtEpochGreaterThan(anyString(), anyLong(), any(PageRequest.class))).thenReturn(mongoResponse);

        List<VpnSession> geoHoppingVpnSessions = service.getGeoHoppingVpnSessions(qatarSession, greeceSession.getCountry(), 1, 6);

        Assert.assertNotNull(geoHoppingVpnSessions);
        Assert.assertEquals(1, geoHoppingVpnSessions.size());
        Assert.assertEquals(greeceSession, geoHoppingVpnSessions.get(0));
    }

    // Test the function getGeoHoppingVpnSessions when we have regular geo hoping
    // We get china -> greece -> qatar and want to mark geo hopping on greece&qatar where the greece session has no closed at value
    @Test
    public void getGeoHoppingVpnSessions_basicGeoHoppingWithClosedAtNull(){

        VpnSession chinaSession = createVpnSession(DateTime.now().minus(Duration.standardMinutes(30)), DateTime.now(), "China", "China");
        VpnSession greeceSession = createVpnSession(DateTime.now().minus(Duration.standardMinutes(30)), null, "Greece", "Greece");
        VpnSession qatarSession = createVpnSession(DateTime.now().minus(Duration.standardMinutes(30)), DateTime.now(), "Qatar", "Qatar");

        List<VpnSession> mongoResponse = new ArrayList<>();
        mongoResponse.add(greeceSession);
        mongoResponse.add(chinaSession);

        service.afterPropertiesSet();

        when(vpnSessionRepository.findByUsernameAndCreatedAtEpochGreaterThan(anyString(), anyLong(), any(PageRequest.class))).thenReturn(mongoResponse);

        List<VpnSession> geoHoppingVpnSessions = service.getGeoHoppingVpnSessions(qatarSession, chinaSession.getCountry(), 1, 6);

        Assert.assertNotNull(geoHoppingVpnSessions);
        Assert.assertEquals(1, geoHoppingVpnSessions.size());
        Assert.assertEquals(greeceSession, geoHoppingVpnSessions.get(0));
    }

    // Test the function getGeoHoppingVpnSessions when we have regular geo hoping
    // We get china -> greece -> qatar created at more then 6 hours apart
    @Test
    public void getGeoHoppingVpnSessions_noGeoHopping_createdAtDidntMatch(){

        VpnSession greeceSession = createVpnSession(DateTime.now().minus(Duration.standardHours(7)), DateTime.now(), "Greece", "Greece");
        VpnSession qatarSession = createVpnSession(DateTime.now().minus(Duration.standardMinutes(10)), DateTime.now(), "Qatar", "Qatar");

        // No response because the greece session is from more then 6 hours
        List<VpnSession> mongoResponse = new ArrayList<>();

        service.afterPropertiesSet();

        when(vpnSessionRepository.findByUsernameAndCreatedAtEpochGreaterThan(anyString(), anyLong(), any(PageRequest.class))).thenReturn(mongoResponse);

        List<VpnSession> geoHoppingVpnSessions = service.getGeoHoppingVpnSessions(qatarSession, greeceSession.getCountry(), 1, 6);

        Assert.assertNotNull(geoHoppingVpnSessions);
        Assert.assertEquals(0, geoHoppingVpnSessions.size());
    }

    // Test the function getGeoHoppingVpnSessions when we have regular geo hoping
    // We get china -> greece -> qatar closed at more then 1 hours apart
    @Test
    public void getGeoHoppingVpnSessions_noGeoHopping_closedAtDidntMatch(){

        VpnSession chinaSession = createVpnSession(DateTime.now().minus(Duration.standardMinutes(40)), DateTime.now().minus(Duration.standardMinutes(75)), "China", "China");
        VpnSession greeceSession = createVpnSession(DateTime.now().minus(Duration.standardMinutes(30)), DateTime.now().minus(Duration.standardMinutes(70)), "Greece", "Greece");
        VpnSession qatarSession = createVpnSession(DateTime.now(), DateTime.now(), "Qatar", "Qatar");

        List<VpnSession> mongoResponse = new ArrayList<>();
        mongoResponse.add(greeceSession);
        mongoResponse.add(chinaSession);

        service.afterPropertiesSet();

        when(vpnSessionRepository.findByUsernameAndCreatedAtEpochGreaterThan(anyString(), anyLong(), any(PageRequest.class))).thenReturn(mongoResponse);

        List<VpnSession> geoHoppingVpnSessions = service.getGeoHoppingVpnSessions(qatarSession, greeceSession.getCountry(), 1, 6);

        Assert.assertNotNull(geoHoppingVpnSessions);
        Assert.assertEquals(0, geoHoppingVpnSessions.size());
    }


    // Test the function getGeoHoppingVpnSessions when we have regular geo hoping
    // We get china -> greece -> qatar when later got holland session that wasn't expected and is located
    // between greece and qatar on the time line
    @Test
    public void getGeoHoppingVpnSessions_WithUnexpectedSession(){

        VpnSession chinaSession = createVpnSession(DateTime.now().minus(Duration.standardHours(5)), DateTime.now().minus(Duration.standardHours(4)), "China", "China");
        VpnSession greeceSession = createVpnSession(DateTime.now().minus(Duration.standardHours(4)), DateTime.now().minus(Duration.standardHours(3)), "Greece", "Greece");
        VpnSession hollandSession = createVpnSession(DateTime.now().minus(Duration.standardHours(3)), DateTime.now(), "Holland", "Holland");
        VpnSession qatarSession = createVpnSession(DateTime.now().minus(Duration.standardMinutes(30)), null, "Qatar", "Qatar");

        List<VpnSession> mongoResponse = new ArrayList<>();
        mongoResponse.add(hollandSession);
        mongoResponse.add(greeceSession);
        mongoResponse.add(chinaSession);

        service.afterPropertiesSet();

        when(vpnSessionRepository.findByUsernameAndCreatedAtEpochGreaterThan(anyString(), anyLong(), any(PageRequest.class))).thenReturn(mongoResponse);

        List<VpnSession> geoHoppingVpnSessions = service.getGeoHoppingVpnSessions(qatarSession, greeceSession.getCountry(), 1, 6);

        Assert.assertNotNull(geoHoppingVpnSessions);
        Assert.assertEquals(1, geoHoppingVpnSessions.size());
        Assert.assertEquals(hollandSession, geoHoppingVpnSessions.get(0));
    }

    // Test the function getGeoHoppingVpnSessions when we have regular geo hoping
    // We get china -> greece -> qatar when later got qatar session that wasn't expected and is located
    // between greece and qatar on the time line
    @Test
    public void getGeoHoppingVpnSessions_WithUnexpectedSessionEqualsTheCurr(){

        VpnSession chinaSession = createVpnSession(DateTime.now().minus(Duration.standardHours(5)), DateTime.now().minus(Duration.standardHours(4)), "China", "China");
        VpnSession greeceSession = createVpnSession(DateTime.now().minus(Duration.standardHours(4)), DateTime.now(), "Greece", "Greece");
        VpnSession unexpectedQatar = createVpnSession(DateTime.now().minus(Duration.standardHours(3)), DateTime.now(), "Qatar", "Qatar");
        VpnSession qatarSession = createVpnSession(DateTime.now().minus(Duration.standardMinutes(30)), null, "Qatar", "Qatar");

        List<VpnSession> mongoResponse = new ArrayList<>();
        mongoResponse.add(unexpectedQatar);
        mongoResponse.add(greeceSession);
        mongoResponse.add(chinaSession);

        service.afterPropertiesSet();

        when(vpnSessionRepository.findByUsernameAndCreatedAtEpochGreaterThan(anyString(), anyLong(), any(PageRequest.class))).thenReturn(mongoResponse);

        List<VpnSession> geoHoppingVpnSessions = service.getGeoHoppingVpnSessions(qatarSession, greeceSession.getCountry(), 1, 6);

        Assert.assertNotNull(geoHoppingVpnSessions);
        Assert.assertEquals(1, geoHoppingVpnSessions.size());
        Assert.assertEquals(greeceSession, geoHoppingVpnSessions.get(0));
    }

    // Test the function getGeoHoppingVpnSessions when we have regular geo hoping
    // We get china -> greece -> qatar when later got greece session that wasn't expected and is located
    // between greece and qatar on the time line
    @Test
    public void getGeoHoppingVpnSessions_WithUnexpectedSessionEqualsThePrev(){

        VpnSession chinaSession = createVpnSession(DateTime.now().minus(Duration.standardHours(5)), DateTime.now().minus(Duration.standardHours(4)), "China", "China");
        VpnSession greeceSession = createVpnSession(DateTime.now().minus(Duration.standardHours(4)), DateTime.now().minus(Duration.standardHours(3)), "Greece", "Greece");
        VpnSession unexpectedGreece = createVpnSession(DateTime.now().minus(Duration.standardHours(3)), DateTime.now(), "Greece", "Greece");
        VpnSession qatarSession = createVpnSession(DateTime.now().minus(Duration.standardMinutes(30)), null, "Qatar", "Qatar");

        List<VpnSession> mongoResponse = new ArrayList<>();
        mongoResponse.add(unexpectedGreece);
        mongoResponse.add(greeceSession);
        mongoResponse.add(chinaSession);

        service.afterPropertiesSet();

        when(vpnSessionRepository.findByUsernameAndCreatedAtEpochGreaterThan(anyString(), anyLong(), any(PageRequest.class))).thenReturn(mongoResponse);

        List<VpnSession> geoHoppingVpnSessions = service.getGeoHoppingVpnSessions(qatarSession, greeceSession.getCountry(), 1, 6);

        Assert.assertNotNull(geoHoppingVpnSessions);
        Assert.assertEquals(1, geoHoppingVpnSessions.size());
        Assert.assertEquals(unexpectedGreece, geoHoppingVpnSessions.get(0));
    }

    private VpnSession createVpnSession(DateTime createdAt, DateTime closedAt, String country, String city){
        VpnSession vpnSession = new VpnSession();
        vpnSession.setCreatedAt(createdAt);
        vpnSession.setClosedAtEpoch(createdAt.getMillis());
        if (closedAt != null) {
            vpnSession.setClosedAt(closedAt);
            vpnSession.setClosedAtEpoch(closedAt.getMillis());
        }
        vpnSession.setCountry(country);
        vpnSession.setCity(city);

        return vpnSession;
    }
}

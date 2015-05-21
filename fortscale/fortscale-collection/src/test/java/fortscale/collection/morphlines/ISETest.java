package fortscale.collection.morphlines;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Date;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class ISETest {

    private MorphlinesTester morphlineTester = new MorphlinesTester();
    private String confFile = "resources/conf-files/parseISE.conf";
    private String[] iseOutputFields = new String[]{"event_code", "timestampepoch", "hostname", "ipaddress", "macAddress"};

    @Before
    public void setUp() throws Exception {
        morphlineTester.init(new String[]{confFile}, Arrays.asList(iseOutputFields));
    }

    @After
    public void tearDown() throws Exception {
        morphlineTester.close();
    }

    @Test
    @Parameters
    public void testIseSingleLines(String testCase, String inputLine, String expectedOutput) {
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }


    @SuppressWarnings("unused")
    private Object[] parametersForTestIseSingleLines() {
        return $(
                $(
                        "Regular 3000 event",
                        "2015-05-07T00:00:07+00:00 isemtv-prd-09 CISE_RADIUS_Accounting 0009659552 1 0 2015-05-07 00:00:07.937 +00:00 0348597681 3000 NOTICE Radius-Accounting: RADIUS Accounting start request, ConfigVersionId=21, Device IP Address=171.68.33.133, RequestLatency=1, NetworkDeviceName=mtv5-mda2-dt-sw1, User-Name=CISCO\\\\\\\\sahota, NAS-IP-Address=171.68.33.133, NAS-Port=50136, Service-Type=Framed, Framed-IP-Address=171.68.33.159, Class=CACS:AB44218500003C86633F00A3:isemtv-prd-09/218277192/12720217, Called-Station-ID=30-F7-0D-D8-3D-A4, Calling-Station-ID=54-EE-75-49-B0-FF, Acct-Status-Type=Start, Acct-Delay-Time=0, Acct-Session-Id=00004404, Acct-Authentic=RADIUS, NAS-Port-Type=Ethernet, NAS-Port-Id=GigabitEthernet1/0/36, undefined-151=B5B585F5, cisco-av-pair=audit-session-id=AB44218500003C86633F00A3, cisco-av-pair=connect-progress=Call Up, cisco-av-pair=dhcp-option=dhcp-parameter-request-list=1\\\\, 15\\\\, 3\\\\, 6\\\\, 44\\\\, 46\\\\, 47\\\\, 31\\\\, 33\\\\, 121\\\\, 249\\\\, 43, cisco-av-pair=dhcp-option=dhcp-class-identifier=MSFT 5.0, cisco-av-pair=dhcp-option=client-fqdn=0\\\\, 0\\\\, 83\\\\, 65\\\\, 72\\\\, 79\\\\, 84\\\\, 45\\\\, 70\\\\, 76\\\\, 86\\\\, 54\\\\, 99\\\\, 105\\\\, 115\\\\, 111\\\\, 109, cisco-av-pair=dhcp-option=host-name=SAHOTA-FLVL6, cisco-av-pair=dhcp-option=dhcp-requested-address=171.68.33.159, cisco-av-pair=dhcp-option=dhcp-client-identifier=01:54:ee:75:49:b0:ff, AcsSessionID=isemtv-prd-09/218277192/12720278, SelectedAccessService=802.1x, Step=11004, Step=11017, Step=15049, Step=15008, Step=15004, Step=11005, NetworkDeviceGroups=Location#All Locations#US West#UNITED STATES#MOUNTAIN VIEW#MTV5, NetworkDeviceGroups=Device Type#All Device Types#C3750, CPMSessionID=AB44218500003C86633F00A3, AllowedProtocolMatchedRule=Wired Dot1X, Location=Location#All Locations#US West#UNITED STATES#MOUNTAIN VIEW#MTV5, Device Type=Device Type#All Device Types#C3750,",
                        "3000,1430956807,SAHOTA-FLVL6,171.68.33.159,30-F7-0D-D8-3D-A4"
                ),
                $(
                        "3000 event without IP",
                        "2015-05-07T01:12:03+00:00 isehkg-prd-05 CISE_RADIUS_Accounting 0006373305 1 0 2015-05-07 01:12:03.349 +00:00 0250092618 3000 NOTICE Radius-Accounting: RADIUS Accounting start request, ConfigVersionId=144, Device IP Address=10.75.17.36, RequestLatency=2, NetworkDeviceName=shn15-wl-wlc1, User-Name=hamao, NAS-IP-Address=10.75.17.36, NAS-Port=13, Class=CACS:0a4b11240005bfad554abbe2:isehkg-prd-05/216703841/8043711, Called-Station-ID=00-07-7d-0a-f3-a0, Calling-Station-ID=f0-24-75-77-1e-bd, NAS-Identifier=shn15-wl-wlc1, Acct-Status-Type=Start, Acct-Session-Id=554abbe2/f0:24:75:77:1e:bd/297515, Acct-Authentic=RADIUS, Event-Timestamp=1430961122, NAS-Port-Type=Wireless - IEEE 802.11, Tunnel-Type=(tag=0) VLAN, Tunnel-Medium-Type=(tag=0) 802, Tunnel-Private-Group-ID=(tag=0) 258, cisco-av-pair=audit-session-id=0a4b11240005bfad554abbe2, Airespace-Wlan-Id=1, AcsSessionID=isehkg-prd-05/216703841/8043717, SelectedAccessService=802.1x, Step=11004, Step=11017, Step=15049, Step=15008, Step=15004, Step=11005, NetworkDeviceGroups=Location#All Locations#APJC#CHINA#SHANGHAI#SHN15, NetworkDeviceGroups=Device Type#All Device Types#WLC, Service-Type=Framed, CPMSessionID=0a4b11240005bfad554abbe2, AllowedProtocolMatchedRule=Wireless Dot1X, Location=Location#All Locations#APJC#CHINA#SHANGHAI#SHN15, Device Type=Device Type#All Device Types#WLC, ",
                        null
                ),
                $(
                        "Regular 3001 event",
                        "2015-05-07T01:00:47+00:00 isemtv-prd-08 CISE_RADIUS_Accounting 0006119038 1 0 2015-05-07 01:00:47.389 +00:00 0210063714 3001 NOTICE Radius-Accounting: RADIUS Accounting stop request, ConfigVersionId=22, Device IP Address=171.68.220.4, RequestLatency=1, NetworkDeviceName=syc02-wl-wlc1, User-Name=davidga2, NAS-IP-Address=171.68.220.4, NAS-Port=13, Framed-IP-Address=171.68.218.35, Class=CACS:ab44dc040001defb554aa7b4:isemtv-prd-08/218255337/10732603, Called-Station-ID=6c-20-56-6c-40-a0, Calling-Station-ID=84-38-38-c5-54-68, NAS-Identifier=syc02-wl-wlc1, Acct-Status-Type=Stop, Acct-Delay-Time=0, Acct-Input-Octets=2233372, Acct-Output-Octets=25451436, Acct-Session-Id=554aa7b4/84:38:38:c5:54:68/30600, Acct-Authentic=RADIUS, Acct-Session-Time=4491, Acct-Input-Packets=9666, Acct-Output-Packets=20629, Acct-Terminate-Cause=User Request, undefined-52=\\000\\000\\000\\000, undefined-53=\\000\\000\\000\\000, Event-Timestamp=1430960447, NAS-Port-Type=Wireless - IEEE 802.11, Tunnel-Type=(tag=0) VLAN, Tunnel-Medium-Type=(tag=0) 802, Tunnel-Private-Group-ID=(tag=0) 250, undefined-97=\\000@\\xFE\\x80\\000\\000\\000\\000\\000\\000\\000\\000\\000\\000\\000\\000\\000\\000, cisco-av-pair=audit-session-id=ab44dc040001defb554aa7b4, cisco-av-pair=dhcp-option=host-name=android-ea27644604e9d444, cisco-av-pair=dhcp-option=dhcp-class-identifier=dhcpcd-5.5.6, Airespace-Wlan-Id=1, AcsSessionID=isemtv-prd-08/218255337/10795055, SelectedAccessService=802.1x, Step=11004, Step=11017, Step=15049, Step=15008, Step=15004, Step=11005, NetworkDeviceGroups=Location#All Locations#US West#UNITED STATES#MILPITAS#SJCSYC02, NetworkDeviceGroups=Device Type#All Device Types#WLC, Service-Type=Framed, CPMSessionID=ab44dc040001defb554aa7b4, AllowedProtocolMatchedRule=Wireless Dot1X, Location=Location#All Locations#US West#UNITED STATES#MILPITAS#SJCSYC02, Device Type=Device Type#All Device Types#WLC,",
                        "3001,1430960447,android-ea27644604e9d444,171.68.218.35,6c-20-56-6c-40-a0"
                ),
                $(
                        "3001 event without IP",
                        "2015-05-07T01:12:02+00:00 iseallne-prd-06 CISE_RADIUS_Accounting 0001545961 1 0 2015-05-07 01:12:02.220 +00:00 0076481111 3001 NOTICE Radius-Accounting: RADIUS Accounting stop request, ConfigVersionId=144, Device IP Address=10.135.10.4, RequestLatency=3, NetworkDeviceName=atl-sw1, NAS-IP-Address=10.135.10.4, NAS-Port=50501, Service-Type=Framed, Called-Station-ID=C0-67-AF-E3-63-C0, Calling-Station-ID=78-4B-87-E1-57-F7, Acct-Status-Type=Stop, Acct-Delay-Time=0, Acct-Input-Octets=37524031, Acct-Output-Octets=2196664552, Acct-Session-Id=0000859D, Acct-Authentic=Local, Acct-Session-Time=43218, Acct-Input-Packets=45190894, Acct-Output-Packets=36707472, Acct-Terminate-Cause=Idle Timeout, NAS-Port-Type=Ethernet, NAS-Port-Id=TenGigabitEthernet5/1, undefined-151=0C74AA21, cisco-av-pair=audit-session-id=0A66840600008206766B9384, cisco-av-pair=disc-cause-ext=No Reason, cisco-av-pair=connect-progress=Call Up, AcsSessionID=iseallne-prd-06/216698891/4132168, SelectedAccessService=802.1x, Step=11004, Step=11017, Step=15049, Step=15008, Step=15048, Step=15048, Step=15048, Step=15048, Step=15048, Step=15048, Step=15048, Step=15048, Step=15048, Step=15004, Step=11005, NetworkDeviceGroups=Location#All Locations#US East#UNITED STATES#ATLANTA#ATL, NetworkDeviceGroups=Device Type#All Device Types#C6K, CPMSessionID=0A66840600008206766B9384, AllowedProtocolMatchedRule=Wired Dot1X, Location=Location#All Locations#US East#UNITED STATES#ATLANTA#ATL, Device Type=Device Type#All Device Types#C6K, ",
                        null
                ),
                $(
                        "Event number 5200 - need to be dropped",
                        "2015-05-07T01:14:56+00:00 isetyo-prd-04 CISE_RADIUS_Accounting 0000949245 2 1  SelectedAccessService=802.1x, Step=11004, Step=11017, Step=15049, Step=15008, Step=15004, Step=11005, NetworkDeviceGroups=Location#All Locations#APJC#JAPAN#TOKYO#TKY7, NetworkDeviceGroups=Device Type#All Device Types#WLC, Service-Type=Framed, CPMSessionID=4068360e00008209554abc5f, AllowedProtocolMatchedRule=Wireless Dot1X, Location=Location#All Locations#APJC#JAPAN#TOKYO#TKY7, Device Type=Device Type#All Device Types#WLC,\",",
                        null
                ),
                $(
                        "No event number - need to be dropped",
                        "2015-05-07T01:14:56+00:00 isetyo-prd-04 CISE_RADIUS_Accounting 0000949245 2 1  SelectedAccessService=802.1x, Step=11004, Step=11017, Step=15049, Step=15008, Step=15004, Step=11005, NetworkDeviceGroups=Location#All Locations#APJC#JAPAN#TOKYO#TKY7, NetworkDeviceGroups=Device Type#All Device Types#WLC, Service-Type=Framed, CPMSessionID=4068360e00008209554abc5f, AllowedProtocolMatchedRule=Wireless Dot1X, Location=Location#All Locations#APJC#JAPAN#TOKYO#TKY7, Device Type=Device Type#All Device Types#WLC,",
                        null
                )
        );
    }

}
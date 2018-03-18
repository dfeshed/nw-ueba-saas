package fortscale.temp;


import fortscale.domain.core.Alert;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.Tag;
import fortscale.domain.core.User;

import java.util.*;

/**
 * Created by shays on 06/07/2017.
 */
public class MockScenarioGenerator {

    public static List<User> generateMocksUsersList(){
        List<User> users =  Arrays.asList(
                new UsersMockBuilder(1).createInstance(),
                new UsersMockBuilder(2).createInstance(),
                new UsersMockBuilder(3).createInstance(),
                new UsersMockBuilder(4).createInstance()
        );
        return users;
    }

    public static Set<User> generateMocksUsersSet(){
        return new HashSet<>(generateMocksUsersList());
    }

    public static User generateMocksUser(){
        return generateMocksUsersList().get(0);
    }

    public static List<Alert> generateMocksAlertsList(){
        List<Alert> alerts =  Arrays.asList(
                new AlertMockBuilder(1).createInstance(),
                new AlertMockBuilder(2).createInstance(),
                new AlertMockBuilder(3).createInstance(),
                new AlertMockBuilder(4).createInstance()
        );
        return alerts;
    }

    public static Set<Alert> generateMocksAlertsSet(){
        return new HashSet<>(generateMocksAlertsList());
    }

    public static Alert generateMocksAlert(){
        return generateMocksAlertsList().get(0);
    }

    public static List<Tag> generateMocksTagList(){
        List<Tag> tags =  Arrays.asList(
                new TagsMockBuilder(1).setName("admin").createInstance()
        );
        return tags;
    }

    public static Set<Tag> generateMocksTagSet(){
        return new HashSet(generateMocksTagList());
    }

    public static Tag generateMocksTag(){
        return  generateMocksTagList().get(0);
    }

    public static List<Evidence> generateMocksEvidenceList(){
        List<Evidence> Evidences =  Arrays.asList(
                new EvidenceMockBuilder(1).createInstance(),
                new EvidenceMockBuilder(2).createInstance(),
                new EvidenceMockBuilder(3).createInstance(),
                new EvidenceMockBuilder(4).createInstance()
        );
        return Evidences;
    }

    public static Set<Evidence> generateMocksEvideceSet(){
        return new HashSet(generateMocksEvidenceList());
    }

    public static Evidence generateMocksEvidence(){
        return  generateMocksEvidenceList().get(0);
    }

    public static Map<String, Object> getMockKerberosEvent() {
        Map<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("source_machine_type","Desktop");
        fieldsMap.put("destination_machine_type","Server");
        fieldsMap.put("failure_code","0x22");
        fieldsMap.put("failure_code_score",85);
        fieldsMap.put("normalized_src_machine_score",0);
        fieldsMap.put("destination_machine","SECUSR16_SRV");
        fieldsMap.put("normalized_dst_machine","secusr16_SRV");
        fieldsMap.put("normalized_dst_machine_score",0);

        fieldsMap.put("is_from_vpn", false);
        fieldsMap.put("is_sensitive_machine", false);
        fieldsMap.put("event_time_score", 0);
        fieldsMap.put("event_score", 55);
        fieldsMap.put("suspicious_event_score", 1);
        fieldsMap.put("username", "secusr16@somebigcompany.com");
        fieldsMap.put("normalized_username", "secusr16@somebigcompany.com");
        fieldsMap.put("source_ip", "192.168.23.16");
        fieldsMap.put("normalized_src_machine", "SECUSR16_PC");
        fieldsMap.put("source_machine", "SECUSR16_PC");
        fieldsMap.put("status", "Failure");
        fieldsMap.put("event_time", 1493993633000L);
        fieldsMap.put("event_time_utc", 1493993633);
        fieldsMap.put("type", "Kerberos");
        return fieldsMap;
    }
}

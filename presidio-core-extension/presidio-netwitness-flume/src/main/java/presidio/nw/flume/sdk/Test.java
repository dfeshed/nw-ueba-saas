package presidio.nw.flume.sdk;

import fortscale.common.general.Schema;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.nw.flume.domain.NetwitnessEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws  Exception{
        ConfigurationServerClientService configurationServerClientService = null;
        NetwitnessEventsStream eventsStream = new NetwitnessEventsStream();
        Map<String, String> config = new HashMap<>();
        config.put(NetwitnessEventsStream.QUERY, "select *");
        config.put(NetwitnessEventsStream.TIME_FIELD, "time");

        try {
            eventsStream.startStreaming(Schema.AUTHENTICATION, Instant.ofEpochSecond(1518143160), Instant.ofEpochSecond(1546128000), config);
            while (eventsStream.hasNext()) {
                System.out.println(eventsStream.next());
            }
        } catch (Exception ex) {
            throw  ex;
        } finally {
            eventsStream.stopStreaming();
        }

    }


    public static void renameProperty(Map<String, Object> map, String oldName, String newName){
        if (map.get(oldName)!=null) {
            map.put(newName, map.get(oldName));
            map.remove(oldName);
        }
    }

}

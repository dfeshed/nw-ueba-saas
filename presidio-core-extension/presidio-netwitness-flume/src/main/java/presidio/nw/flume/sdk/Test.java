package presidio.nw.flume.sdk;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.common.general.Schema;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.nw.flume.domain.NetwitnessEvent;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;

import java.time.Instant;
import java.util.Map;

public class Test {

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    }



    public static void main(String[] args) throws  Exception{
        ConfigurationServerClientService configurationServerClientService = null;
        NetwitnessEventsStream eventsStream = new NetwitnessEventsStream();
        System.out.println("day");
        try {
            eventsStream.startStreaming(Schema.AUTHENTICATION, Instant.ofEpochSecond(1518143160), Instant.ofEpochSecond(1546128000));
            while (eventsStream.hasNext()) {
                NetwitnessEvent nwe = (NetwitnessEvent) eventsStream.next();
                renameProperty(nwe.getMetaFields(),"event_source_id","eventId");
                renameProperty(nwe.getMetaFields(),"event_source","dataSource");
                renameProperty(nwe.getMetaFields(),"user_dst","userId");
                //renameProperty(nwe.getMetaFields(),"alias_host","srcMachineId");
                String str = mapper.writeValueAsString(nwe);
                System.out.println(str);
                Object parsedEvent = mapper.readValue(str, AuthenticationRawEvent.class);
                System.out.println(parsedEvent);
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

package presidio.nw.flume.sdk;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.nw.flume.domain.NetwitnessEvent;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;

import java.time.Instant;
import java.util.HashMap;
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
        Map<String, String> config = new HashMap<>();
        config.put(NetwitnessEventsStream.QUERY, "select * where reference.id regex '4624|4625|4769|4776'"); //where sessionid=961228");
        config.put(NetwitnessEventsStream.TIME_FIELD, "event.time");

        try {
            int count =0;
            eventsStream.startStreaming(Schema.AUTHENTICATION, Instant.ofEpochSecond(1514589000), Instant.ofEpochSecond(1514589300),config);
            while (eventsStream.hasNext()) {
                NetwitnessEvent nwe = (NetwitnessEvent) eventsStream.next();
                if (nwe!=null) {
                    count++;
                    System.out.println(count+" " +nwe);
                    //renameProperty(nwe.getMetaFields(), "event_source_id", "eventId");
                    //renameProperty(nwe.getMetaFields(), "event_source", "dataSource");
                    //renameProperty(nwe.getMetaFields(), "user_dst", "userId");
                    //renameProperty(nwe.getMetaFields(), "event_time", "dateTime");
                    //renameProperty(nwe.getMetaFields(),"alias_host","srcMachineId");
                    //String str = mapper.writeValueAsString(nwe);
                    //Object parsedEvent = mapper.readValue(str, AuthenticationRawEvent.class);
                    //System.out.println(parsedEvent);
                }
            }
            System.out.println(count);
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

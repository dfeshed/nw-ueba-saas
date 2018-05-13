package source.sdk;

import fortscale.common.general.Schema;
import presidio.config.server.client.ConfigurationServerClientService;

import java.time.Instant;

public class Test {


    public static void main(String[] args){
        ConfigurationServerClientService configurationServerClientService = null;
        NetwitnessEventsStream eventsStream = new NetwitnessEventsStream();

        try {
            eventsStream.startStreaming(Schema.AUTHENTICATION, Instant.ofEpochSecond(1518143160), Instant.ofEpochSecond(1518143460));
            while (eventsStream.hasNext()) {
                System.out.println(eventsStream.next());
            }
        } catch (Exception ex) {
            throw  ex;
        } finally {
            eventsStream.stopStreaming();
        }

    }


}

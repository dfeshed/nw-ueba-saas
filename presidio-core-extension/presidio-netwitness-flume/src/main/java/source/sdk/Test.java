package source.sdk;

import fortscale.common.general.Schema;
import presidio.config.server.client.ConfigurationServerClientService;

import java.time.Instant;

public class Test {


    public static void main(String[] args){
        ConfigurationServerClientService configurationServerClientService = null;
        NetwitnessStreamingSDK netwitnessStreamingSDK = new NetwitnessStreamingSDK();
        netwitnessStreamingSDK.startStreaming(Schema.AUTHENTICATION, Instant.ofEpochSecond(1518143160), Instant.ofEpochSecond(1518143460));
        while (netwitnessStreamingSDK.hasNext()) {
            System.out.println(netwitnessStreamingSDK.next());
        }

        netwitnessStreamingSDK.stopStreaming();
    }


}

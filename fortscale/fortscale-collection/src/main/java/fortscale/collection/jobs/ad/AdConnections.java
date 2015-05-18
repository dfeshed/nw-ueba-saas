package fortscale.collection.jobs.ad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amir Keren on 17/05/2015.
 */
public class AdConnections {

    private List<AdConnection> adConnections;

    public AdConnections() {
        adConnections = new ArrayList();
        //TODO - load data from file
        AdConnection adConnection = new AdConnection("192.168.0.125", "DC=pitzy,DC=dom", "administrator", "P@ssw0rd");
        adConnections.add(adConnection);
    }

    public List<AdConnection> getAdConnections() {
        return adConnections;
    }

}
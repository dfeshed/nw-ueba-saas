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
        AdConnection adConnection = new AdConnection("ldap://192.168.0.75:389", "DC=somebigcompany,DC=com",
                "somebigcompany\\administrator", "iYTLjyA0VryKhpkvBrMMLQ==");
        adConnections.add(adConnection);
    }

    public List<AdConnection> getAdConnections() {
        return adConnections;
    }

}
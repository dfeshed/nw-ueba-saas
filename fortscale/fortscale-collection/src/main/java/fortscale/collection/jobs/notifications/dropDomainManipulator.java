package fortscale.collection.jobs.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * drops the domain suffix from the hostname according to specific chars which marks the domain (e.g. '@').
 * return the hostname without the domain and the domain marker.
 *
 * Created by galiar on 03/03/2016.
 */
public class DropDomainManipulator  implements HostnameManipulator{

    private static Logger logger = LoggerFactory.getLogger(DropDomainManipulator.class);

    @Override
    public String getManipulatedHostname(String hostnameField, List<String> additionalInput) {

        //additional input in the case is a list of chars that marks the domain -- if missing, nothing to do.
        if (additionalInput.isEmpty()){
            logger.info("No domain markers!! no rules for manipulation. no manipulation on hostname field is conducted.");
            return  "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("CASE WHEN ");
        String condition = createHostnameCondition(hostnameField,additionalInput);
        sb.append( condition);
        sb.append(" is null then false esle ");
        sb.append( condition);

        return sb.toString();
    }

    /**
     * create sql condition that drops the domain suffix according to doaminMarkers.
     * @param hostnameField
     * @return
     */
    private String createHostnameCondition(String hostnameField, List<String> domainMarkers) {
        StringBuilder sb = new StringBuilder();

        for(int i =0; i< domainMarkers.size(); i++){
            String domainMarker1 = domainMarkers.get(i);
            for (int j = 0; j < domainMarkers.size(); j++){
                String domainMarker2 = domainMarkers.get(j);

                if( i==0 && j==0 ){ // i==j , first line
                    sb.append(     "(lpad(t1." +hostnameField+", instr(t1."+hostnameField+", '"+domainMarker1+"')-1, '') !=  lpad(t2."+hostnameField+", instr(t2."+hostnameField+", '"+domainMarker2+"')-1, ''))" );
                }
                else{
                    sb.append(" or ((lpad(t1." +hostnameField+", instr(t1."+hostnameField+", '"+domainMarker1+"')-1, '') !=  lpad(t2."+hostnameField+", instr(t2."+hostnameField+", '"+domainMarker2+"')-1, ''))" );
                }
            }
        }
        return sb.toString();
    }
}

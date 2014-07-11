package fortscale.streaming.service.tagging;

import fortscale.streaming.model.tagging.AccountMachineAccess;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by idanp on 7/7/2014.
 */
public class SweeperTagImpl implements ServiceAccountTagging {




    @Value("${sweeperThreshold}") //get the value from fortscale-overriding-streaming.properties file
    private Double threshold;

    public SweeperTagImpl() {

    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }


    public void tag(AccountMachineAccess account)
    {

        if(account == null ||account.getDestinations() == null)
            return;

        //Get the number of destination for that account (the distinct forced at the insertion )
        int numOfDistinctDestinations = account.getDestinations().size();

        account.addTag("Sweeper",(numOfDistinctDestinations >= threshold));



    }
}

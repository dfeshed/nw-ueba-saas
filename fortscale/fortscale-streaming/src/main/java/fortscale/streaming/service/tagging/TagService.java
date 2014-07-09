package fortscale.streaming.service.tagging;

import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.service.SpringService;

import java.util.Collection;
import java.util.List;

/**
 * Created by idanp on 7/8/2014.
 * This class represent the service that will responsible to execute the tagging process implemintations
 */
public class TagService {

    private Collection<ServiceAccountTagging> implementationList;


    public TagService() {

        //retrieve the implementation list from spring
       this.implementationList = SpringService.getInstance().resolveAll(ServiceAccountTagging.class);


    }

    public void tagAccount(AccountMachineAccess targetAccount)
    {
        for(ServiceAccountTagging impl : this.implementationList)
        {
            impl.tag(targetAccount);
        }
    }
}

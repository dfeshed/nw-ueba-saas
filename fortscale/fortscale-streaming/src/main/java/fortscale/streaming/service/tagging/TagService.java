package fortscale.streaming.service.tagging;

import fortscale.domain.core.ComputerUsageType;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.service.SpringService;
import org.apache.samza.storage.kv.KeyValueStore;

import java.util.Collection;
import java.util.List;

/**
 * Created by idanp on 7/8/2014.
 * This class represent the service that will responsible to execute the tagging process implemintations
 */
public class TagService {

    private Collection<ServiceAccountTagging> implementationList;
    private KeyValueStore<String, AccountMachineAccess> store;
    private Long daysBackForArchive;



    public TagService(KeyValueStore<String, AccountMachineAccess> store, long daysBack) {

        this.store = store;
        this.daysBackForArchive = daysBack;

        //retrieve the implementation list from spring
       this.implementationList = SpringService.getInstance().resolveAll(ServiceAccountTagging.class);


    }

    public void handleAccount (String userName, Long timeStamp,String sourceHostName,String destHostName, ComputerUsageType sourceComputerType , ComputerUsageType destComputerType )
    {
        //add or update the account at the store key value
        //check if need to create instance of AccountAccessMachine for that account
        AccountMachineAccess  currentAccount = store.get(userName);
        if (currentAccount == null) {
            currentAccount = new AccountMachineAccess(userName);
            this.store.put(userName, currentAccount);
        }

        currentAccount.setLastEventTimeStamp(timeStamp);

        //Add source and destination machines to the account
        currentAccount.addSource(sourceHostName,timeStamp , sourceComputerType);
        currentAccount.addDestination(destHostName, timeStamp, destComputerType);
        currentAccount.dilutionLists(this.daysBackForArchive);


        //tag the account
        tagAccount(currentAccount);
    }

    public void tagAccount(AccountMachineAccess targetAccount)
    {
        for(ServiceAccountTagging impl : this.implementationList)
        {
            impl.tag(targetAccount);
        }
    }
}

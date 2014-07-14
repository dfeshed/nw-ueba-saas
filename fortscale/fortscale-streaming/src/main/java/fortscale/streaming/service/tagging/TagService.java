package fortscale.streaming.service.tagging;

import fortscale.domain.core.ComputerUsageType;
import fortscale.services.UserService;
import fortscale.streaming.model.prevalance.PrevalanceModel;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.dao.Model;
//import fortscale.streaming.service.dao.State;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by idanp on 7/8/2014.
 * This class represent the service that will responsible to execute the tagging process implemintations
 */
public class TagService {

    private Collection<ServiceAccountTagging> implementationList;
    private KeyValueStore<String, AccountMachineAccess> store;
    private Long daysBackForArchive;

    @Autowired
    private UserService userHandler;



    public TagService(KeyValueStore<String, AccountMachineAccess> store, long daysBack) {

        this.store = store;
        this.daysBackForArchive = daysBack;

        //retrieve the implementation list from spring
       this.implementationList = SpringService.getInstance("classpath*:META-INF/spring/streaming-TaggingTask-context.xml").resolveAll(ServiceAccountTagging.class);


    }

    public void handleAccount(String userName, Long timeStamp,String sourceHostName,String destHostName, ComputerUsageType sourceComputerType , ComputerUsageType destComputerType,boolean isSensetiveMachine )
    {
        //add or update the account at the store key value
        //check if need to create instance of AccountAccessMachine for that account
        AccountMachineAccess  currentAccount = store.get(userName);
        if (currentAccount == null) {
            currentAccount = new AccountMachineAccess(userName);
            currentAccount.setFirstEventTimestamp(timeStamp);

        }

        currentAccount.setLastEventTimeStamp(timeStamp);

        //Add source and destination machines to the account
        currentAccount.addSource(sourceHostName,timeStamp , sourceComputerType);
        currentAccount.addDestination(destHostName, timeStamp, destComputerType,isSensetiveMachine);
        currentAccount.dilutionLists(this.daysBackForArchive);


        //tag the account
        tagAccount(currentAccount);
        
        this.store.put(userName, currentAccount);
    }

    private void tagAccount(AccountMachineAccess targetAccount)
    {
        for(ServiceAccountTagging impl : this.implementationList)
        {
            impl.tag(targetAccount);
        }
    }

    public void exportTags()
    {
        KeyValueIterator iter =  this.store.all();

        while (iter.hasNext()) {
            Entry<String, AccountMachineAccess> entry  = (Entry<String, AccountMachineAccess>) iter.next();
            if(entry.getValue().getIsDirty()) {
                this.userHandler.updateTags(entry.getKey(),entry.getValue().getTags());
                entry.getValue().setIsDirty(false);
            }
        }

    }

    public void closeContext()
    {
        SpringService.getInstance("classpath*:META-INF/spring/streaming-TaggingTask-context.xml").CloseContext();
    }


}

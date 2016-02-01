package fortscale.streaming.service.tagging;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

import fortscale.domain.core.ComputerUsageType;
import fortscale.services.UserService;
import fortscale.streaming.exceptions.KeyValueDBException;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.services.impl.SpringService;

/**
 * Created by idanp on 7/8/2014.
 * This class represent the service that will responsible to execute the tagging process implemintations
 */
public class TagService {

	private static final Logger logger = LoggerFactory.getLogger(TagService.class);
	
    private Collection<ServiceAccountTagging> implementationList;
    private KeyValueStore<String, AccountMachineAccess> store;
    private Long daysBackForArchive;

    private UserService userService;
    private fortscale.services.TagService tagService;

    public TagService(KeyValueStore<String, AccountMachineAccess> store, long daysBack) {

        this.store = store;
        this.daysBackForArchive = daysBack;

        //retrieve the implementation list from spring
       this.implementationList = SpringService.getInstance().resolveAll(ServiceAccountTagging.class);
       this.userService = SpringService.getInstance().resolve(UserService.class);
       this.tagService = SpringService.getInstance().resolve(fortscale.services.TagService.class);

       for (ServiceAccountTagging serviceAccountTagging: implementationList) {
           serviceAccountTagging.addTagToMongo(tagService);
       }
    }

    public void handleAccount(String userName, Long timeStamp,String sourceHostName,String destHostName, ComputerUsageType sourceComputerType , ComputerUsageType destComputerType,boolean isSensetiveMachine ) throws KeyValueDBException
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
        if (StringUtils.isNotEmpty(sourceHostName))
        	currentAccount.addSource(sourceHostName,timeStamp , sourceComputerType);
        if (StringUtils.isNotEmpty(destHostName))
        	currentAccount.addDestination(destHostName, timeStamp, destComputerType,isSensetiveMachine);
        currentAccount.dilutionLists(this.daysBackForArchive);

        //tag the account
        tagAccount(currentAccount);
        
        try{
        	this.store.put(userName, currentAccount);
        } catch(Exception exception){
        	logger.error("error storing value. username: {}, timestamp: {} exception: {}", userName, timeStamp, exception);
            logger.error("error storing value.", exception);
            throw new KeyValueDBException(String.format("error while trying to store user %s.", userName), exception);
        }
    }

    private void tagAccount(AccountMachineAccess targetAccount)
    {
        for(ServiceAccountTagging impl : this.implementationList) {
            impl.tag(targetAccount);
        }
    }

    public void exportTags() throws Exception{
        KeyValueIterator<String, AccountMachineAccess> iter =  this.store.all();
        
        try {
	        while (iter.hasNext()) {
	            Entry<String, AccountMachineAccess> entry  = iter.next();
	            boolean isSavedToMongo = false;
	            if(entry.getValue().getIsDirty()) {
	            	try {
	            		// update the user in mongo
	            		this.userService.updateTags(entry.getKey(), entry.getValue().getTags());
	            		isSavedToMongo = true;
	            	} catch(Exception e) {
	                    logger.error(String.format("error exporing tags for user %s.", entry.getKey()), e);
	                    // propagate exception when connection to mongodb failed, so we won't process additional models 
	                    Throwables.propagateIfInstanceOf(e, org.springframework.dao.DataAccessResourceFailureException.class);
	            	}
	            }
	            if(isSavedToMongo){
	            	entry.getValue().setIsDirty(false);
	            	try {
	            		// update the dirty flag in the store
	            		store.put(entry.getKey(), entry.getValue());
	            	} catch(Exception e) {
	                    logger.error(String.format("error storing value in user {}.", entry.getKey()), e);
	                    throw new KeyValueDBException(String.format("error while trying to store user %s.", entry.getKey()), e);
	            	}
	            }
	        }
        } finally {
        	if (iter!=null)
        		iter.close();
        }
    }
}

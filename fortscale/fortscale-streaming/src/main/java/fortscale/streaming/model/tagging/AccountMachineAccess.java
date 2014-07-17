package fortscale.streaming.model.tagging;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fortscale.domain.core.ComputerUsageType;
import fortscale.domain.core.User;
import fortscale.services.UserService;
import fortscale.utils.TimestampUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by idanp on 7/7/2014.
 * This class represent a Account state that will be save at the LevelDB
 */


@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class AccountMachineAccess {

	private static final Logger logger = LoggerFactory.getLogger(AccountMachineAccess.class);
	
    private String userName;
    private Map<String,MachineState> sources;
    private Map<String,MachineState> destinations;
    private Map<String,Boolean> tags;
    private boolean isDirty;
    private long lastEventTimeStamp;
    private long firstEventTimestamp;





    //CTOs
    @JsonCreator
    public AccountMachineAccess(@JsonProperty("userName") String userName) {

        this.userName = userName;
        this.tags = new HashMap<String,Boolean>();
        this.sources =new HashMap<String,MachineState>();
        this.destinations = new HashMap<String,MachineState>();

    }


    public String getUserName() {
        return userName;
    }

    public Map<String,MachineState> getSources() {
        return sources;
    }

    public Map<String,MachineState> getDestinations() {
        return destinations;
    }

    public  Map<String,Boolean> getTags() {
        return tags;
    }

    public boolean getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public void setLastEventTimeStamp(long lastEventTimeStamp) {
    	long millis = TimestampUtils.convertToMilliSeconds(lastEventTimeStamp);
    	if (!TimestampUtils.isFutureTimestamp(millis, 24))
    		this.lastEventTimeStamp = Math.max(lastEventTimeStamp, millis);
    	else
    		logger.error("encountered event in the future {}, skipping setting last event time [current time is {}]", lastEventTimeStamp, System.currentTimeMillis());
    }

    public long getLastEventTimeStamp() {
        return lastEventTimeStamp;
    }

    public void setFirstEventTimestamp(long firstEventTimestamp) {
        this.firstEventTimestamp = TimestampUtils.convertToMilliSeconds(firstEventTimestamp);
    }

    public long getFirstEventTimestamp() {
        return firstEventTimestamp;
    }



    //Add tag to the tag set
    public void addTag(String tag, boolean flag)
    {
        if(this.tags.get(tag) == null || (this.tags.get(tag) != null && this.tags.get(tag) != flag))
            this.isDirty = true;

        this.tags.put(tag,flag);
    }



    //Add new machine to the source list
    public void addSource(String hostName , long timeStamp, ComputerUsageType type)
    {
        //retrieve the machine state from the source map by hostName
        MachineState machineState =  this.sources.get(hostName);

        if (machineState != null)
        {
            //in case that the machine exist compare the time stamps
            if(machineState.getLastEventTimeStamp() < timeStamp)
                machineState.setLastEventTimeStamp(timeStamp);

        }
        else
        {
            MachineState newMachineState = new MachineState(hostName);
            newMachineState.setLastEventTimeStamp(timeStamp);
            newMachineState.setType(type);
            this.sources.put(hostName,newMachineState);

        }

    }

    //Add new machine to the destination list
    public void addDestination(String hostName , long timeStamp, ComputerUsageType type,boolean isSensetiveMachine)
    {

        //retrive the machine state from the destination map by hostName
        MachineState machineState =  this.destinations.get(hostName);

        if (machineState != null)
        {
            //in case that the machine exist compare the time stamps
            if(machineState.getLastEventTimeStamp() < timeStamp)
                machineState.setLastEventTimeStamp(timeStamp);

            //update the isSensetiveMachine flag (if the machine was sensitive in the past dont change the flag
            //else update the flag
            if(!machineState.isSensitiveMachine())
                machineState.setSensitiveMachine(isSensetiveMachine);

        }
        else
        {
            MachineState newMachineState = new MachineState(hostName);
            newMachineState.setLastEventTimeStamp(timeStamp);
            newMachineState.setType(type);
            newMachineState.setSensitiveMachine(isSensetiveMachine);
            this.destinations.put(hostName,newMachineState);

        }

    }


    public void dilutionLists(Long daysBack)
    {
        long startTimeStampToKeep = this.lastEventTimeStamp - (daysBack*24*60*60*1000);

        diluateList(sources, startTimeStampToKeep);
        diluateList(destinations, startTimeStampToKeep);
    }
    
    private void diluateList(Map<String, MachineState> list, long startTimeStampToKeep) {
    	
    	Iterator<Map.Entry<String, MachineState>> destIter = list.entrySet().iterator();
    	List<String> keysToRemove = new LinkedList<String>();

        while(destIter.hasNext())
        {
            Map.Entry<String, MachineState> entry  = destIter.next();

            //remove  the source if he below the startTimeStampToKeep
            if(entry.getValue().getLastEventTimeStamp() < startTimeStampToKeep)
            	keysToRemove.add(entry.getKey());
        }
        
        if (!keysToRemove.isEmpty()) {
        	for (String key : keysToRemove) 
        		list.remove(key);
        }
    }




}

package fortscale.streaming.model.tagging;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.domain.core.ComputerUsageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by idanp on 7/7/2014.
 * This class represent a Account state that will be save at the LevelDB
 */


@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class AccountMachineAccess {

    private String userName;
    private Map<String,MachineState> sources;
    private Map<String,MachineState> destinations;
    private List<String> tags;
    private boolean isDirty;



    //CTOs
    @JsonCreator
    public AccountMachineAccess(@JsonProperty("userName") String userName) {

        this.userName = userName;
        this.tags = new ArrayList<String>();
        this.sources =new HashMap<String,MachineState>();
        this.destinations = new HashMap<String,MachineState>();



    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String,MachineState> getSources() {
        return sources;
    }

    public void setSources(Map<String,MachineState> sources) {
        this.sources = sources;
    }

    public Map<String,MachineState> getDestinations() {
        return destinations;
    }

    public void setDestinations(Map<String,MachineState> destinations) {
        this.destinations = destinations;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }


    //Add tag to the tag set
    public void addTag(String tag)
    {

        if(!this.tags.contains(tag)) {
            this.tags.add(tag);
            this.isDirty = true;
        }

    }

    //Remove Tag
    public void removeTag(String tag)
    {
        if(this.tags.contains(tag)) {
            this.tags.remove(tag);
            this.isDirty = true;
        }
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
    public void addDestination(String hostName , long timeStamp, ComputerUsageType type)
    {

        //retrive the machine state from the destination map by hostName
        MachineState machineState =  this.destinations.get(hostName);

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
            this.destinations.put(hostName,newMachineState);

        }

    }


}

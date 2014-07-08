package fortscale.streaming.model.tagging;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by idanp on 7/7/2014.
 * This class represent a Account state that will be save at the LevelDB
 */


@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class AccountMachineAccess {

    private String userName;
    private List<MachineState> sources;
    private List<MachineState> destinations;
    private List<String> tags;
    private boolean isDirty;



    //CTOs
    @JsonCreator
    public AccountMachineAccess(@JsonProperty("userName") String userName) {

        this.userName = userName;
        this.tags = new ArrayList<String>();
        this.destinations = new ArrayList<MachineState>();
        this.sources = new ArrayList<MachineState>();

    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<MachineState> getSources() {
        return sources;
    }

    public void setSources(List<MachineState> sources) {
        this.sources = sources;
    }

    public List<MachineState> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<MachineState> destinations) {
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
        switch(tag)
        {
            case "Desktops":
            {
                //in case that the tagging switch from "Servers" to "Desktops"
                if (this.tags.contains("Servers"))
                    this.tags.remove("Servers");
                if(!this.tags.contains(tag)) {
                    this.tags.add(tag);
                    this.isDirty = true;
                }

                //can add here some notification handling for switching between those two tags
                break;
            }

            case "Servers":
            {
                //in case that the tagging switch from "Desktops" to "Servers"
                if (this.tags.contains("Desktops"))
                    this.tags.remove("Desktops");
                if(!this.tags.contains(tag)) {
                    this.tags.add(tag);
                    this.isDirty = true;
                }

                //can add here some notification handling for switching between those two tags
                break;
            }

            default:
            {
                if(!this.tags.contains(tag)) {
                    this.tags.add(tag);
                    this.isDirty = true;
                }

                break;
            }


        }


    }

    //Add new machine to the source list
    public void addSource(MachineState machine)
    {
        int instanceIndex = this.sources.indexOf(machine);
        //if the machine already exist in the list than compare the timestamps
        if(instanceIndex != -1)
        {

            MachineState existing = this.sources.get(instanceIndex);

            //switch the existing with the current machine cause its newer
            if(existing.getLastEventTimeStamp() < machine.getLastEventTimeStamp())
            {
                this.sources.remove(existing);
                this.sources.add(machine);

                this.isDirty=true;
            }

        }

        //else insert the new machine to the list
        else
        {
            this.sources.add(machine);
            this.isDirty = true;
        }
    }

    //Add new machine to the destination list
    public void addDestination(MachineState machine)
    {
        int instanceIndex = this.destinations.indexOf(machine);


        //if the machine already exist in the list than compare the timestamps
        if(instanceIndex != -1)
        {

            MachineState existing = this.destinations.get(instanceIndex);

            //switch the existing with the current machine cause its newer
            if(existing.getLastEventTimeStamp() < machine.getLastEventTimeStamp())
            {
                this.destinations.remove(existing);
                this.destinations.add(machine);

                this.isDirty = true;
            }

        }


        //else insert the new machine to the list
        else
        {
            this.destinations.add(machine);
            this.isDirty = true;
        }
    }


}

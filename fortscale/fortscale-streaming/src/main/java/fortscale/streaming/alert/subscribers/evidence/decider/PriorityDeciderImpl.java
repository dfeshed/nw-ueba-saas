package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by rans on 14/03/16.
 */
public class PriorityDeciderImpl extends OrderedDeciderCommandAbstract<Integer>{

    @Autowired
    private AlertConfiguration conf;


    private AlertConfiguration.PriorityType type;



    private boolean useMaxPriority = true; //If true - return the List<EnrichedFortscaleEvent> with max priority
                                   //If false - return the List<EnrichedFortscaleEvent> with min priority


    public PriorityDeciderImpl(AlertConfiguration.PriorityType type) {
        this.type = type;
    }

    public PriorityDeciderImpl(AlertConfiguration.PriorityType type, boolean useMaxPriority) {
        this.type = type;
        this.useMaxPriority = useMaxPriority;
    }

    protected  Integer getOrder(EnrichedFortscaleEvent evidence){
        String anomaly = evidence.getAnomalyTypeFieldName();
        Integer priority = conf.getPriorityMap(type).get(anomaly);
        return  priority;
    }

    protected boolean isUseMax(){
        return useMaxPriority;
    }


    public boolean isUseMaxPriority() {
        return useMaxPriority;
    }

    public void setUseMaxPriority(boolean useMaxPriority) {
        this.useMaxPriority = useMaxPriority;
    }
}

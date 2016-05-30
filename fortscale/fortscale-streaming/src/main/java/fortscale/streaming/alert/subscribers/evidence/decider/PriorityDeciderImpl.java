package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.domain.core.AlertTimeframe;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by rans on 14/03/16.
 */
public class PriorityDeciderImpl extends OrderedDeciderCommandAbstract<Integer>{

    @Autowired
    private AlertTypeConfigurationServiceImpl conf;


    private AlertTypeConfigurationServiceImpl.PriorityType type;



    private boolean useMaxPriority = true; //If true - return the List<EnrichedFortscaleEvent> with max priority
                                   //If false - return the List<EnrichedFortscaleEvent> with min priority


    public PriorityDeciderImpl(AlertTypeConfigurationServiceImpl.PriorityType type) {
        this.type = type;
    }

    public PriorityDeciderImpl(AlertTypeConfigurationServiceImpl.PriorityType type, boolean useMaxPriority) {
        this.type = type;
        this.useMaxPriority = useMaxPriority;
    }

    protected  Integer getOrder(EnrichedFortscaleEvent evidence,AlertTimeframe alertTimeframe){
        String anomaly = evidence.getAnomalyTypeFieldName();
        int priority = conf.getPriority(anomaly, this.type, alertTimeframe);
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

package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.domain.core.AlertTimeframe;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;

/**
 * Created by rans on 14/03/16.
 */
public class FreshnessDeciderImpl extends OrderedDeciderCommandAbstract<Long>{

    private boolean useMaxTime = true; //If true - return the List<EnrichedFortscaleEvent> with max score
                                        //If false - return the List<EnrichedFortscaleEvent> with min score


    protected  Long getOrder(EnrichedFortscaleEvent evidence,AlertTimeframe alertTimeframe){
        Long time = evidence.getStartTimeUnix();
        return time;
    }

    protected boolean isUseMax(){
        return useMaxTime;
    }


    public boolean isUseMaxTime() {
        return useMaxTime;
    }

    public void setUseMaxTime(boolean useMaxTime) {
        this.useMaxTime = useMaxTime;
    }
}

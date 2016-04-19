package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;

/**
 * Created by rans on 14/03/16.
 */
public class ScoreDeciderImpl extends OrderedDeciderCommandAbstract<Integer>{

    private boolean useMaxScore = true; //If true - return the List<EnrichedFortscaleEvent> with max score
                                        //If false - return the List<EnrichedFortscaleEvent> with min score

    protected  Integer getOrder(EnrichedFortscaleEvent evidence){
        return evidence.getScore();
    }
    protected boolean isUseMax(){
        return useMaxScore;
    }


}

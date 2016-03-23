package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by rans on 14/03/16.
 */
@Component
public class DeciderServiceImpl implements  DeciderService{

    private List<DeciderCommand> nameDecidersList; //Some order list of command
    private List<DeciderCommand> scoreDecidersList; //Some order list of command

    @Autowired
    private DeciderConfiguration conf;

    public String decideName(List<EnrichedFortscaleEvent> evidences){
        String title = null;
        EnrichedFortscaleEvent evidenceForTitle = executeInternal(evidences, nameDecidersList);
        if (evidenceForTitle !=null){
            title = conf.getAlertNameByAnonalyType(evidenceForTitle.getAnomalyTypeFieldName());
        } else {
            title  = "No Name Match to the alert";
        }

        return title;

    }

    public int decideScore(List<EnrichedFortscaleEvent> evidences){
        int score = Integer.MIN_VALUE;
        EnrichedFortscaleEvent evidenceForTitle = executeInternal(evidences, scoreDecidersList);

        if (evidenceForTitle !=null){
            score = evidenceForTitle.getScore();
        }

        return score;
    }


    private EnrichedFortscaleEvent executeInternal(List<EnrichedFortscaleEvent> evidences, List<DeciderCommand> deciderCommandList){
        List<EnrichedFortscaleEvent> candidates = evidences;

        for (DeciderCommand decider: deciderCommandList){
            if (candidates.size()> 1){//There is more then one candidate, need to make the list smaller
                candidates = decider.decide(candidates);
            }
        }

        if (candidates.size() > 0){
            return candidates.get(0);
        } else {
            return null;
        }
    }


    public List<DeciderCommand> getNameDecidersList() {
        return nameDecidersList;
    }

    public void setNameDecidersList(List<DeciderCommand> nameDecidersList) {
        this.nameDecidersList = nameDecidersList;
    }

    public List<DeciderCommand> getScoreDecidersList() {
        return scoreDecidersList;
    }

    public void setScoreDecidersList(List<DeciderCommand> scoreDecidersList) {
        this.scoreDecidersList = scoreDecidersList;
    }


    public DeciderConfiguration getConf() {
        return conf;
    }

    public void setConf(DeciderConfiguration conf) {
        this.conf = conf;
    }
}

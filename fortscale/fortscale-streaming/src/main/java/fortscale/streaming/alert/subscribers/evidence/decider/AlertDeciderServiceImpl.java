package fortscale.streaming.alert.subscribers.evidence.decider;

import fortscale.domain.core.AlertTimeframe;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by rans on 14/03/16.
 */
public class AlertDeciderServiceImpl implements AlertDeciderService {

    private List<DeciderCommand> nameDecidersList; //Some order list of command
    private List<DeciderCommand> scoreDecidersList; //Some order list of command

    @Autowired
    private AlertTypeConfigurationServiceImpl conf;

    public String decideName(List<EnrichedFortscaleEvent> evidences, AlertTimeframe alertTimeframe){
        String title = null;
        EnrichedFortscaleEvent evidenceForTitle = executeInternal(evidences, nameDecidersList, alertTimeframe);
        if (evidenceForTitle !=null){
            title = conf.getAlertNameByAnonalyType(evidenceForTitle.getAnomalyTypeFieldName(),alertTimeframe);
        } else {
            title  = "No Name Match to the alert";
        }

        return title;

    }

    public int decideScore(List<EnrichedFortscaleEvent> evidences, AlertTimeframe alertTimeframe){
        int score = Integer.MIN_VALUE;
        EnrichedFortscaleEvent evidenceForScore = executeInternal(evidences, scoreDecidersList, alertTimeframe);

        if (evidenceForScore !=null){
            score = evidenceForScore.getScore();
        }

        return score;
    }


    private EnrichedFortscaleEvent executeInternal(List<EnrichedFortscaleEvent> evidences, List<DeciderCommand> deciderCommandList, AlertTimeframe alertTimeframe){
        List<EnrichedFortscaleEvent> candidates = evidences;

        for (DeciderCommand decider: deciderCommandList){
            if (candidates.size()> 1){//There is more then one candidate, need to make the list smaller
                candidates = decider.decide(candidates,alertTimeframe);
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


    public AlertTypeConfigurationServiceImpl getConf() {
        return conf;
    }

    public void setConf(AlertTypeConfigurationServiceImpl conf) {
        this.conf = conf;
    }
}

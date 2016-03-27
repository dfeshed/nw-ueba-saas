package fortscale.ml.scorer.params;

import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.PriorityScorerContainerConf;

import java.util.ArrayList;
import java.util.List;

public class PriorityScorerParams implements ScorerParams {
    List<ScorerParams> scorerParamsList = new ArrayList<>();
    List<Scorer> scorerList = new ArrayList<>();
    String name = "priority scorer name";

    public List<ScorerParams> getScorerParamsList() {
        return scorerParamsList;
    }

    public PriorityScorerParams setScorerParamsList(List<ScorerParams> scorerParamsList) {
        this.scorerParamsList = scorerParamsList;
        if(scorerParamsList!=null) {
            for (ScorerParams scorerParams : scorerParamsList) {
                scorerList.add(scorerParams.getScorer());
            }
        }
        return this;
    }

    public List<Scorer> getScorerList() {
        return scorerList;
    }

    public String getName() {
        return name;
    }

    public PriorityScorerParams setName(String name) {
        this.name = name;
        return this;
    }

    public PriorityScorerParams addScorerParams(ScorerParams scorerParams) {
        scorerParamsList.add(scorerParams);
        scorerList.add(scorerParams.getScorer());
        return this;
    }

    public PriorityScorerParams addScorer(Scorer scorer) {
        scorerList.add(scorer);
        return this;
    }

    public String getScorerConfJsonString() {
        String res = "{\"type\":\""+ PriorityScorerContainerConf.SCORER_TYPE+"\"";
        res+= (name==null ? "" : ",\"name\":\""+name+"\"");
        if(scorerParamsList!=null) {
            res+=",\"scorers\":[";
            boolean firstScorer = true;
            for (ScorerParams scorerParams : scorerParamsList) {
                if(firstScorer) {
                    firstScorer=false;
                } else {
                    res+=",";
                }
                res +=  scorerParams.getScorerConfJsonString();
            }
            res+="]";
        }
        res+="}";
        return res;
    }
}

package fortscale.ml.scorer;

import java.util.ArrayList;
import java.util.List;


abstract public class ScorerContainer extends AbstractScorer {

    protected List<Scorer> scorers = new ArrayList<>();

    public ScorerContainer(String name, List<Scorer> scorers) {

        super(name);
        this.scorers = scorers;
    }

}

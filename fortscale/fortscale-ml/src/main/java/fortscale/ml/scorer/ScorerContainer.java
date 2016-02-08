package fortscale.ml.scorer;


import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;


abstract public class ScorerContainer extends AbstractScorer {

    protected List<Scorer> scorers = new ArrayList<>();

    public ScorerContainer(String name, List<Scorer> scorers) {

        super(name);
        Assert.notNull(scorers, "scorers must not be null");
        Assert.isTrue(!scorers.isEmpty(), "scorers must hold at least one scorer");
        this.scorers = scorers;
    }

    public List<Scorer> getScorers() {
        return scorers;
    }
}

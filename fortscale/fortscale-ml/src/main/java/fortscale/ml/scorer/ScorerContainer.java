package fortscale.ml.scorer;

import org.eclipse.jdt.internal.core.Assert;

import java.util.ArrayList;
import java.util.List;


abstract public class ScorerContainer extends AbstractScorer {

    protected List<Scorer> scorers = new ArrayList<>();

    public ScorerContainer(String name, List<Scorer> scorers) {

        super(name);
        Assert.isNotNull(scorers, "scorers must not be null");
        Assert.isTrue(!scorers.isEmpty(), "scorers must hold at least one scorer");
        this.scorers = scorers;
    }

}

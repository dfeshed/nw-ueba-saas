package fortscale.ml.scorer;

import org.springframework.util.Assert;

abstract public class AbstractScorer implements Scorer {

    private String name;

    public AbstractScorer(String name) {
        Assert.hasText(name, "scorer name must be provided and cannot be null, empty or blank");
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

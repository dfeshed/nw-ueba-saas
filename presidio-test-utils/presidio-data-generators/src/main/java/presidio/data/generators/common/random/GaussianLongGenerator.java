package presidio.data.generators.common.random;

import org.apache.commons.math3.distribution.NormalDistribution;
import presidio.data.generators.IBaseGenerator;

public class GaussianLongGenerator implements IBaseGenerator<Long> {
    private NormalDistribution dist;

    public GaussianLongGenerator(double mean , double sd) {
        dist = new NormalDistribution(mean, sd);
    }

    @Override
    public Long getNext() {
        return Math.round(dist.sample());
    }
}

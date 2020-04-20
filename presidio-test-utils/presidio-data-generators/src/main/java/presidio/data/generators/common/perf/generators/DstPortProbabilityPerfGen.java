package presidio.data.generators.common.perf.generators;

import org.testng.Assert;
import presidio.data.generators.IBaseGenerator;

import java.util.Random;

public class DstPortProbabilityPerfGen implements IBaseGenerator<Integer> {

    private final double probability;
    private final int fromPort;
    private final int toPort;
    private final int defaultPort;
    private Random random = new Random();

    public DstPortProbabilityPerfGen(double probability, int fromPort, int toPort, int defaultPort) {
        Assert.assertTrue(probability <= 1);
        Assert.assertTrue(probability >=0);
        this.fromPort = fromPort;
        this.toPort = toPort;
        this.defaultPort = defaultPort;
        this.probability = probability;
    }

    @Override
    public Integer getNext() {
        if (random.nextDouble() <= probability) {
            return random.ints(fromPort, toPort).findAny().orElse(defaultPort);
        }
        return defaultPort;
    }
}

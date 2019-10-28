package presidio.data.generators.common.perf;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public abstract class UniformBasedPerfGen<T> extends DistributionBasedGen<T, Integer> {

    private final int maxBound;
    public UniformBasedPerfGen(int uniqueId, int amount) {
        super(uniqueId);
        this.maxBound = amount;
    }

    @Override
    protected Supplier<Integer> getSample() {
        return () -> ThreadLocalRandom.current().nextInt(0, maxBound);
    }

}
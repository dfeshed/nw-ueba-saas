package presidio.data.generators.common.perf.tls;

import presidio.data.generators.common.perf.UniformBasedPerfGen;

import java.util.function.Function;

public class DstPortPerfGen extends UniformBasedPerfGen<Integer> {


    public DstPortPerfGen(int uniqueId, int amount) {
        super(uniqueId, amount);
        if (amount > 999) throw new RuntimeException("Out of limit");
    }

    @Override
    protected Function<Integer, Integer> getMappingFunc() {
        return i -> UNIQUE_ID * 1000 + i;
    }

    @Override
    protected Function<Integer, Integer> getTransformationFunc() {
        return e -> e;
    }

}
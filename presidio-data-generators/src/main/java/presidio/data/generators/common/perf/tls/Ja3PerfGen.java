package presidio.data.generators.common.perf.tls;

import presidio.data.generators.common.perf.UniformBasedPerfGen;

import java.util.function.Function;

public class Ja3PerfGen extends UniformBasedPerfGen<String> {

    public Ja3PerfGen(int uniqueId, int amount) {
        super(uniqueId, amount);
    }

    @Override
    protected Function<Integer, String> getMappingFunc() {
        return i ->  "6e1932bea4b34db0b3cb4f" + "-" + UNIQUE_ID + "-" + i;
    }

}
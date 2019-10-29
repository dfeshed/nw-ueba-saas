package presidio.data.generators.common.perf.tls;

import presidio.data.generators.common.perf.UniformBasedPerfGen;

import java.util.function.Function;

public class SrcNetnamePerfGen extends UniformBasedPerfGen<String> {


    private static final String prefix = "private";

    public SrcNetnamePerfGen(int uniqueId, int amount) {
        super(uniqueId, amount);
    }

    @Override
    protected Function<Integer, String> getMappingFunc() {
        return i -> prefix + "_" + UNIQUE_ID + "_" + i;
    }

    @Override
    protected Function<String, String> getTransformationFunc() {
        return e -> e;
    }

}
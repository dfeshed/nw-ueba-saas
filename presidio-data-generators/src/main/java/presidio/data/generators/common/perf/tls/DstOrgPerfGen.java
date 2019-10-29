package presidio.data.generators.common.perf.tls;

import presidio.data.generators.common.perf.UniformBasedPerfGen;

import java.util.function.Function;

public class DstOrgPerfGen extends UniformBasedPerfGen<String> {


    private static final String prefix = "private";

    public DstOrgPerfGen(int uniqueId, int amount) {
        super(uniqueId, amount);
    }

    @Override
    protected Function<Integer, String> getMappingFunc() {
        return i -> prefix + "_" + UNIQUE_ID + "_" + i;
    }


}
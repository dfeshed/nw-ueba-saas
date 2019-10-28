package presidio.data.generators.common.perf.tls;

import presidio.data.generators.common.perf.UniformBasedPerfGen;

import java.util.function.Function;

import static presidio.data.generators.common.list.content.Ipv4.indexToIpv4NoOffset;

public class Ipv4PerfGen extends UniformBasedPerfGen<String> {

    public Ipv4PerfGen(int uniqueId, int amount) {
        super(uniqueId, amount);
    }

    @Override
    protected Function<Integer, String> getMappingFunc() {
        return i -> indexToIpv4NoOffset.apply(UNIQUE_ID * 10000 + i);
    }

    @Override
    protected Function<String, String> getTransformationFunc() {
        return e -> e;
    }

}
package presidio.data.generators.event.tls;

import presidio.data.generators.common.list.RangeGenerator;

import static presidio.data.generators.event.tls.TlsValuesAllocator.TLS_VALUES_ALLOCATOR;

public class DstPortRangeAllocator extends FieldRangeAllocator<Integer> {

    @Override
    public RangeGenerator<Integer> nextRangeGenCyclic(int range) {
        RangeGenerator<Integer> gen = TLS_VALUES_ALLOCATOR.nextDstPortRangeCyclic(range);
        setGenerator(gen);
        return gen;
    }

    @Override
    public RangeGenerator<Integer> nextRangeRandom(int range) {
        RangeGenerator<Integer> gen = TLS_VALUES_ALLOCATOR.nextDstPortRangeRandom(range);
        setGenerator(gen);
        return gen;
    }
}

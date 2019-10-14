package presidio.data.generators.event.tls;

import presidio.data.generators.common.list.ListBasedGen;

import static presidio.data.generators.event.tls.TlsValuesAllocator.TLS_VALUES_ALLOCATOR;

public class DstOrgRangeAllocator extends FieldRangeAllocator<String> {

    @Override
    public ListBasedGen<String> nextRangeGenCyclic(int range) {
        ListBasedGen<String> gen = TLS_VALUES_ALLOCATOR.nextDstOrgRangeCyclic(range);
        setGenerator(gen);
        return gen;
    }

    @Override
    public ListBasedGen<String> nextRangeRandom(int range) {
        ListBasedGen<String> gen = TLS_VALUES_ALLOCATOR.nextDstOrgRangeRandom(range);
        setGenerator(gen);
        return gen;
    }
}

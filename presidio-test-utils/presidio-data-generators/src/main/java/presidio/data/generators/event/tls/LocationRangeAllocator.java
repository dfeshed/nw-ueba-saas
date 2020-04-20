package presidio.data.generators.event.tls;

import presidio.data.domain.Location;
import presidio.data.generators.common.list.ListBasedGen;

import static presidio.data.generators.event.tls.TlsValuesAllocator.TLS_VALUES_ALLOCATOR;

public class LocationRangeAllocator extends FieldRangeAllocator<Location> {

    @Override
    public ListBasedGen<Location> nextRangeGenCyclic(int range) {
        ListBasedGen<Location> gen = TLS_VALUES_ALLOCATOR.nextLocationRangeCyclic(range);
        setGenerator(gen);
        return gen;
    }

    @Override
    public ListBasedGen<Location> nextRangeRandom(int range) {
        ListBasedGen<Location> gen = TLS_VALUES_ALLOCATOR.nextLocationRangeRandom(range);
        setGenerator(gen);
        return gen;
    }
}

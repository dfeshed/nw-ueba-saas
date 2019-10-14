package presidio.data.generators.common.list.random;

import com.google.common.collect.ImmutableList;
import presidio.data.generators.common.list.ListBasedGen;

import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static presidio.data.generators.common.list.content.Ipv4.indexToIpv4;

public class RandomRangeIpv4Gen extends ListBasedGen<String> {
    private final ImmutableList<String> list;

    public RandomRangeIpv4Gen(int fromIndex, int size) {
        super(0, size, size);
        list = ImmutableList.copyOf(IntStream.range(fromIndex, fromIndex + size).boxed().map(indexToIpv4).collect(toList()));
    }

    @Override
    public String getNext() {
        return getNextRandom();
    }

    @Override
    protected ImmutableList<String> getList() {
        return list;
    }


}

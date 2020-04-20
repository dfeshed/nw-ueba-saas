package presidio.data.generators.common.list.cyclic;

import com.google.common.collect.ImmutableList;
import presidio.data.generators.common.list.ListBasedGen;
import presidio.data.generators.common.random.Md5RandomGenerator;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CyclicRangeMd5Gen extends ListBasedGen<String> {
    private int size;
    private final ImmutableList<String> list;

    public CyclicRangeMd5Gen(int size) {
        super(0, size, size);
        this.size = size;
        list = ImmutableList.copyOf(IntStream.range(0, size).boxed().map(e -> new Md5RandomGenerator().getNext()).collect(Collectors.toList()));
    }

    @Override
    public String getNext() {
        return getNextCyclic();
    }

    @Override
    protected ImmutableList<String> getList() {
        return list;
    }
}

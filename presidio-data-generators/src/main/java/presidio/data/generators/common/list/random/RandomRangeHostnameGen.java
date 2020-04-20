package presidio.data.generators.common.list.random;

import com.google.common.collect.ImmutableList;
import presidio.data.generators.common.list.ListBasedGen;

import static presidio.data.generators.common.list.content.Hostnames.HOSTNAMES;

public class RandomRangeHostnameGen extends ListBasedGen<String> {

    public RandomRangeHostnameGen(int fromIndex, int size) {
        super(fromIndex, size, HOSTNAMES.size());
    }

    @Override
    public String getNext() {
        return getNextRandom();
    }

    @Override
    protected ImmutableList<String> getList() {
        return HOSTNAMES;
    }
}

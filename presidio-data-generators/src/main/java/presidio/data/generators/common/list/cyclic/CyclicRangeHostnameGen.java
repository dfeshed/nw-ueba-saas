package presidio.data.generators.common.list.cyclic;

import com.google.common.collect.ImmutableList;
import presidio.data.generators.common.list.ListBasedGen;

import static presidio.data.generators.common.list.content.Hostnames.HOSTNAMES;

public class CyclicRangeHostnameGen extends ListBasedGen {

    public CyclicRangeHostnameGen(int fromIndex, int size) {
        super(fromIndex, size, HOSTNAMES.size());
    }

    @Override
    public String getNext() {
        return getNextCyclic();
    }

    @Override
    protected ImmutableList<String> getList() {
        return HOSTNAMES;
    }
}

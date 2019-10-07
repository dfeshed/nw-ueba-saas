package presidio.data.generators.common.list.random;

import com.google.common.collect.ImmutableList;
import presidio.data.generators.common.list.ListBasedGen;

import static presidio.data.generators.common.list.content.AlexaDomains.ALEXA_DOMAINS;

public class RandomRangeDomainGen extends ListBasedGen {

    public RandomRangeDomainGen(int fromIndex, int size) {
        super(fromIndex, size,ALEXA_DOMAINS.size());
    }

    @Override
    public String getNext() {
        return getNextRandom();
    }

    @Override
    protected ImmutableList<String> getList() {
        return ALEXA_DOMAINS;
    }

}

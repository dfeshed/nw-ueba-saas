package presidio.data.generators.common.list.random;

import com.google.common.collect.ImmutableList;
import presidio.data.generators.common.list.ListBasedGen;

import static presidio.data.generators.common.list.content.SingleWord.SINGLE_WORDS;

public class RandomRangeWordGen extends ListBasedGen {

    public RandomRangeWordGen(int fromIndex, int size) {
        super(fromIndex, size, SINGLE_WORDS.size());
    }

    @Override
    public String getNext() {
        return getNextRandom();
    }

    @Override
    protected ImmutableList<String> getList() {
        return SINGLE_WORDS;
    }
}

package presidio.data.generators.common.list.random;

import com.google.common.collect.ImmutableList;
import presidio.data.generators.common.list.ListBasedGen;

import static presidio.data.generators.common.list.content.CompanyNames.COMPANY_NAMES;


public class RandomRangeCompanyGen extends ListBasedGen {

    public RandomRangeCompanyGen(int fromIndex, int size) {
        super(fromIndex, size,  COMPANY_NAMES.size());
    }

    @Override
    public String getNext() {
        return getNextRandom();
    }

    @Override
    protected ImmutableList<String> getList() {
        return COMPANY_NAMES;
    }
}

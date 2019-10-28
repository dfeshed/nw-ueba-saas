package presidio.data.generators.common.perf.tls;

import presidio.data.generators.common.list.RangeGenerator;
import presidio.data.generators.common.list.random.RandomRangeCompanyGen;
import presidio.data.generators.common.perf.UniformBasedPerfGen;

import java.util.function.Function;

import static presidio.data.generators.common.list.content.CompanyNames.COMPANY_NAMES;

public class SslSubjectPerfGen extends UniformBasedPerfGen<String> {


    private RangeGenerator subjects = new RandomRangeCompanyGen(0, COMPANY_NAMES.size());

    public SslSubjectPerfGen(int uniqueId, int amount) {
        super(uniqueId, amount);
    }

    @Override
    protected Function<Integer, String> getMappingFunc() {
        return i -> subjects.getNext() + " " + UNIQUE_ID + " " + i;
    }

    @Override
    protected Function<String, String> getTransformationFunc() {
        return  e -> e.toLowerCase().replaceAll("\\W+"," ").trim();
    }

}
package presidio.data.generators.common.perf.tls;

import presidio.data.generators.common.perf.UniformBasedPerfGen;

import java.util.function.Function;

import static presidio.data.generators.common.list.content.CompanyNames.COMPANY_NAMES;

public class SslSubjectPerfGen extends UniformBasedPerfGen<String> {

    public SslSubjectPerfGen(int uniqueId, int amount) {
        super(uniqueId, amount);
    }

    @Override
    protected Function<Integer, String> getMappingFunc() {
        return i -> COMPANY_NAMES.get(i % COMPANY_NAMES.size()) + " " + UNIQUE_ID + " " + i;
    }

}
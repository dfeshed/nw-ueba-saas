package presidio.data.generators.common.perf.generators;

import presidio.data.generators.IBaseGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SslSubjectLimitedPerfGen implements IBaseGenerator<SslSubjectContainer> {

    private static AtomicInteger UNIQUE_ID_COUNTER = new AtomicInteger(0);
    private List<SslSubjectContainer> sslSubjectGens = new ArrayList<>();
    private int next;


    public SslSubjectLimitedPerfGen(int amount) {
        next = 0;
        for (int i=0; i<amount; i++) {
            sslSubjectGens.add(new SslSubjectContainer(UNIQUE_ID_COUNTER.getAndAdd(1)));
        }
    }

    @Override
    public SslSubjectContainer getNext() {
        if (next >= sslSubjectGens.size()) {
            next = 0;
        }
        return sslSubjectGens.get(next++);
    }
}

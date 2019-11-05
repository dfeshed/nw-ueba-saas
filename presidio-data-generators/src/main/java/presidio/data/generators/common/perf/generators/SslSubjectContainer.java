package presidio.data.generators.common.perf.generators;

import presidio.data.generators.FixedValueGenerator;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.perf.lists.PerfGenListMappers;

import java.util.Random;

import static presidio.data.generators.common.list.content.Ipv4.indexToIpv4;
import static presidio.data.generators.event.performance.tls.TlsEventsSimplePerfGen.NUM_OF_DOMAINS;

public class SslSubjectContainer {

    private final double MANY_DOMAINS_PROBABILITY = 0.001;

    private IBaseGenerator<String> sslSubjectGen;
    private LimitedListPerfGenerator<String> domainGen;
    private FixedValueGenerator<String> dstIpGen;


    SslSubjectContainer(int uniqueId) {
        Random random = new Random(uniqueId);

        sslSubjectGen = new FixedValueGenerator<>("International Business Machines Corporation " + uniqueId);
        dstIpGen = new FixedValueGenerator<>(indexToIpv4.apply(uniqueId));

        if (random.nextDouble() <= MANY_DOMAINS_PROBABILITY) {
            int manyDomains = random.nextInt(1000);
            domainGen =  new LimitedListPerfGenerator<>(manyDomains * 3, PerfGenListMappers.hostnameMapper(NUM_OF_DOMAINS));
        } else {
            domainGen =  new LimitedListPerfGenerator<>(5 * 3, PerfGenListMappers.hostnameMapper(NUM_OF_DOMAINS));
        }

    }


    public IBaseGenerator<String> getSslSubjectGen() {
        return sslSubjectGen;
    }

    public IBaseGenerator<String> getDomainGen() {
        return domainGen;
    }

    public IBaseGenerator<String> getDstIpGen() {
        return dstIpGen;
    }

}

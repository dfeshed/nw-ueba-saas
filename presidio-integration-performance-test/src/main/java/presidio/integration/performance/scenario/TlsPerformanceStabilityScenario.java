package presidio.integration.performance.scenario;

import presidio.data.generators.event.performance.tls.clusters.TlsEventsSimplePerfGen;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static presidio.integration.performance.generators.tls.ClusterSizeFactory.*;

public class TlsPerformanceStabilityScenario {


    private final int LOW_SIZE_CLUSTERS = 500;
    private final int MEDIUM_SIZE_CLUSTERS = 200;
    private final int LARGE_SIZE_CLUSTERS = 3;

    private List<TlsEventsSimplePerfGen> tlsGroupSmall = IntStream.range(0, LOW_SIZE_CLUSTERS).boxed().map(index -> new TlsEventsSimplePerfGen(getSmallClusterParams())).collect(toList());
    private List<TlsEventsSimplePerfGen> tlsGroupMedium = IntStream.range(0, MEDIUM_SIZE_CLUSTERS).boxed().map(index -> new TlsEventsSimplePerfGen(getMediumClusterParams())).collect(toList());
    private List<TlsEventsSimplePerfGen> tlsGroupLarge = IntStream.range(0, LARGE_SIZE_CLUSTERS).boxed().map(index -> new TlsEventsSimplePerfGen(getLargeClusterParams())).collect(toList());



    public TlsPerformanceStabilityScenario(Instant startInstant, Instant endInstant, int numOfNormalUsers, double probabilityMultiplier) {

    }

    public List<TlsEventsSimplePerfGen> tlsEventsGenerators = Stream.of(
            tlsGroupSmall.stream(),
            tlsGroupMedium.stream(),
            tlsGroupLarge.stream())
            .flatMap(a -> a).collect(Collectors.toList());



}

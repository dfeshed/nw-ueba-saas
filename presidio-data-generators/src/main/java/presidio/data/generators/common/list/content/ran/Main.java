package presidio.data.generators.common.list.content.ran;

import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.performance.tls.clusters.TlsEventsSimplePerfGen;
import presidio.data.generators.event.performance.tls.clusters.TlsPerfClusterParams;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Main {


    private static TlsPerfClusterParams tlsPerfClusterParamsSmall = new TlsPerfClusterParams(1,10,10,10,10,1,1,10,1);
    private static TlsPerfClusterParams tlsPerfClusterParamsMedium = new TlsPerfClusterParams(1,1,1,1,1,1,1,1,1);
    private static TlsPerfClusterParams tlsPerfClusterParamsLarge = new TlsPerfClusterParams(1,1,1,1,1,1,1,1,1);


    public static void main(String[] args){
        List<TlsEventsSimplePerfGen> tlsGroupSmall = IntStream.range(0, 1).boxed().map(index -> new TlsEventsSimplePerfGen(tlsPerfClusterParamsSmall)).collect(toList());
        List<TlsEventsSimplePerfGen> tlsGroupMedium = IntStream.range(0, 2).boxed().map(index -> new TlsEventsSimplePerfGen(tlsPerfClusterParamsMedium)).collect(toList());
        List<TlsEventsSimplePerfGen> tlsGroupLarge = IntStream.range(0, 3).boxed().map(index -> new TlsEventsSimplePerfGen(tlsPerfClusterParamsLarge)).collect(toList());

        List<TlsEventsSimplePerfGen> groups = Stream.of(tlsGroupSmall.stream(), tlsGroupMedium.stream(), tlsGroupLarge.stream()).flatMap(a -> a).collect(Collectors.toList());

        List<TlsEvent> tlsEvents = groups.stream().map(a -> {
            try {
                return a.generate().stream();
            } catch (GeneratorException e) {
                e.printStackTrace();
                return null;
            }
        }).flatMap(a -> a).collect(toList());

        System.out.println(tlsEvents.size());
    }

}

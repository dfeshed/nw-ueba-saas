package presidio.data.generators.event.network;

import com.google.common.base.CaseFormat;
import presidio.data.domain.Location;
import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.generators.FixedValueGenerator;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.hostname.HostnameGenerator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static presidio.data.generators.event.network.NetworkEventsGenerator.*;

public class NetworkBuilderHelper {
    private NetworkEventsGenerator eventGen;
    private static Map<String, Long> stateHolder = new ConcurrentHashMap<>();
    private static Map<String, String> valuesHolder = new ConcurrentHashMap<>();
    private IBaseGenerator<String> fqdnUncommonGenerator = new HostnameGenerator(DEFAULT_FQDN_END_INDEX+1,1999);
    private final long UNCOMMON_PORT_START_INDEX = (long) (DEFAULT_REGULAR_PORT_BELOW + 1);
    private Function<Long, String> uncommonIP = index -> "10."+ index + "." + DEFAULT_IP_3D_BYTE+1 + ".200";
    private Function<Integer, String> distinctIP = index -> "10."+ index + "." + DEFAULT_IP_3D_BYTE+2 + ".200";

    NetworkBuilderHelper(NetworkEventsGenerator eventGen){
        this.eventGen = eventGen;
    }

    public NetworkBuilderHelper setSSLSubjectEntityValue(String label) {
        eventGen.setSslSubjectGenerator(new FixedValueGenerator<>(label));
        return this;
    }

    public NetworkBuilderHelper setJa3EntityValue(String label) {
        eventGen.setJa3Generator(new FixedValueGenerator<>(label));
        return this;
    }

    public NetworkBuilderHelper fixSslSubject(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setSslSubjectGenerator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkBuilderHelper fixJa3(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setJa3Generator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkBuilderHelper fixDestinationOrganization(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setDestinationOrganizationGenerator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }


    public NetworkBuilderHelper nextDestinationOrganization(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        stateHolder.computeIfPresent(key,(k,v) -> v+1);
        eventGen.setDestinationOrganizationGenerator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }


    public NetworkBuilderHelper fixJa3s(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setJa3sGenerator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkBuilderHelper fixFqdn(){
        String key = valueKey.get();
        valuesHolder.putIfAbsent(key,fqdnUncommonGenerator.getNext());
        eventGen.setFqdnGenerator(new FixedValueGenerator<>(
                generatorKeyString.apply(key).replaceAll("_","-").concat(valuesHolder.get(key))));
        return this;
    }

    public NetworkBuilderHelper nextFqdn(){
        String key = valueKey.get();
        valuesHolder.putIfAbsent(key,fqdnUncommonGenerator.getNext());
        valuesHolder.computeIfPresent(key,(k,v) -> fqdnUncommonGenerator.getNext());
        eventGen.setFqdnGenerator(new FixedValueGenerator<>(
                generatorKeyString.apply(key).replaceAll("_","-").concat(valuesHolder.get(key))));
        return this;
    }

    public NetworkBuilderHelper fixSourceNetname(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setSourceNetnameGen(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkBuilderHelper nextSourceNetname(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        stateHolder.computeIfPresent(key,(k,v) -> v+1);
        eventGen.setSourceNetnameGen(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkBuilderHelper fixDestinationNetname(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setDestinationNetnameGen(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkBuilderHelper fixLocation(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setLocationGen(new FixedValueGenerator<>(new Location(generatorKeyString.apply(key),
                generatorKeyString.apply(key),generatorKeyString.apply(key))));
        return this;
    }

    public NetworkBuilderHelper nextLocation(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        stateHolder.computeIfPresent(key,(k,v) -> v+1);
        eventGen.setLocationGen(new FixedValueGenerator<>(new Location(generatorKeyString.apply(key),
                generatorKeyString.apply(key),generatorKeyString.apply(key))));
        return this;
    }

    public NetworkBuilderHelper fixDestPort(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,UNCOMMON_PORT_START_INDEX);
        eventGen.setDestinationPortGenerator(new FixedValueGenerator<>(stateHolder.get(key).intValue()));
        return this;
    }

    public NetworkBuilderHelper nextDestPort(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,UNCOMMON_PORT_START_INDEX);
        stateHolder.computeIfPresent(key,(k,v) -> v+1L);
        eventGen.setDestinationPortGenerator(new FixedValueGenerator<>(stateHolder.get(key).intValue()));
        return this;
    }

    public List<NetworkEvent> generate() throws GeneratorException {
        return eventGen.generate();
    }

    public List<NetworkEvent> generateAndAppendTo(List<NetworkEvent> eventsList) throws GeneratorException {
        eventsList.addAll(eventGen.generate());
        return eventsList;
    }

    public NetworkBuilderHelper currentUnusualSourceIp(){
        String key = "SourceIp";
        stateHolder.putIfAbsent(key,0L);
        eventGen.setSourceIpGenerator(new FixedValueGenerator<>(uncommonIP.apply(stateHolder.get(key))));
        return this;
    }

    public NetworkBuilderHelper nextUnusualSourceIp(){
        String key = "SourceIp";
        stateHolder.putIfAbsent(key,0L);
        stateHolder.computeIfPresent(key,(k,v) -> v+1);
        eventGen.setDestinationOrganizationGenerator(new FixedValueGenerator<>(uncommonIP.apply(stateHolder.get(key))));
        return this;
    }


    public NetworkBuilderHelper setDistinctSrcIps(int start, int end){
        List<Integer> list = IntStream.range(start, end).boxed().collect(Collectors.toList());
        IBaseGenerator<String> normalDistinctIps = new CyclicValuesGenerator<>(list.stream().map(e -> distinctIP.apply(e)).toArray(String[]::new));
        eventGen.setSourceIpGenerator(normalDistinctIps);
        return this;
    }




    private Function<String,String> prependTestMarker = e -> eventGen.getTestMarker()
            .concat("_")
            .concat(e);

    private Function<String,String> removeNamePrefix = e -> e
            .replace("fix","")
            .replace("next", "");

    private Function<String,String> toSnakeCase = e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e);

    private Supplier<String> methodName = () -> new Throwable().getStackTrace()[2].getMethodName();

    private Supplier<String> valueKey = () -> removeNamePrefix.andThen(prependTestMarker).andThen(toSnakeCase).apply(methodName.get());

    private Function<String,String> generatorKeyString = e -> e.concat("_").concat(String.valueOf(stateHolder.get(e)));

}

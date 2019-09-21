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

public class NetworkAnomalyHelper {
    private NetworkEventsGenerator eventGen;
    private static Map<String, Long> stateHolder = new ConcurrentHashMap<>();
    private static Map<String, String> valuesHolder = new ConcurrentHashMap<>();
    private static IBaseGenerator<String> fqdnUncommonGenerator = new HostnameGenerator(DEFAULT_FQDN_END_INDEX+1,1999);

    private final long UNCOMMON_PORT_START_INDEX = (long) (DEFAULT_REGULAR_PORT_BELOW + 1);
    private Function<Long, String> uncommonIP = index -> "10."+ index + "." + DEFAULT_IP_3D_BYTE+1 + ".200";
    private Function<Integer, String> distinctIP = index -> "10."+ index + "." + DEFAULT_IP_3D_BYTE+2 + ".200";

    NetworkAnomalyHelper(NetworkEventsGenerator eventGen){
        this.eventGen = eventGen;
    }

  /** entity fields - must be set for validation */
 public NetworkAnomalyHelper setSSLSubjectEntityValue(String label) {
        eventGen.setSslSubjectGenerator(new FixedValueGenerator<>(label));
        return this;
    }

    public NetworkAnomalyHelper setJa3EntityValue(String label) {
        eventGen.setJa3Generator(new FixedValueGenerator<>(label));
        return this;
    }

    public NetworkAnomalyHelper fixSslSubject(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setSslSubjectGenerator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkAnomalyHelper fixJa3(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setJa3Generator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }
    public NetworkAnomalyHelper nextSslSubject(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        stateHolder.computeIfPresent(key,(k,v) -> v+1);
        eventGen.setSslSubjectGenerator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkAnomalyHelper nextJa3(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        stateHolder.computeIfPresent(key,(k,v) -> v+1);
        eventGen.setJa3Generator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }


    /** constant String values - extracted from calling method name */
    public NetworkAnomalyHelper fixDestinationOrganization(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setDestinationOrganizationGenerator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }


    public NetworkAnomalyHelper nextDestinationOrganization(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        stateHolder.computeIfPresent(key,(k,v) -> v+1);
        eventGen.setDestinationOrganizationGenerator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }


    public NetworkAnomalyHelper fixJa3s(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setJa3sGenerator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkAnomalyHelper fixSslCa(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setSslCaGenerator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkAnomalyHelper fixSourceNetname(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setSourceNetnameGen(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkAnomalyHelper nextSourceNetname(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        stateHolder.computeIfPresent(key,(k,v) -> v+1);
        eventGen.setSourceNetnameGen(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkAnomalyHelper fixDestinationNetname(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setDestinationNetnameGen(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkAnomalyHelper fixLocation(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setLocationGen(new FixedValueGenerator<>(new Location(generatorKeyString.apply(key),
                generatorKeyString.apply(key),generatorKeyString.apply(key))));
        return this;
    }

    public NetworkAnomalyHelper nextLocation(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        stateHolder.computeIfPresent(key,(k,v) -> v+1);
        eventGen.setLocationGen(new FixedValueGenerator<>(new Location(generatorKeyString.apply(key),
                generatorKeyString.apply(key),generatorKeyString.apply(key))));
        return this;
    }






    /** index based constant values. the values taken from pre-defined abnormal list by running index*/
    public NetworkAnomalyHelper fixDstPort(){
        String callingMethodKey = valueKey.get();
        String fieldKey = "destPort";
        stateHolder.putIfAbsent(fieldKey,UNCOMMON_PORT_START_INDEX);
        updateForNewMethodLeaveForExisting(callingMethodKey, fieldKey);
        eventGen.setDestinationPortGenerator(new FixedValueGenerator<>(stateHolder.get(fieldKey).intValue()));
        return this;
    }

    public NetworkAnomalyHelper nextDstPort(){
        String callingMethodKey = valueKey.get();
        String fieldKey = "destPort";
        stateHolder.putIfAbsent(callingMethodKey,0L);
        stateHolder.putIfAbsent(fieldKey,UNCOMMON_PORT_START_INDEX);
        stateHolder.computeIfPresent(fieldKey,(k,v) -> v+1);
        eventGen.setDestinationPortGenerator(new FixedValueGenerator<>(stateHolder.get(fieldKey).intValue()));
        return this;
    }

    public NetworkAnomalyHelper fixSourceIp(){
        String callingMethodKey = valueKey.get();
        String fieldKey = "SourceIp";
        stateHolder.putIfAbsent(fieldKey,0L);
        updateForNewMethodLeaveForExisting(callingMethodKey, fieldKey);
        eventGen.setSourceIpGenerator(new FixedValueGenerator<>(uncommonIP.apply(stateHolder.get(fieldKey))));
        return this;
    }

    public NetworkAnomalyHelper nextSourceIp(){
        String callingMethodKey = valueKey.get();
        String fieldKey = "SourceIp";
        stateHolder.putIfAbsent(callingMethodKey,0L);
        stateHolder.putIfAbsent(fieldKey,0L);
        stateHolder.computeIfPresent(fieldKey,(k,v) -> v+1);
        eventGen.setSourceIpGenerator(new FixedValueGenerator<>(uncommonIP.apply(stateHolder.get(callingMethodKey))));
        return this;
    }


    /** string values from abnormal list by calling method name */
    public NetworkAnomalyHelper fixFqdn(){
        String callingMethodKey = valueKey.get();
        valuesHolder.putIfAbsent(callingMethodKey,fqdnUncommonGenerator.getNext());
        eventGen.setFqdnGenerator(new FixedValueGenerator<>(valuesHolder.get(callingMethodKey)));
        return this;
    }

    public NetworkAnomalyHelper nextFqdn(){
        String callingMethodKey = valueKey.get();
        valuesHolder.putIfAbsent(callingMethodKey,fqdnUncommonGenerator.getNext());
        valuesHolder.computeIfPresent(callingMethodKey,(k,v) -> fqdnUncommonGenerator.getNext());
        eventGen.setFqdnGenerator(new FixedValueGenerator<>(valuesHolder.get(callingMethodKey)));
        return this;
    }



    public NetworkAnomalyHelper setDistinctSrcIps(int start, int end){
        List<Integer> list = IntStream.range(start, end).boxed().collect(Collectors.toList());
        IBaseGenerator<String> normalDistinctIps = new CyclicValuesGenerator<>(list.stream().map(e -> distinctIP.apply(e)).toArray(String[]::new));
        eventGen.setSourceIpGenerator(normalDistinctIps);
        return this;
    }


    /** accessory methods */
    public List<NetworkEvent> generate() throws GeneratorException {
        return eventGen.generate();
    }

    public List<NetworkEvent> generateAndAppendTo(List<NetworkEvent> eventsList) throws GeneratorException {
        eventsList.addAll(eventGen.generate());
        return eventsList;
    }

    private void updateForNewMethodLeaveForExisting(String callingMethodKey, String fieldKey){
        if (!stateHolder.containsKey(callingMethodKey)) {
            stateHolder.putIfAbsent(callingMethodKey,0L);
            stateHolder.computeIfPresent(fieldKey,(k,v) -> v+1L);
        }
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

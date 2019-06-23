package presidio.data.generators.event.network;

import com.google.common.base.CaseFormat;
import presidio.data.domain.Location;
import presidio.data.domain.MachineEntity;
import presidio.data.generators.FixedValueGenerator;
import presidio.data.generators.IBaseGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkBuilderHelper {
    NetworkEventsGenerator eventGen;

    IBaseGenerator<String> testNameModifier;
    IBaseGenerator<MachineEntity> fixedMachineEntityModifier;
    IBaseGenerator<Location> fixedLocationModifier;

    private static Map<String, Long> stateHolder = new ConcurrentHashMap<>();

    public NetworkBuilderHelper(NetworkEventsGenerator eventGen){
        this.eventGen = eventGen;
    }

    private Function<String,String> removeNamePrefix = e -> e
            .replace("fix","")
            .replace("next", "");

    private Function<String,String> prependTestMarker = e -> eventGen.getTestMarker()
            .concat("_")
            .concat(e);

    private Function<String,String>  snakeCase = e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e);

    private Supplier<String> methodName = () -> new Throwable().getStackTrace()[2].getMethodName();

    private Supplier<String> valueKey = () -> removeNamePrefix.andThen(prependTestMarker).andThen(snakeCase).apply(methodName.get());

    private Function<String,String> generatorKeyString = e -> e.concat("_").concat(String.valueOf(stateHolder.get(e)));



    public NetworkBuilderHelper fixSslSubject(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setSslSubjectGenerator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkBuilderHelper nextSslSubject(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        stateHolder.computeIfPresent(key,(k,v) -> v+1);
        eventGen.setSslSubjectGenerator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkBuilderHelper fixJa3(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setJa3Generator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkBuilderHelper fixJa3s(){
        String key = valueKey.get();
        stateHolder.putIfAbsent(key,0L);
        eventGen.setJa3sGenerator(new FixedValueGenerator<>(generatorKeyString.apply(key)));
        return this;
    }

    public NetworkBuilderHelper fixSrcMachine(){
        eventGen.setSrcMachineGenerator(fixedMachineEntityModifier);
        return this;
    }

    public NetworkBuilderHelper fixDstMachine(){
        eventGen.setDstMachineGenerator(fixedMachineEntityModifier);
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
        eventGen.setLocationGen(fixedLocationModifier);
        return this;
    }

}

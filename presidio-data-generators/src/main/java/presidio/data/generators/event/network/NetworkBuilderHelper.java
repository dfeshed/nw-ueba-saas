package presidio.data.generators.event.network;

import presidio.data.domain.Location;
import presidio.data.domain.MachineEntity;
import presidio.data.generators.FixedValueGenerator;
import presidio.data.generators.IBaseGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

public class NetworkBuilderHelper {
    NetworkEventsGenerator eventGen;

    IBaseGenerator<String> fixedStringModifier;
    IBaseGenerator<MachineEntity> fixedMachineEntityModifier;
    IBaseGenerator<Location> fixedLocationModifier;

    private static Map<String, Long> stateHolder = new ConcurrentHashMap<>();

    public NetworkBuilderHelper(NetworkEventsGenerator eventGen){
        this.eventGen = eventGen;

        stateHolder.putIfAbsent(eventGen.getTestMarker(),0L);

        fixedStringModifier = new FixedValueGenerator<>(testNameToValue.apply(eventGen.getTestMarker()));

        stateHolder.computeIfPresent(eventGen.getTestMarker(),(s, aLong) -> aLong++);

    }

    private UnaryOperator<String> testNameToValue = e -> e.concat("_").concat(Long.toString(stateHolder.get(e)));


    public NetworkBuilderHelper fixSslSubject(){
        eventGen.setSslSubjectGenerator(fixedStringModifier);
        return this;
    }

    public NetworkBuilderHelper fixJa3(){
        eventGen.setJa3Generator(fixedStringModifier);
        return this;
    }

    public NetworkBuilderHelper fixJa3s(){
        eventGen.setJa3sGenerator(fixedStringModifier);
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
        eventGen.setSourceNetnameGen(fixedStringModifier);
        return this;
    }

    public NetworkBuilderHelper fixDestinationNetname(){
        eventGen.setDestinationNetnameGen(fixedStringModifier);
        return this;
    }

    public NetworkBuilderHelper fixLocation(){
        eventGen.setLocationGen(fixedLocationModifier);
        return this;
    }

}

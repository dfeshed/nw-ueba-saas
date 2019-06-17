package presidio.data.generators.event.network;

import com.google.common.base.CaseFormat;
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

    public enum NETWORK_FIELD {
        SSL_SUBJECT_GENERATOR,
        JA3_GENERATOR,
        JA3S_GENERATOR,
        DATA_SOURCE_GENERATOR,
        SRC_MACHINE_GENERATOR,
        DST_MACHINE_GENERATOR,
        SOURCE_NETNAME_GEN,
        DESTINATION_NETNAME_GEN,
        LOCATION_GEN
    }

    public NetworkBuilderHelper(NetworkEventsGenerator eventGen){
        this.eventGen = eventGen;

        stateHolder.putIfAbsent(eventGen.testMarker,0L);

        fixedStringModifier = new FixedValueGenerator<>(testNameToValue.apply(eventGen.testMarker));

        stateHolder.compute(eventGen.testMarker,(s, aLong) -> aLong++);

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

    public NetworkBuilderHelper fixDataSource(){
        eventGen.setDataSourceGenerator(fixedStringModifier);
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

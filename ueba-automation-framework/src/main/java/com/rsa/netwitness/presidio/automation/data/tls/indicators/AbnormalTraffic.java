package com.rsa.netwitness.presidio.automation.data.tls.indicators;

import com.rsa.netwitness.presidio.automation.data.tls.events_gen.UncommonValuesEventsGen;
import com.rsa.netwitness.presidio.automation.data.tls.model.EntityType;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsIndicator;
import org.assertj.core.util.Lists;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.random.GaussianLongGenerator;
import presidio.data.generators.event.tls.FieldRangeAllocator;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

import java.util.List;
import java.util.function.Function;

public class AbnormalTraffic<T> {

    private final int UNCOMMON_DATA_VALUES = 2;

    private final String entity;
    private final EntityType entityType;
    TlsIndicator indicator;
    UncommonValuesEventsGen eventsSupplier;


    public Function<T, String> contextToString = String::valueOf;

    private final IBaseGenerator<Long> regularTrafficGenerator = new GaussianLongGenerator(1e9, 10e6);

    private final IBaseGenerator<Long> unusualTrafficGenerator = new GaussianLongGenerator(1.5e9, 10e6);



    public AbnormalTraffic(String entity, EntityType type, String name, int dataPeriod, int uncommonStartDay) {
        this.entity = entity;
        indicator = new TlsIndicator(entity, type, name);
        eventsSupplier = new UncommonValuesEventsGen(dataPeriod, uncommonStartDay, name, entity, type);
        this.entityType = type;
    }




    public TlsRangeEventsGen createNormalTrafficHistoryGen(TlsRangeEventsGen initialGenCopy, FieldRangeAllocator<String> sourceGen,  FieldRangeAllocator<T> destinationGen){
        setEntity(initialGenCopy);
        initialGenCopy.setNumOfBytesSentGenerator(regularTrafficGenerator);

        TlsRangeEventsGen addSslSubjectsToDomain = initialGenCopy.copy();
        addSslSubjectsToDomain.sslSubjectGen.nextRangeRandom(5);

        indicator.addNormalValues(initialGenCopy.getNumOfBytesSentGenerator().nextValues(4, String::valueOf));
        indicator.addContext(contextToString(sourceGen, destinationGen));
        eventsSupplier.setCommonValuesGen(initialGenCopy);
        eventsSupplier.setCommonValuesGen(addSslSubjectsToDomain);

        return initialGenCopy.copy();
    }

    public TlsRangeEventsGen createHighTrafficHistoryGen(TlsRangeEventsGen initialGenCopy, FieldRangeAllocator<String> sourceGen,  FieldRangeAllocator<T> destinationGen){
        // todo: need to investigate

//        sourceGen.nextRangeGenCyclic(UNCOMMON_DATA_VALUES);
//        setEntity(initialGenCopy);
//        initialGenCopy.setNumOfBytesSentGenerator(unusualTrafficGenerator);
//
//        indicator.addAbnormalValues(initialGenCopy.getNumOfBytesSentGenerator().nextValues(4, String::valueOf));
//        eventsSupplier.setUncommonValuesHistoryGen(initialGenCopy.copy());

        return initialGenCopy.copy();
    }

    public TlsRangeEventsGen createHighTrafficAnomalyGen(TlsRangeEventsGen heightTrafficAnomalyGen, FieldRangeAllocator<String> sourceGen,  FieldRangeAllocator<T> destinationGen){
        setEntity(heightTrafficAnomalyGen);

        TlsRangeEventsGen addSslSubjectsToDomain = heightTrafficAnomalyGen.copy();
        addSslSubjectsToDomain.sslSubjectGen.nextRangeRandom(5);

        heightTrafficAnomalyGen.setNumOfBytesSentGenerator(unusualTrafficGenerator);
        eventsSupplier.setUncommonValuesAnomalyGen(heightTrafficAnomalyGen);
        eventsSupplier.setUncommonValuesAnomalyGen(addSslSubjectsToDomain);
        return heightTrafficAnomalyGen.copy();
    }

 



    public TlsRangeEventsGen createNormalTrafficHistoryGen(TlsRangeEventsGen initialGenCopy, FieldRangeAllocator<T> destinationGen){
        setEntity(initialGenCopy);
        initialGenCopy.setNumOfBytesSentGenerator(regularTrafficGenerator);

        indicator.addNormalValues(initialGenCopy.getNumOfBytesSentGenerator().nextValues(4, String::valueOf));
        indicator.addContext(destinationGen.getGenerator().getAllValuesToString(contextToString));
        eventsSupplier.setCommonValuesGen(initialGenCopy);

        return initialGenCopy.copy();
    }

    public TlsRangeEventsGen createHighTrafficHistoryGen(TlsRangeEventsGen initialGenCopy, FieldRangeAllocator<T> destinationGen){
        setEntity(initialGenCopy);
        destinationGen.nextRangeGenCyclic(UNCOMMON_DATA_VALUES);
        initialGenCopy.setNumOfBytesSentGenerator(unusualTrafficGenerator);

        indicator.addAbnormalValues(initialGenCopy.getNumOfBytesSentGenerator().nextValues(4, String::valueOf));
        eventsSupplier.setUncommonValuesHistoryGen(initialGenCopy.copy());

        return initialGenCopy.copy();
    }

    public TlsRangeEventsGen createHighTrafficAnomalyGen(TlsRangeEventsGen initialGenCopy, FieldRangeAllocator<T> destinationGen){
        setEntity(initialGenCopy);
        initialGenCopy.srcIpGenerator.nextRangeGenCyclic(2);
        initialGenCopy.setNumOfBytesSentGenerator(unusualTrafficGenerator);
        eventsSupplier.setUncommonValuesAnomalyGen(initialGenCopy);
        return initialGenCopy.copy();
    }




    public TlsRangeEventsGen createNormalTrafficHistoryGen(TlsRangeEventsGen initialGenCopy){
        setEntity(initialGenCopy);
        initialGenCopy.setNumOfBytesSentGenerator(regularTrafficGenerator);

        indicator.addNormalValues(initialGenCopy.getNumOfBytesSentGenerator().nextValues(4, String::valueOf));
        eventsSupplier.setCommonValuesGen(initialGenCopy);

        return initialGenCopy.copy();
    }

    public TlsRangeEventsGen createHighTrafficHistoryGen(TlsRangeEventsGen initialGenCopy){
        initialGenCopy.setNumOfBytesSentGenerator(unusualTrafficGenerator);
        indicator.addAbnormalValues(initialGenCopy.getNumOfBytesSentGenerator().nextValues(4, String::valueOf));
        eventsSupplier.setUncommonValuesHistoryGen(initialGenCopy.copy());
        return initialGenCopy.copy();
    }

    public TlsRangeEventsGen createHighTrafficAnomalyGen(TlsRangeEventsGen heightTrafficAnomalyGen){
        setEntity(heightTrafficAnomalyGen);
        heightTrafficAnomalyGen.srcIpGenerator.nextRangeGenCyclic(2);
        heightTrafficAnomalyGen.setNumOfBytesSentGenerator(unusualTrafficGenerator);
        eventsSupplier.setUncommonValuesAnomalyGen(heightTrafficAnomalyGen);
        return heightTrafficAnomalyGen.copy();
    }

    
    


    public TlsIndicator getIndicator() {
        indicator.setEventsGenerator(eventsSupplier);
        return indicator;
    }
    
    private void setEntity(TlsRangeEventsGen gen) {
        if (entityType.equals(EntityType.JA3)) {
            gen.ja3Gen.setConstantValueGen(entity);
        } else if (entityType.equals(EntityType.SSL_SUBJECT)) {
            gen.sslSubjectGen.setConstantValueGen(entity);
        } else  {
            throw new EnumConstantNotPresentException(entityType.getClass(), "Setter is missing for entity type " + entityType.name());
        }
    }

    private List<String> contextToString(FieldRangeAllocator<String> sourceGen,  FieldRangeAllocator<T> destinationGen) {
        List<String> sources = sourceGen.getGenerator().getAllValues();
        List<String> destinations = destinationGen.getGenerator().getAllValuesToString(contextToString);
        return Lists.list("[" + String.join(", ", sources) + "] -> [" + String.join(", ", destinations) + "]");
    }


}

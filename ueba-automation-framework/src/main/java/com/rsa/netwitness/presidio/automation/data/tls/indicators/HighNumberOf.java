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

public class HighNumberOf<T> {

    private final int UNCOMMON_DATA_VALUES = 2;

    private final String entity;
    private final EntityType entityType;
    TlsIndicator indicator;
    UncommonValuesEventsGen eventsSupplier;


    public Function<T, String> contextToString = String::valueOf;

    private final IBaseGenerator<Long> regularTrafficGenerator = new GaussianLongGenerator(1e9, 10e6);

    private final IBaseGenerator<Long> unusualTrafficGenerator = new GaussianLongGenerator(1.5e9, 10e6);



    public HighNumberOf(String entity, EntityType type, String name, int dataPeriod, int uncommonStartDay) {
        this.entity = entity;
        indicator = new TlsIndicator(entity, type, name);
        eventsSupplier = new UncommonValuesEventsGen(dataPeriod, uncommonStartDay, name, entity, type);
        this.entityType = type;
    }




    public TlsRangeEventsGen createHighNumberOfDistinctSecIpGen(TlsRangeEventsGen initialGen){
        setEntity(initialGen);
        indicator.addNormalValues(initialGen.srcIpGenerator.getGenerator().getAllValues());
        indicator.addContext(initialGen.ja3Gen.getGenerator().getAllValues());
        eventsSupplier.setCommonValuesGen(initialGen);

        initialGen.srcIpGenerator.nextRangeGenCyclic(15);
        indicator.addNormalValues(initialGen.srcIpGenerator.getGenerator().getAllValues());
        eventsSupplier.setUncommonValuesAnomalyGen(initialGen, 15);
        return initialGen.copy();
    }







    public TlsRangeEventsGen createNormalTrafficHistoryGen(TlsRangeEventsGen initialGenCopy, FieldRangeAllocator<String> sourceGen,  FieldRangeAllocator<T> destinationGen){
        setEntity(initialGenCopy);
        initialGenCopy.setNumOfBytesSentGenerator(regularTrafficGenerator);

        indicator.addNormalValues(initialGenCopy.getNumOfBytesSentGenerator().nextValues(4, String::valueOf));
        indicator.addContext(contextToString(sourceGen, destinationGen));
        eventsSupplier.setCommonValuesGen(initialGenCopy);

        return initialGenCopy.copy();
    }

    public TlsRangeEventsGen createHighTrafficHistoryGen(TlsRangeEventsGen initialGenCopy, FieldRangeAllocator<String> sourceGen,  FieldRangeAllocator<T> destinationGen){
        sourceGen.nextRangeGenCyclic(UNCOMMON_DATA_VALUES);
        setEntity(initialGenCopy);
        initialGenCopy.setNumOfBytesSentGenerator(unusualTrafficGenerator);

        indicator.addAbnormalValues(initialGenCopy.getNumOfBytesSentGenerator().nextValues(4, String::valueOf));
        eventsSupplier.setUncommonValuesHistoryGen(initialGenCopy.copy());

        return initialGenCopy.copy();
    }

    public TlsRangeEventsGen createHighTrafficAnomalyGen(TlsRangeEventsGen heightTrafficAnomalyGen, FieldRangeAllocator<String> sourceGen,  FieldRangeAllocator<T> destinationGen){
        setEntity(heightTrafficAnomalyGen);
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

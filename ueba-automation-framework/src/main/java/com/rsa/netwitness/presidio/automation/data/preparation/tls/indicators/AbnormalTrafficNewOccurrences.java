package com.rsa.netwitness.presidio.automation.data.preparation.tls.indicators;

import com.rsa.netwitness.presidio.automation.data.preparation.tls.events_gen.UncommonValuesEventsGen;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.EntityType;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.TlsIndicator;
import org.assertj.core.util.Lists;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.random.GaussianLongGenerator;
import presidio.data.generators.event.tls.FieldRangeAllocator;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

import java.util.List;
import java.util.function.Function;

public class AbnormalTrafficNewOccurrences<T> {

    private final int UNCOMMON_DATA_VALUES = 1;

    private final String entity;
    private final EntityType entityType;
    TlsIndicator indicator;
    UncommonValuesEventsGen eventsSupplier;


    public Function<T, String> contextToString = String::valueOf;

    private double BYTES_SEND = 1e9;
    private final IBaseGenerator<Long> regularTrafficGenerator = new GaussianLongGenerator(BYTES_SEND, 10e6);
    private final Function<Double, IBaseGenerator<Long>> trafficGenerator = e -> new GaussianLongGenerator(BYTES_SEND * e, 10e6);

    private final IBaseGenerator<Long> unusualTrafficGenerator = new GaussianLongGenerator(1.5e9, 10e6);



    public AbnormalTrafficNewOccurrences(String entity, EntityType type, String name, int dataPeriod, int uncommonStartDay) {
        this.entity = entity;
        indicator = new TlsIndicator(entity, type, name);
        eventsSupplier = new UncommonValuesEventsGen(dataPeriod, uncommonStartDay, name, entity, type);
        this.entityType = type;
    }


    public TlsRangeEventsGen createNormalTrafficForNewOccurrencesGen() {
        final int limitOfDaysBack = 50;
        final int distinctSourceIp = 3;

        TlsRangeEventsGen newOccurrencesGen = new TlsRangeEventsGen(limitOfDaysBack * distinctSourceIp);
        newOccurrencesGen.srcIpGenerator.nextRangeGenCyclic(distinctSourceIp);
        newOccurrencesGen.setNumOfBytesSentGenerator(regularTrafficGenerator);

        setEntity(newOccurrencesGen);
        eventsSupplier.setCommonValuesGenForNewOccurrences(newOccurrencesGen, 1440 / distinctSourceIp);
        return newOccurrencesGen.copy();
    }



    public TlsRangeEventsGen createHighTrafficAnomalyGen(TlsRangeEventsGen heightTrafficAnomalyGen){
        setEntity(heightTrafficAnomalyGen);
        heightTrafficAnomalyGen.srcIpGenerator.nextRangeGenCyclic(6);
        heightTrafficAnomalyGen.setNumOfBytesSentGenerator(unusualTrafficGenerator);
        eventsSupplier.setUncommonValuesAnomalyGen(heightTrafficAnomalyGen);
        return heightTrafficAnomalyGen.copy();
    }


    public TlsRangeEventsGen createHighTrafficFromSrcIpAnomalyGen(TlsRangeEventsGen heightTrafficAnomalyGen){
        setEntity(heightTrafficAnomalyGen);
        heightTrafficAnomalyGen.setNumOfBytesSentGenerator(trafficGenerator.apply(4d));
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

package com.rsa.netwitness.presidio.automation.data.preparation.tls.indicators;

import com.rsa.netwitness.presidio.automation.data.preparation.tls.events_gen.UncommonValuesEventsGen;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.EntityType;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.TlsIndicator;
import org.assertj.core.util.Lists;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.list.RangeGenerator;
import presidio.data.generators.common.random.GaussianLongGenerator;
import presidio.data.generators.event.tls.FieldRangeAllocator;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

import java.util.List;
import java.util.function.Function;

public class AbnormalTraffic<T> {

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



    public AbnormalTraffic(String entity, EntityType type, String name, int dataPeriod, int uncommonStartDay) {
        this.entity = entity;
        indicator = new TlsIndicator(entity, type, name);
        eventsSupplier = new UncommonValuesEventsGen(dataPeriod, uncommonStartDay, name, entity, type);
        this.entityType = type;
    }


    String abnormalIP;
    RangeGenerator<String> uncommonIpGenerator;
    String ubnormalIP1,ubnormalIP2,ubnormalIP3;

    public TlsRangeEventsGen createNormalTrafficHistoryGen(TlsRangeEventsGen initialGenCopy, FieldRangeAllocator<String> sourceGen,  FieldRangeAllocator<T> destinationGen){
        // 3 ips with regularTraffic per 1 hour

        setEntity(initialGenCopy);

        initialGenCopy.srcIpGenerator.nextRangeGenCyclic(3);

        TlsRangeEventsGen group1 = initialGenCopy.copy();
        group1.setNumOfBytesSentGenerator(regularTrafficGenerator);
        ubnormalIP1 = group1.srcIpGenerator.getGenerator().getAllValues().get(0);

        TlsRangeEventsGen group2 = initialGenCopy.copy();
        group2.sslSubjectGen.setConstantValueGen(entity + " a");
        group2.setNumOfBytesSentGenerator(trafficGenerator.apply(2d));
        ubnormalIP2 = group2.srcIpGenerator.getGenerator().getAllValues().get(0);

        TlsRangeEventsGen group3 = initialGenCopy.copy();
        group3.sslSubjectGen.setConstantValueGen(entity + " b");
        group3.setNumOfBytesSentGenerator(trafficGenerator.apply(3d));
        ubnormalIP3 = group3.srcIpGenerator.getGenerator().getAllValues().get(0);


        indicator.addNormalValues(initialGenCopy.getNumOfBytesSentGenerator().nextValues(3, String::valueOf));
        indicator.addContext(contextToString(sourceGen, destinationGen));

        eventsSupplier.setCommonValuesGen(group1, 20);
        eventsSupplier.setCommonValuesGen(group2, 20);
        eventsSupplier.setCommonValuesGen(group3, 20);

        eventsSupplier.setUncommonValuesAnomalyGen(group2, 20);
        eventsSupplier.setUncommonValuesAnomalyGen(group3, 20);

        TlsRangeEventsGen sslSubjectGen = new TlsRangeEventsGen(1);
        sslSubjectGen.sslSubjectGen.setConstantValueGen(entity);
        sslSubjectGen.srcIpGenerator.setConstantValueGen(ubnormalIP1);
        sslSubjectGen.setNumOfBytesSentGenerator(trafficGenerator.apply(10d));
        eventsSupplier.setCommonValuesGen(sslSubjectGen, 20);

        return group1.copy();
    }

    public TlsRangeEventsGen createHighTrafficAnomalyGen(TlsRangeEventsGen heightTrafficAnomalyGen, FieldRangeAllocator<String> sourceGen,  FieldRangeAllocator<T> destinationGen){
        // 1 ip with 2*regularTraffic per 1 hour
        setEntity(heightTrafficAnomalyGen);
        heightTrafficAnomalyGen.srcIpGenerator.setConstantValueGen(ubnormalIP1);
        heightTrafficAnomalyGen.setNumOfBytesSentGenerator(trafficGenerator.apply(4d));
        eventsSupplier.setUncommonValuesAnomalyGen(heightTrafficAnomalyGen, 20);
        return heightTrafficAnomalyGen.copy();
    }







    public TlsRangeEventsGen createNormalTrafficHistoryGen(TlsRangeEventsGen initialGenCopy, FieldRangeAllocator<T> destinationGen){
        // 1 ip with regularTraffic per 1 hour

        setEntity(initialGenCopy);

        initialGenCopy.setNumOfBytesSentGenerator(trafficGenerator.apply(1.5d));
        uncommonIpGenerator = initialGenCopy.srcIpGenerator.nextRangeGenCyclic(3);
        abnormalIP = uncommonIpGenerator.getNext();
        initialGenCopy.srcIpGenerator.setConstantValueGen(abnormalIP);

        indicator.addNormalValues(initialGenCopy.getNumOfBytesSentGenerator().nextValues(1, String::valueOf));
        indicator.addContext(destinationGen.getGenerator().getAllValuesToString(contextToString));
        eventsSupplier.setCommonValuesGen(initialGenCopy, 60);

        TlsRangeEventsGen initialGenCopy1 = initialGenCopy.copy();
        initialGenCopy1.sslSubjectGen.setConstantValueGen(entity + " a");
        initialGenCopy1.setNumOfBytesSentGenerator(trafficGenerator.apply(0.8d));

        TlsRangeEventsGen initialGenCopy2 = initialGenCopy.copy();
        initialGenCopy2.sslSubjectGen.setConstantValueGen(entity + " b");
        initialGenCopy2.setNumOfBytesSentGenerator(trafficGenerator.apply(0.4d));

        eventsSupplier.setCommonValuesGen(initialGenCopy1, 60);
        eventsSupplier.setCommonValuesGen(initialGenCopy2, 60);

        TlsRangeEventsGen sslSubjectGen = new TlsRangeEventsGen(1);
        sslSubjectGen.sslSubjectGen.setConstantValueGen(entity);
        sslSubjectGen.setNumOfBytesSentGenerator(trafficGenerator.apply(10d));
        eventsSupplier.setCommonValuesGen(sslSubjectGen, 60);

        return initialGenCopy.copy();
    }

    public TlsRangeEventsGen createHighTrafficAnomalyGen(TlsRangeEventsGen initialGenCopy, FieldRangeAllocator<T> destinationGen){
        // 3 ips with regularTraffic per 1 hour

        setEntity(initialGenCopy);

        initialGenCopy.srcIpGenerator.setGenerator(uncommonIpGenerator);
        initialGenCopy.setNumOfBytesSentGenerator(trafficGenerator.apply(1.5d));
        eventsSupplier.setUncommonValuesAnomalyGen(initialGenCopy, 20);

        TlsRangeEventsGen initialGenCopy1 = initialGenCopy.copy();
        initialGenCopy1.sslSubjectGen.setConstantValueGen(entity + " a");
        initialGenCopy1.setNumOfBytesSentGenerator(trafficGenerator.apply(0.8d));

        TlsRangeEventsGen initialGenCopy2 = initialGenCopy.copy();
        initialGenCopy2.sslSubjectGen.setConstantValueGen(entity + " b");
        initialGenCopy2.setNumOfBytesSentGenerator(trafficGenerator.apply(0.4d));

        eventsSupplier.setUncommonValuesAnomalyGen(initialGenCopy1, 60);
        eventsSupplier.setUncommonValuesAnomalyGen(initialGenCopy2, 60);

        return initialGenCopy.copy();
    }







    public TlsRangeEventsGen createNormalTrafficHistoryGen(TlsRangeEventsGen initialGenCopy){
        setEntity(initialGenCopy);
        initialGenCopy.setNumOfBytesSentGenerator(regularTrafficGenerator);

        indicator.addNormalValues(initialGenCopy.getNumOfBytesSentGenerator().nextValues(4, String::valueOf));
        eventsSupplier.setCommonValuesGen(initialGenCopy);

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

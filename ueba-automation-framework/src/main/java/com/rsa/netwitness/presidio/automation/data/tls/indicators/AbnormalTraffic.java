package com.rsa.netwitness.presidio.automation.data.tls.indicators;

import com.rsa.netwitness.presidio.automation.data.tls.events_gen.UncommonValuesEventsGen;
import com.rsa.netwitness.presidio.automation.data.tls.model.EntityType;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsIndicator;
import org.assertj.core.util.Lists;
import presidio.data.generators.FixedValueGenerator;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.list.RangeGenerator;
import presidio.data.generators.common.random.GaussianLongGenerator;
import presidio.data.generators.event.tls.FieldRangeAllocator;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    RangeGenerator<String> normalTrafficIpGenerator;
    List<TlsRangeEventsGen> allGenerators = new ArrayList<>();

    public TlsRangeEventsGen createNormalTrafficHistoryGen(TlsRangeEventsGen initialGenCopy, FieldRangeAllocator<String> sourceGen,  FieldRangeAllocator<T> destinationGen){
        // 9 ips with regularTraffic per 1 hour

        TlsRangeEventsGen normalGen = initialGenCopy.copy();
        setEntity(normalGen);
        normalGen.setNumOfBytesSentGenerator(regularTrafficGenerator);
        normalTrafficIpGenerator = normalGen.srcIpGenerator.nextRangeGenCyclic(9);
        double i = 0.8;

        List<String> allIps = normalTrafficIpGenerator.getAllValues();
        for (String ip : allIps) {
            TlsRangeEventsGen nextGen = normalGen.copy();
            nextGen.srcIpGenerator.setConstantValueGen(ip);
            nextGen.setNumOfBytesSentGenerator(trafficGenerator.apply(i+=0.2));
            allGenerators.add(nextGen);
        }

        String sslSubjectGroup2 =  normalGen.sslSubjectGen.nextRangeGenCyclic(1).getNext();
        String sslSubjectGroup3 =  normalGen.sslSubjectGen.nextRangeGenCyclic(1).getNext();

        IntStream.range(3, 6).boxed().forEach(j -> allGenerators.get(j).sslSubjectGen.setConstantValueGen(sslSubjectGroup2));
        IntStream.range(6, 9).boxed().forEach(j -> allGenerators.get(j).sslSubjectGen.setConstantValueGen(sslSubjectGroup3));


        allGenerators.forEach(e ->
                indicator.addNormalValues(
                        Lists.newArrayList(String.join(", ", e.srcIpGenerator.getGenerator().getAllValues()) +
                                " -> "  + e.getNumOfBytesSentGenerator().getNext())));

        indicator.addContext(allGenerators.stream().map(e -> e.sslSubjectGen.getGenerator().getAllValues().stream()).flatMap(e -> e).collect(Collectors.toList()), String::valueOf);
        allGenerators.forEach(e ->  eventsSupplier.setCommonValuesGen(e, 60));

        return initialGenCopy.copy();
    }

    public TlsRangeEventsGen createHighTrafficAnomalyGen(TlsRangeEventsGen heightTrafficAnomalyGen, FieldRangeAllocator<String> sourceGen,  FieldRangeAllocator<T> destinationGen){
        // 3 ip with 2*regularTraffic per 1 hour

        TlsRangeEventsGen firstGen = allGenerators.get(0);
        TlsRangeEventsGen secondGen = allGenerators.get(3);
        TlsRangeEventsGen thirdGen = allGenerators.get(6);

        String upnormalIp1 = firstGen.srcIpGenerator.getGenerator().getAllValues().get(0);
        String upnormalIp2 = secondGen.srcIpGenerator.getGenerator().getAllValues().get(0);
        String upnormalIp3 = thirdGen.srcIpGenerator.getGenerator().getAllValues().get(0);

        firstGen.srcIpGenerator.setConstantValueGen(upnormalIp1);
        firstGen.setNumOfBytesSentGenerator(trafficGenerator.apply(2d));

        secondGen.srcIpGenerator.setConstantValueGen(upnormalIp2);
        secondGen.setNumOfBytesSentGenerator(trafficGenerator.apply(2.6d));

        thirdGen.srcIpGenerator.setConstantValueGen(upnormalIp3);
        thirdGen.setNumOfBytesSentGenerator(trafficGenerator.apply(3.3d));


        eventsSupplier.setCommonValuesGen(firstGen, 20);
        eventsSupplier.setCommonValuesGen(secondGen, 20);
        eventsSupplier.setCommonValuesGen(thirdGen, 20);

        return heightTrafficAnomalyGen.copy();
    }



    public TlsRangeEventsGen createNormalTrafficHistoryGen(TlsRangeEventsGen initialGenCopy, FieldRangeAllocator<T> destinationGen){
        // 1 ip with 3*regularTraffic per 1 hour

        setEntity(initialGenCopy);
        initialGenCopy.setNumOfBytesSentGenerator(regularTrafficGenerator);
        uncommonIpGenerator = initialGenCopy.srcIpGenerator.nextRangeGenCyclic(3);
        abnormalIP = uncommonIpGenerator.getNext();
        initialGenCopy.srcIpGenerator.setConstantValueGen(abnormalIP);

        indicator.addNormalValues(initialGenCopy.getNumOfBytesSentGenerator().nextValues(1, String::valueOf));
        indicator.addContext(destinationGen.getGenerator().getAllValuesToString(contextToString));
        eventsSupplier.setCommonValuesGen(initialGenCopy, 60);

        TlsRangeEventsGen initialGenCopy1 = initialGenCopy.copy();
        initialGenCopy1.sslSubjectGen.setConstantValueGen(entity + " 1");
        initialGenCopy1.setNumOfBytesSentGenerator(trafficGenerator.apply(2d));

        TlsRangeEventsGen initialGenCopy2 = initialGenCopy.copy();
        initialGenCopy2.sslSubjectGen.setConstantValueGen(entity + " 2");
        initialGenCopy2.setNumOfBytesSentGenerator(trafficGenerator.apply(3d));

        eventsSupplier.setCommonValuesGen(initialGenCopy1, 60);
        eventsSupplier.setCommonValuesGen(initialGenCopy2, 60);


        return initialGenCopy.copy();
    }

    public TlsRangeEventsGen createHighTrafficAnomalyGen(TlsRangeEventsGen initialGenCopy, FieldRangeAllocator<T> destinationGen){
        // 3 ips with regularTraffic per 1 hour

        setEntity(initialGenCopy);

        initialGenCopy.srcIpGenerator.setGenerator(uncommonIpGenerator);
        initialGenCopy.setNumOfBytesSentGenerator(regularTrafficGenerator);
        eventsSupplier.setUncommonValuesAnomalyGen(initialGenCopy, 20);

        TlsRangeEventsGen initialGenCopy1 = initialGenCopy.copy();
        initialGenCopy1.sslSubjectGen.setConstantValueGen(entity + " 1");
        initialGenCopy1.setNumOfBytesSentGenerator(trafficGenerator.apply(2d));

        TlsRangeEventsGen initialGenCopy2 = initialGenCopy.copy();
        initialGenCopy2.sslSubjectGen.setConstantValueGen(entity + " 2");
        initialGenCopy2.setNumOfBytesSentGenerator(trafficGenerator.apply(3d));

        eventsSupplier.setUncommonValuesAnomalyGen(initialGenCopy1, 20);
        eventsSupplier.setUncommonValuesAnomalyGen(initialGenCopy2, 20);

        return initialGenCopy.copy();
    }


    public TlsRangeEventsGen addSslSubjectsToUncommonDomain(TlsRangeEventsGen initialGenCopy){
        initialGenCopy.setNumOfBytesSentGenerator(new FixedValueGenerator<>(100L));
        eventsSupplier.setCommonValuesGen(initialGenCopy, 60);
        eventsSupplier.setUncommonValuesAnomalyGen(initialGenCopy, 60);
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

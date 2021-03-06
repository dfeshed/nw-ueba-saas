package com.rsa.netwitness.presidio.automation.data.preparation.tls.indicators;

import com.rsa.netwitness.presidio.automation.data.preparation.tls.events_gen.UncommonValuesEventsGen;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.EntityType;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.TlsIndicator;
import org.assertj.core.util.Lists;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.list.random.RandomRangeMd5Gen;
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
        final int limitOfDaysBack = 40;
        final int distinctSourceIp = 3 * 2;

        RandomRangeMd5Gen sslSubjRandomGen = new RandomRangeMd5Gen(limitOfDaysBack * distinctSourceIp);
        sslSubjRandomGen.formatter = e -> "new_ssl_subj_" + e;

        RandomRangeMd5Gen dstOrgRandomGen = new RandomRangeMd5Gen(limitOfDaysBack * distinctSourceIp);
        dstOrgRandomGen.formatter = e -> "new_dst_org_" + e;

        TlsRangeEventsGen newOccurrencesGen = new TlsRangeEventsGen(1);
        newOccurrencesGen.sslSubjectGen.setGenerator(sslSubjRandomGen);
        newOccurrencesGen.ja3Gen.nextRangeGenCyclic(limitOfDaysBack * distinctSourceIp);
        newOccurrencesGen.dstOrgGen.setGenerator(dstOrgRandomGen);
        newOccurrencesGen.dstPortGen.nextRangeGenCyclic(limitOfDaysBack * distinctSourceIp);
        newOccurrencesGen.hostnameGen.nextRangeGenCyclic(limitOfDaysBack * distinctSourceIp);
        newOccurrencesGen.setNumOfBytesSentGenerator(regularTrafficGenerator);

        TlsRangeEventsGen nextIp1 = newOccurrencesGen.copy();
        nextIp1.srcIpGenerator.nextRangeGenCyclic(1);
        TlsRangeEventsGen nextIp2 = newOccurrencesGen.copy();
        nextIp2.srcIpGenerator.nextRangeGenCyclic(1);

        // 2 events per day x 3 ips ;  total daily traffic = 2*10^9 * 3 IPs
        eventsSupplier.setCommonValuesGenForNewOccurrences(newOccurrencesGen, 480);
        eventsSupplier.setCommonValuesGenForNewOccurrences(nextIp1, 480);
        eventsSupplier.setCommonValuesGenForNewOccurrences(nextIp2, 480);

        return newOccurrencesGen.copy();
    }


    public TlsRangeEventsGen negativeHighTrafficAnomalyGen(TlsRangeEventsGen heightTrafficAnomalyGen){
        setEntity(heightTrafficAnomalyGen);
        // 6 events from 6 ips in anomaly day ;  total daily traffic = 6 * 10^9  (daily regular traffic)
        heightTrafficAnomalyGen.srcIpGenerator.nextRangeGenCyclic(6);
        heightTrafficAnomalyGen.setNumOfBytesSentGenerator(regularTrafficGenerator);
        eventsSupplier.setUncommonValuesAnomalyGen(heightTrafficAnomalyGen, 480 / 6);
        return heightTrafficAnomalyGen.copy();
    }


    public TlsRangeEventsGen createHighTrafficAnomalyGen(TlsRangeEventsGen heightTrafficAnomalyGen){
        setEntity(heightTrafficAnomalyGen);
        // 6 events from 6 ips in anomaly day ;  total daily traffic = 6 * 2 * 10^9  (x2 more then daily regular traffic)
        heightTrafficAnomalyGen.srcIpGenerator.nextRangeGenCyclic(6);
        heightTrafficAnomalyGen.setNumOfBytesSentGenerator(trafficGenerator.apply(2d));
        eventsSupplier.setUncommonValuesAnomalyGen(heightTrafficAnomalyGen, 480 / 6);
        return heightTrafficAnomalyGen.copy();
    }


    public TlsRangeEventsGen createHighTrafficFromSrcIpAnomalyGen(TlsRangeEventsGen heightTrafficAnomalyGen){
        setEntity(heightTrafficAnomalyGen);
        // 2 events per day x 1 ips ;  total daily traffic = 2 * 10^9 * 3
        heightTrafficAnomalyGen.srcIpGenerator.nextRangeGenCyclic(1);
        heightTrafficAnomalyGen.setNumOfBytesSentGenerator(trafficGenerator.apply(6d));
        eventsSupplier.setUncommonValuesAnomalyGen(heightTrafficAnomalyGen,480);
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

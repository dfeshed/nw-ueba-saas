package com.rsa.netwitness.presidio.automation.data.tls.model;

import com.rsa.netwitness.presidio.automation.data.tls.events.EntityHistoricalDataSupplier;
import com.rsa.netwitness.presidio.automation.data.tls.events.UncommonValuesEventsSupplier;
import com.rsa.netwitness.presidio.automation.data.tls.feilds.TlsEventsGen;
import presidio.data.domain.Location;
import presidio.data.generators.IBaseGenerator;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ja3TlsAlertBuilder extends TlsAlert {

    private final int ENTITY_HISTORICAL_DATA_VALUES = 5;
    private final int HISTORICAL_DATA_COMMON_VALUES = 2;
    private final int UNCOMMON_DATA_VALUES = 2;
    private final int dataPeriod;
    private final int uncommonStartDay;

    private Supplier<TlsEventsGen> ja3HistoryGenCopy;


    public Ja3TlsAlertBuilder(String entity, int dataPeriod, int uncommonStartDay) {
        super(entity, "ja3");
        this.dataPeriod = dataPeriod;
        this.uncommonStartDay = uncommonStartDay;
        setJa3HistoricalData();
    }



    private void setJa3HistoricalData() {
        final TlsEventsGen ja3HistoryGen = new TlsEventsGen(ENTITY_HISTORICAL_DATA_VALUES);
        ja3HistoryGen.setConstantValueJa3(entity);
        TlsIndicator indicator = new TlsIndicator(entity, "ja3", "entity_history");
        indicator.setEventsGenerator(new EntityHistoricalDataSupplier(ja3HistoryGen.copy()));
        indicators.add(indicator);
        this.ja3HistoryGenCopy = ja3HistoryGen::copy;
    }


    public Ja3TlsAlertBuilder ja3_abnormal_dst_org_for_src_netname_outbound() {
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        IBaseGenerator<String> keyGen = commonEventsGen.getSourceNetnameGen();
        IBaseGenerator<String> commonValuesGen = commonEventsGen.getDestinationOrganizationGenerator();

        TlsEventsGen uncommonValuesHistoryGen = new TlsEventsGen(UNCOMMON_DATA_VALUES);
        IBaseGenerator<String> abnormalValuesGen = uncommonValuesHistoryGen.getDestinationOrganizationGenerator();

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueJa3(entity);
        uncommonGen.setSourceNetnameGen(keyGen);
        uncommonGen.setDestinationOrganizationGenerator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(entity, "ja3", name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES));

        UncommonValuesEventsSupplier eventsSupplier = new UncommonValuesEventsSupplier(dataPeriod, uncommonStartDay)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        indicators.add(indicator);
        return this;
    }

    public Ja3TlsAlertBuilder ja3_abnormal_country_for_ssl_subject_outbound() {
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        IBaseGenerator<String> keyGen = commonEventsGen.getSslSubjectGenerator();
        IBaseGenerator<Location> commonValuesGen = commonEventsGen.getLocationGen();

        TlsEventsGen uncommonValuesHistoryGen = new TlsEventsGen(UNCOMMON_DATA_VALUES);
        IBaseGenerator<Location> abnormalValuesGen = uncommonValuesHistoryGen.getLocationGen();

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueJa3(entity);
        uncommonGen.setSslSubjectGenerator(keyGen);
        uncommonGen.setLocationGen(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(entity, "ja3", name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES).stream().map(Location::getCountry).collect(Collectors.toList()));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES).stream().map(Location::getCountry).collect(Collectors.toList()));

        UncommonValuesEventsSupplier eventsSupplier = new UncommonValuesEventsSupplier(dataPeriod, uncommonStartDay)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        indicators.add(indicator);
        return this;
    }

    public Ja3TlsAlertBuilder ja3_abnormal_dst_port_for_dst_org_outbound() {
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        IBaseGenerator<Integer> commonValuesGen = commonEventsGen.getDestinationPortGenerator();

        TlsEventsGen uncommonValuesHistoryGen = new TlsEventsGen(UNCOMMON_DATA_VALUES);
        IBaseGenerator<String> keyGen = commonEventsGen.getDestinationOrganizationGenerator();
        IBaseGenerator<Integer> abnormalValuesGen = uncommonValuesHistoryGen.getDestinationPortGenerator();

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueJa3(entity);
        uncommonGen.setDestinationOrganizationGenerator(keyGen);
        uncommonGen.setDestinationPortGenerator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(entity, "ja3", name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES).stream().map(String::valueOf).collect(Collectors.toList()));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES).stream().map(String::valueOf).collect(Collectors.toList()));

        UncommonValuesEventsSupplier eventsSupplier = new UncommonValuesEventsSupplier(dataPeriod, uncommonStartDay)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        indicators.add(indicator);
        return this;
    }


    public Ja3TlsAlertBuilder abnormal_ja3_for_source_netname_outbound() {
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        IBaseGenerator<String> keyGen = commonEventsGen.getSourceNetnameGen();
        IBaseGenerator<String> commonValuesGen = commonEventsGen.getJa3Generator();

        // ja3 history is created by setJa3HistoricalData()

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueJa3(entity);
        uncommonGen.setSourceNetnameGen(keyGen);

        TlsIndicator indicator = new TlsIndicator(entity, "ja3", name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(uncommonGen.getJa3Generator(), UNCOMMON_DATA_VALUES));

        UncommonValuesEventsSupplier eventsSupplier = new UncommonValuesEventsSupplier(dataPeriod, uncommonStartDay)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen);

        indicator.setEventsGenerator(eventsSupplier);
        indicators.add(indicator);
        return this;
    }

    public Ja3TlsAlertBuilder ja3_abnormal_ssl_subject_for_ja3_outbound() {
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        indicatorNames.add(name);

        IBaseGenerator<String> commonValuesGen = ja3HistoryGenCopy.get().getSslSubjectGenerator();

        TlsEventsGen uncommonValuesHistoryGen = new TlsEventsGen(UNCOMMON_DATA_VALUES);
        IBaseGenerator<String> abnormalValuesGen = uncommonValuesHistoryGen.getSslSubjectGenerator();

        TlsEventsGen uncommonGen = ja3HistoryGenCopy.get();
        uncommonGen.setConstantValueJa3(entity);
        uncommonGen.setSslSubjectGenerator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(entity, "ja3", name);
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES));

        UncommonValuesEventsSupplier eventsSupplier = new UncommonValuesEventsSupplier(dataPeriod, uncommonStartDay)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        indicators.add(indicator);
        return this;
    }

    public Ja3TlsAlertBuilder abnormal_domain_for_ja3_outbound() {
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        indicatorNames.add(name);

        IBaseGenerator<String> commonValuesGen = ja3HistoryGenCopy.get().getFqdnGenerator();

        TlsEventsGen uncommonValuesHistoryGen = new TlsEventsGen(UNCOMMON_DATA_VALUES);
        IBaseGenerator<String> abnormalValuesGen = uncommonValuesHistoryGen.getFqdnGenerator();

        TlsEventsGen uncommonGen = ja3HistoryGenCopy.get();
        uncommonGen.setConstantValueJa3(entity);
        uncommonGen.setFqdnGenerator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(entity, "ja3", name);
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES));

        UncommonValuesEventsSupplier eventsSupplier = new UncommonValuesEventsSupplier(dataPeriod, uncommonStartDay)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        indicators.add(indicator);
        return this;
    }


    public <T> List<T> getValues(IBaseGenerator<T> generator, int numOfValues) {
        return IntStream.range(0, numOfValues).boxed().map(e -> generator.getNext()).collect(Collectors.toList());
    }

}

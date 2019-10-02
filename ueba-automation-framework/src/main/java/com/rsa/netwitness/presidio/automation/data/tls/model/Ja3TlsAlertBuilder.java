package com.rsa.netwitness.presidio.automation.data.tls.model;

import com.rsa.netwitness.presidio.automation.data.tls.events.EntityHistoricalDataSupplier;
import com.rsa.netwitness.presidio.automation.data.tls.events.UncommonValuesEventsSupplier;
import com.rsa.netwitness.presidio.automation.data.tls.feilds.TlsEventsGen;
import presidio.data.domain.Location;
import presidio.data.generators.IBaseGenerator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ja3TlsAlertBuilder extends TlsAlert {

    private final int ENTITY_HISTORICAL_DATA_VALUES = 5;
    private final int HISTORICAL_DATA_COMMON_VALUES = 2;
    private final int UNCOMMON_DATA_VALUES = 2;


    public Ja3TlsAlertBuilder(String entity) {
        super(entity, "ja3");
        setJa3HistoricalData();
    }



    private void setJa3HistoricalData() {
        TlsEventsGen ja3HistoryGen = new TlsEventsGen(ENTITY_HISTORICAL_DATA_VALUES);
        ja3HistoryGen.setConstantValueJa3(entity);
        TlsIndicator indicator = new TlsIndicator(entity, "ja3", "entity_history");
        indicator.setEventsGenerator(new EntityHistoricalDataSupplier(ja3HistoryGen));
        indicators.add(indicator);
    }


    public Ja3TlsAlertBuilder ja3_abnormal_dst_org_for_src_netname_outbound() {

        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        indicatorNames.add(name);

        TlsEventsGen historicalDataGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        IBaseGenerator<String> normalValuesGen = historicalDataGen.getDestinationOrganizationGenerator();

        TlsEventsGen uncommonGen = historicalDataGen.copy();
        uncommonGen.setConstantValueJa3(entity);
        uncommonGen.nextDstOrgGenerator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<String> abnormalValuesGen = uncommonGen.getDestinationOrganizationGenerator();

        TlsIndicator indicator = new TlsIndicator(entity, "ja3", name);
        indicator.addNormalValues(getValues(normalValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES));

        indicator.setEventsGenerator(new UncommonValuesEventsSupplier(historicalDataGen, uncommonGen));
        indicators.add(indicator);
        return this;
    }

    public Ja3TlsAlertBuilder ja3_abnormal_country_for_ssl_subject_outbound() {
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        indicatorNames.add(name);

        TlsEventsGen historicalDataGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        IBaseGenerator<Location> normalValuesGen = historicalDataGen.getLocationGen();

        TlsEventsGen uncommonGen = historicalDataGen.copy();
        uncommonGen.setConstantValueJa3(entity);
        uncommonGen.nextLocationGenerator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<Location> abnormalValuesGen = uncommonGen.getLocationGen();

        TlsIndicator indicator = new TlsIndicator(entity, "ja3", name);
        indicator.addNormalValues(getValues(normalValuesGen, HISTORICAL_DATA_COMMON_VALUES).stream().map(Location::getCountry).collect(Collectors.toList()));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES).stream().map(Location::getCountry).collect(Collectors.toList()));

        indicator.setEventsGenerator(new UncommonValuesEventsSupplier(historicalDataGen, uncommonGen));
        indicators.add(indicator);
        return this;
    }

    public Ja3TlsAlertBuilder ja3_abnormal_dst_port_for_dst_org_outbound() {
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        indicatorNames.add(name);

        TlsEventsGen historicalDataGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        IBaseGenerator<Integer> normalValuesGen = historicalDataGen.getDestinationPortGenerator();

        TlsEventsGen uncommonGen = historicalDataGen.copy();
        uncommonGen.setConstantValueJa3(entity);
        uncommonGen.nextDstPortGenerator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<Integer> abnormalValuesGen = uncommonGen.getDestinationPortGenerator();

        TlsIndicator indicator = new TlsIndicator(entity, "ja3", name);
        indicator.addNormalValues(getValues(normalValuesGen, HISTORICAL_DATA_COMMON_VALUES).stream().map(String::valueOf).collect(Collectors.toList()));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES).stream().map(String::valueOf).collect(Collectors.toList()));

        indicator.setEventsGenerator(new UncommonValuesEventsSupplier(historicalDataGen, uncommonGen));
        indicators.add(indicator);
        return this;
    }


    public Ja3TlsAlertBuilder abnormal_ja3_for_source_netname_outbound() {
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        indicatorNames.add(name);

        TlsEventsGen historicalDataGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        IBaseGenerator<String> normalValuesGen = historicalDataGen.getJa3Generator();

        TlsEventsGen uncommonGen = historicalDataGen.copy();
        uncommonGen.setConstantValueJa3(entity);
        IBaseGenerator<String> abnormalValuesGen = uncommonGen.getJa3Generator();

        TlsIndicator indicator = new TlsIndicator(entity, "ja3", name);
        indicator.addNormalValues(getValues(normalValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES));

        indicator.setEventsGenerator(new UncommonValuesEventsSupplier(historicalDataGen, uncommonGen));
        indicators.add(indicator);
        return this;
    }

    public Ja3TlsAlertBuilder ja3_abnormal_ssl_subject_for_ja3_outbound() {
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        indicatorNames.add(name);

        TlsEventsGen historicalDataGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        IBaseGenerator<String> normalValuesGen = historicalDataGen.getSslSubjectGenerator();

        TlsEventsGen uncommonGen = historicalDataGen.copy();

        uncommonGen.nextDstOrgGenerator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<String> abnormalValuesGen = uncommonGen.getSslSubjectGenerator();
        uncommonGen.setConstantValueJa3(entity);

        TlsIndicator indicator = new TlsIndicator(entity, "ja3", name);
        indicator.addNormalValues(getValues(normalValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES));

        indicator.setEventsGenerator(new UncommonValuesEventsSupplier(historicalDataGen, uncommonGen));
        indicators.add(indicator);
        return this;
    }

    public Ja3TlsAlertBuilder abnormal_domain_for_ja3_outbound() {
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        indicatorNames.add(name);

        TlsEventsGen domainHistory = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        IBaseGenerator<String> normalValuesGen = domainHistory.getFqdnGenerator();

        TlsEventsGen uncommonGen = domainHistory.copy();
        uncommonGen.setConstantValueJa3(entity);
        uncommonGen.nextFqdnGenerator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<String> abnormalValuesGen = uncommonGen.getFqdnGenerator();

        TlsIndicator indicator = new TlsIndicator(entity, "ja3", name);
        indicator.addNormalValues(getValues(normalValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES));

        indicator.setEventsGenerator(new UncommonValuesEventsSupplier(domainHistory, uncommonGen));
        indicators.add(indicator);
        return this;
    }


    public <T> List<T> getValues(IBaseGenerator<T> generator, int numOfValues) {
        return IntStream.range(0, numOfValues).boxed().map(e -> generator.getNext()).collect(Collectors.toList());
    }

}

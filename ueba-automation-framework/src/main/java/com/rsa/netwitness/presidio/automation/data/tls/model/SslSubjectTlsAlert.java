package com.rsa.netwitness.presidio.automation.data.tls.model;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.data.tls.events_gen.UncommonValueIndicatorEventsGen;
import com.rsa.netwitness.presidio.automation.data.tls.events_gen.UnregularHoursIndicatorEventsGen;
import com.rsa.netwitness.presidio.automation.data.tls.feilds_gen.TlsEventsGen;
import org.slf4j.LoggerFactory;
import presidio.data.domain.Location;
import presidio.data.generators.IBaseGenerator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SslSubjectTlsAlert {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(SslSubjectTlsAlert.class);

    private final TlsAlert alert;

    private final int HISTORICAL_DATA_COMMON_VALUES = 2;
    private final int UNCOMMON_DATA_VALUES = 2;
    private final int dataPeriod;
    private final int uncommonStartDay;
    private final String TYPE = "ssl_subject";

    public SslSubjectTlsAlert(String entity, int dataPeriod, int uncommonStartDay) {
        alert = new TlsAlert(entity, TYPE);
        this.dataPeriod = dataPeriod;
        this.uncommonStartDay = uncommonStartDay;
    }

    public TlsAlert create() {
        return alert;
    }



    public SslSubjectTlsAlert ssl_subject_abnormal_ja3_day_time() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen abnormalValueGen = new TlsEventsGen(1).setConstantValueSslSubject(alert.entity);

        TlsEventsGen historyGen = abnormalValueGen.copy();
        historyGen.nextJa3Generator(1);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        UnregularHoursIndicatorEventsGen eventsSupplier = new UnregularHoursIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setRegularHoursHistoryGen(abnormalValueGen)
                .setUnregularHoursHistoryGenJa3(historyGen)
                .setAnomalyDayUnregularHoursGenJa3(abnormalValueGen);

        indicator.unregularHoursStartTime = eventsSupplier.getUnregularStartTimeSslSubject();
        indicator.setEventsGenerator(eventsSupplier);
        indicator.addNormalValues(getValues(historyGen.getSslSubjectGenerator(), 1));
        indicator.addAbnormalValues(getValues(abnormalValueGen.getSslSubjectGenerator(), 1));
        alert.indicators.add(indicator);
        return this;
    }

    public SslSubjectTlsAlert ssl_subject_abnormal_ssl_subject_day_time() {

        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen abnormalValueGen = new TlsEventsGen(1).setConstantValueSslSubject(alert.entity);

        TlsEventsGen historyGen = abnormalValueGen.copy();
        historyGen.nextSslSubjectGenerator(1);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        UnregularHoursIndicatorEventsGen eventsSupplier = new UnregularHoursIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setRegularHoursHistoryGen(abnormalValueGen)
                .setUnregularHoursHistoryGenSslSubject(historyGen)
                .setAnomalyDayUnregularHoursGenSslSubject(abnormalValueGen);

        indicator.unregularHoursStartTime = eventsSupplier.getUnregularStartTimeJa3();

        indicator.setEventsGenerator(eventsSupplier);
        indicator.addNormalValues(getValues(historyGen.getJa3Generator(), 1));
        indicator.addAbnormalValues(getValues(abnormalValueGen.getJa3Generator(), 1));
        alert.indicators.add(indicator);
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_dst_org_for_src_netname_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES).setConstantValueSslSubject(alert.entity);
        IBaseGenerator<String> keyGen = commonEventsGen.getSourceNetnameGen();
        IBaseGenerator<String> commonValuesGen = commonEventsGen.getDestinationOrganizationGenerator();

        TlsEventsGen uncommonValuesHistoryGen = commonEventsGen.copy();
        uncommonValuesHistoryGen.nextSrcNetnameGenerator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<String> abnormalValuesGen = uncommonValuesHistoryGen.nextDstOrgGenerator(UNCOMMON_DATA_VALUES);
        uncommonValuesHistoryGen.setDestinationOrganizationGenerator(abnormalValuesGen);

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueSslSubject(alert.entity);
        uncommonGen.setSourceNetnameGen(keyGen);
        uncommonGen.setDestinationOrganizationGenerator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES));

        UncommonValueIndicatorEventsGen eventsSupplier = new UncommonValueIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        alert.indicators.add(indicator);
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_ssl_subject_for_src_netname_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        IBaseGenerator<String> keyGen = commonEventsGen.getSourceNetnameGen();
        IBaseGenerator<String> commonValuesGen = commonEventsGen.getSslSubjectGenerator();

        TlsEventsGen uncommonValuesHistoryGen = commonEventsGen.copy();
        uncommonValuesHistoryGen.nextSrcNetnameGenerator(UNCOMMON_DATA_VALUES);
        uncommonValuesHistoryGen.setConstantValueSslSubject(alert.entity);
        IBaseGenerator<String> abnormalValuesGen = uncommonValuesHistoryGen.getSslSubjectGenerator();

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueSslSubject(alert.entity);
        uncommonGen.setSourceNetnameGen(keyGen);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES));

        UncommonValueIndicatorEventsGen eventsSupplier = new UncommonValueIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        alert.indicators.add(indicator);
        return this;
    }

    public SslSubjectTlsAlert ssl_subject_abnormal_domain_for_src_netname_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES).setConstantValueSslSubject(alert.entity);
        IBaseGenerator<String> keyGen = commonEventsGen.getSourceNetnameGen();
        IBaseGenerator<String> commonValuesGen = commonEventsGen.getFqdnGenerator();

        TlsEventsGen uncommonValuesHistoryGen = commonEventsGen.copy();
        uncommonValuesHistoryGen.nextSrcNetnameGenerator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<String> abnormalValuesGen = uncommonValuesHistoryGen.nextFqdnGenerator(UNCOMMON_DATA_VALUES);

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueSslSubject(alert.entity);
        uncommonGen.setSourceNetnameGen(keyGen);
        uncommonGen.setFqdnGenerator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES));

        UncommonValueIndicatorEventsGen eventsSupplier = new UncommonValueIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        alert.indicators.add(indicator);
        return this;
    }

    public SslSubjectTlsAlert ssl_subject_abnormal_dst_port_for_src_netname_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES).setConstantValueSslSubject(alert.entity);
        IBaseGenerator<String> keyGen = commonEventsGen.getSourceNetnameGen();
        IBaseGenerator<Integer> commonValuesGen = commonEventsGen.getDestinationPortGenerator();

        TlsEventsGen uncommonValuesHistoryGen = commonEventsGen.copy();
        uncommonValuesHistoryGen.nextSrcNetnameGenerator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<Integer> abnormalValuesGen = uncommonValuesHistoryGen.nextDstPortGenerator(UNCOMMON_DATA_VALUES);

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueSslSubject(alert.entity);
        uncommonGen.setSourceNetnameGen(keyGen);
        uncommonGen.setDestinationPortGenerator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES), String::valueOf);
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES), String::valueOf);

        UncommonValueIndicatorEventsGen eventsSupplier = new UncommonValueIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        alert.indicators.add(indicator);
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_dst_port_for_domain_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES).setConstantValueSslSubject(alert.entity);
        IBaseGenerator<String> keyGen = commonEventsGen.getFqdnGenerator();
        IBaseGenerator<Integer> commonValuesGen = commonEventsGen.getDestinationPortGenerator();

        TlsEventsGen uncommonValuesHistoryGen = commonEventsGen.copy();
        uncommonValuesHistoryGen.nextFqdnGenerator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<Integer> abnormalValuesGen = uncommonValuesHistoryGen.nextDstPortGenerator(UNCOMMON_DATA_VALUES);

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueSslSubject(alert.entity);
        uncommonGen.setFqdnGenerator(keyGen);
        uncommonGen.setDestinationPortGenerator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES), String::valueOf);
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES), String::valueOf);

        UncommonValueIndicatorEventsGen eventsSupplier = new UncommonValueIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        alert.indicators.add(indicator);
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_dst_port_for_ssl_subject_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES).setConstantValueSslSubject(alert.entity);
        IBaseGenerator<String> keyGen = commonEventsGen.getSslSubjectGenerator();
        IBaseGenerator<Integer> commonValuesGen = commonEventsGen.getDestinationPortGenerator();

        TlsEventsGen uncommonValuesHistoryGen = commonEventsGen.copy();
        uncommonValuesHistoryGen.nextSslSubjectGenerator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<Integer> abnormalValuesGen = uncommonValuesHistoryGen.nextDstPortGenerator(UNCOMMON_DATA_VALUES);

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueSslSubject(alert.entity);
        uncommonGen.setDestinationPortGenerator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES), String::valueOf);
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES), String::valueOf);

        UncommonValueIndicatorEventsGen eventsSupplier = new UncommonValueIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        alert.indicators.add(indicator);
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_country_for_ssl_subject_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        commonEventsGen.setConstantValueSslSubject(alert.entity);
        IBaseGenerator<String> keyGen = commonEventsGen.getSslSubjectGenerator();
        IBaseGenerator<Location> commonValuesGen = commonEventsGen.getLocationGen();

        TlsEventsGen uncommonValuesHistoryGen = commonEventsGen.copy();
        uncommonValuesHistoryGen.nextSslSubjectGenerator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<Location> abnormalValuesGen = uncommonValuesHistoryGen.nextLocationGenerator(UNCOMMON_DATA_VALUES);

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueSslSubject(alert.entity);
        uncommonGen.setLocationGen(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES), Location::getCountry);
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES), Location::getCountry);

        UncommonValueIndicatorEventsGen eventsSupplier = new UncommonValueIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        alert.indicators.add(indicator);
        return this;
    }

    public SslSubjectTlsAlert ssl_subject_abnormal_dst_port_for_dst_org_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES).setConstantValueSslSubject(alert.entity);
        IBaseGenerator<String> keyGen = commonEventsGen.getDestinationOrganizationGenerator();
        IBaseGenerator<Integer> commonValuesGen = commonEventsGen.getDestinationPortGenerator();

        TlsEventsGen uncommonValuesHistoryGen = commonEventsGen.copy();
        uncommonValuesHistoryGen.nextDstOrgGenerator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<Integer> abnormalValuesGen = uncommonValuesHistoryGen.nextDstPortGenerator(UNCOMMON_DATA_VALUES);

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueSslSubject(alert.entity);
        uncommonGen.setDestinationOrganizationGenerator(keyGen);
        uncommonGen.setDestinationPortGenerator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES), String::valueOf);
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES), String::valueOf);

        UncommonValueIndicatorEventsGen eventsSupplier = new UncommonValueIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        alert.indicators.add(indicator);
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_ja3_for_source_netname_outbound() {

        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES).setConstantValueSslSubject(alert.entity);
        IBaseGenerator<String> keyGen = commonEventsGen.getSourceNetnameGen();
        IBaseGenerator<String> commonValuesGen = commonEventsGen.getJa3Generator();

        TlsEventsGen uncommonValuesHistoryGen = commonEventsGen.copy();
        uncommonValuesHistoryGen.nextSrcNetnameGenerator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<String> abnormalValuesGen = uncommonValuesHistoryGen.nextJa3Generator(UNCOMMON_DATA_VALUES);

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueSslSubject(alert.entity);
        uncommonGen.setSourceNetnameGen(keyGen);
        uncommonGen.setJa3Generator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES));

        UncommonValueIndicatorEventsGen eventsSupplier = new UncommonValueIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        alert.indicators.add(indicator);
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_ssl_subject_for_ja3_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        IBaseGenerator<String> keyGen = commonEventsGen.getJa3Generator();
        IBaseGenerator<String> commonValuesGen = commonEventsGen.getSslSubjectGenerator();

        TlsEventsGen uncommonValuesHistoryGen = commonEventsGen.copy();
        uncommonValuesHistoryGen.setConstantValueSslSubject(alert.entity);
        uncommonValuesHistoryGen.nextJa3Generator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<String> abnormalValuesGen = uncommonValuesHistoryGen.getSslSubjectGenerator();

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setJa3Generator(keyGen);
        uncommonGen.setSslSubjectGenerator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES));

        UncommonValueIndicatorEventsGen eventsSupplier = new UncommonValueIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        alert.indicators.add(indicator);
        return this;
    }

    public SslSubjectTlsAlert ssl_subject_abnormal_domain_for_ja3_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        commonEventsGen.setConstantValueSslSubject(alert.entity);
        IBaseGenerator<String> keyGen = commonEventsGen.getJa3Generator();
        IBaseGenerator<String> commonValuesGen = commonEventsGen.getFqdnGenerator();

        TlsEventsGen uncommonValuesHistoryGen = commonEventsGen.copy();
        uncommonValuesHistoryGen.nextJa3Generator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<String> abnormalValuesGen = uncommonValuesHistoryGen.nextFqdnGenerator(UNCOMMON_DATA_VALUES);

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueSslSubject(alert.entity);
        uncommonGen.setConstantValueJa3(alert.entity);
        uncommonGen.setFqdnGenerator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES));

        UncommonValueIndicatorEventsGen eventsSupplier = new UncommonValueIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        alert.indicators.add(indicator);
        return this;
    }

    public SslSubjectTlsAlert ssl_subject_abnormal_dst_port_for_ja3_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsEventsGen commonEventsGen = new TlsEventsGen(HISTORICAL_DATA_COMMON_VALUES).setConstantValueSslSubject(alert.entity);
        IBaseGenerator<String> keyGen = commonEventsGen.getJa3Generator();
        IBaseGenerator<Integer> commonValuesGen = commonEventsGen.getDestinationPortGenerator();

        TlsEventsGen uncommonValuesHistoryGen = commonEventsGen.copy();
        uncommonValuesHistoryGen.nextJa3Generator(UNCOMMON_DATA_VALUES);
        IBaseGenerator<Integer> abnormalValuesGen = uncommonValuesHistoryGen.nextDstPortGenerator(UNCOMMON_DATA_VALUES);

        TlsEventsGen uncommonGen = commonEventsGen.copy();
        uncommonGen.setConstantValueSslSubject(alert.entity);
        uncommonGen.setJa3Generator(keyGen);
        uncommonGen.setDestinationPortGenerator(abnormalValuesGen);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        indicator.addKeys(getValues(keyGen, HISTORICAL_DATA_COMMON_VALUES));
        indicator.addNormalValues(getValues(commonValuesGen, HISTORICAL_DATA_COMMON_VALUES), String::valueOf);
        indicator.addAbnormalValues(getValues(abnormalValuesGen, UNCOMMON_DATA_VALUES), String::valueOf);

        UncommonValueIndicatorEventsGen eventsSupplier = new UncommonValueIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE)
                .setCommonValuesGen(commonEventsGen)
                .setUncommonValuesAnomalyGen(uncommonGen)
                .setUncommonValuesHistoryGen(uncommonValuesHistoryGen);

        indicator.setEventsGenerator(eventsSupplier);
        alert.indicators.add(indicator);
        return this;
    }


    private <T> List<T> getValues(IBaseGenerator<T> generator, int numOfValues) {
        return IntStream.range(0, numOfValues).boxed().map(e -> generator.getNext()).collect(Collectors.toList());
    }

}

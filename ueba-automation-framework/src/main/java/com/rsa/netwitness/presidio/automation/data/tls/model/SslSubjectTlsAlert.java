package com.rsa.netwitness.presidio.automation.data.tls.model;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.data.tls.events_gen.UnregularHoursIndicatorEventsGen;
import com.rsa.netwitness.presidio.automation.data.tls.indicators.AbnormalTraffic;
import com.rsa.netwitness.presidio.automation.data.tls.indicators.UncommonEntityForContext;
import com.rsa.netwitness.presidio.automation.data.tls.indicators.UncommonValueForContext;
import com.rsa.netwitness.presidio.automation.data.tls.indicators.UncommonValueForEntity;
import org.slf4j.LoggerFactory;
import presidio.data.domain.Location;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

public class SslSubjectTlsAlert {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(SslSubjectTlsAlert.class);

    private final TlsAlert alert;

    private final int HISTORICAL_DATA_COMMON_VALUES = 3;
    private final int dataPeriod;
    private final int uncommonStartDay;
    private final EntityType TYPE = EntityType.SSL_SUBJECT;

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

        TlsRangeEventsGen abnormalValueGen = new TlsRangeEventsGen(1);
        abnormalValueGen.sslSubjectGen.setConstantValueGen(alert.entity);

        TlsRangeEventsGen historyGen = abnormalValueGen.copy();
        historyGen.ja3Gen.nextRangeGenCyclic(1);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        UnregularHoursIndicatorEventsGen eventsSupplier = new UnregularHoursIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE);
        eventsSupplier.setRegularHoursHistoryGen(abnormalValueGen)
                .setUnregularHoursHistoryGenJa3(historyGen)
                .setAnomalyDayUnregularHoursGenJa3(abnormalValueGen);

        indicator.unregularHoursStartTime = eventsSupplier.getUnregularStartTimeSslSubject();
        indicator.setEventsGenerator(eventsSupplier);
        indicator.addNormalValues(historyGen.sslSubjectGen.getGenerator().getAllValues());
        indicator.addAbnormalValues(abnormalValueGen.sslSubjectGen.getGenerator().getAllValues());
        alert.indicators.add(indicator);
        return this;
    }

    public SslSubjectTlsAlert ssl_subject_abnormal_ssl_subject_day_time() {

        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        TlsRangeEventsGen abnormalValueGen = new TlsRangeEventsGen(1);
        abnormalValueGen.sslSubjectGen.setConstantValueGen(alert.entity);

        TlsRangeEventsGen historyGen = abnormalValueGen.copy();
        historyGen.sslSubjectGen.nextRangeGenCyclic(1);

        TlsIndicator indicator = new TlsIndicator(alert.entity, TYPE, name);
        UnregularHoursIndicatorEventsGen eventsSupplier = new UnregularHoursIndicatorEventsGen(dataPeriod, uncommonStartDay, name, alert.entity, TYPE);
        eventsSupplier.setRegularHoursHistoryGen(abnormalValueGen)
                .setUnregularHoursHistoryGenSslSubject(historyGen)
                .setAnomalyDayUnregularHoursGenSslSubject(abnormalValueGen);

        indicator.unregularHoursStartTime = eventsSupplier.getUnregularStartTimeJa3();

        indicator.setEventsGenerator(eventsSupplier);
        indicator.addNormalValues(historyGen.ja3Gen.getGenerator().getAllValues());
        indicator.addAbnormalValues(abnormalValueGen.ja3Gen.getGenerator().getAllValues());
        alert.indicators.add(indicator);
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_dst_org_for_src_netname_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        UncommonValueForContext<String, String> indicatorCreator = new UncommonValueForContext<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        TlsRangeEventsGen commonEventsGen = indicatorCreator.createCommonValuesGen(eventsGenInit, eventsGenInit.srcNetnameGen, eventsGenInit.dstOrgGen);
        TlsRangeEventsGen uncommonHistoryGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesHistoryGen(uncommonHistoryGen, uncommonHistoryGen.srcNetnameGen, uncommonHistoryGen.dstOrgGen);
        TlsRangeEventsGen uncommonAlertGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesAnomalyGen(uncommonAlertGen, uncommonAlertGen.srcNetnameGen, uncommonAlertGen.dstOrgGen);
        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_ssl_subject_for_src_netname_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        UncommonEntityForContext<String> indicatorCreator = new UncommonEntityForContext<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        TlsRangeEventsGen commonEventsGen = indicatorCreator.createCommonValuesGen(eventsGenInit, eventsGenInit.srcNetnameGen, eventsGenInit.sslSubjectGen);
        TlsRangeEventsGen uncommonHistoryGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesHistoryGen(uncommonHistoryGen, uncommonHistoryGen.srcNetnameGen, uncommonHistoryGen.sslSubjectGen);
        TlsRangeEventsGen uncommonAlertGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesAnomalyGen(uncommonAlertGen, uncommonAlertGen.srcNetnameGen, uncommonAlertGen.sslSubjectGen);
        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }

    public SslSubjectTlsAlert ssl_subject_abnormal_domain_for_src_netname_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        UncommonValueForContext<String, String> indicatorCreator = new UncommonValueForContext<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        TlsRangeEventsGen commonEventsGen = indicatorCreator.createCommonValuesGen(eventsGenInit, eventsGenInit.srcNetnameGen, eventsGenInit.hostnameGen);
        TlsRangeEventsGen uncommonHistoryGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesHistoryGen(uncommonHistoryGen, uncommonHistoryGen.srcNetnameGen, uncommonHistoryGen.hostnameGen);
        TlsRangeEventsGen uncommonAlertGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesAnomalyGen(uncommonAlertGen, uncommonAlertGen.srcNetnameGen, uncommonAlertGen.hostnameGen);
        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }

    public SslSubjectTlsAlert ssl_subject_abnormal_dst_port_for_src_netname_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        UncommonValueForContext<String, Integer> indicatorCreator = new UncommonValueForContext<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        TlsRangeEventsGen commonEventsGen = indicatorCreator.createCommonValuesGen(eventsGenInit, eventsGenInit.srcNetnameGen, eventsGenInit.dstPortGen);
        TlsRangeEventsGen uncommonHistoryGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesHistoryGen(uncommonHistoryGen, uncommonHistoryGen.srcNetnameGen, uncommonHistoryGen.dstPortGen);
        TlsRangeEventsGen uncommonAlertGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesAnomalyGen(uncommonAlertGen, uncommonAlertGen.srcNetnameGen, uncommonAlertGen.dstPortGen);
        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_dst_port_for_domain_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        UncommonValueForContext<String, Integer> indicatorCreator = new UncommonValueForContext<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        TlsRangeEventsGen commonEventsGen = indicatorCreator.createCommonValuesGen(eventsGenInit, eventsGenInit.hostnameGen, eventsGenInit.dstPortGen);
        TlsRangeEventsGen uncommonHistoryGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesHistoryGen(uncommonHistoryGen, uncommonHistoryGen.hostnameGen, uncommonHistoryGen.dstPortGen);
        TlsRangeEventsGen uncommonAlertGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesAnomalyGen(uncommonAlertGen, uncommonAlertGen.hostnameGen, uncommonAlertGen.dstPortGen);
        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_dst_port_for_ssl_subject_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        UncommonValueForEntity<Integer> indicatorCreator = new UncommonValueForEntity<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        TlsRangeEventsGen commonEventsGen = indicatorCreator.createCommonValuesGen(eventsGenInit, eventsGenInit.sslSubjectGen, eventsGenInit.dstPortGen);
        TlsRangeEventsGen uncommonHistoryGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesHistoryGen(uncommonHistoryGen, uncommonHistoryGen.sslSubjectGen, uncommonHistoryGen.dstPortGen);
        TlsRangeEventsGen uncommonAlertGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesAnomalyGen(uncommonAlertGen, uncommonAlertGen.sslSubjectGen, uncommonAlertGen.dstPortGen);
        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_country_for_ssl_subject_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        UncommonValueForEntity<Location> indicatorCreator = new UncommonValueForEntity<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        indicatorCreator.valueToString = Location::getCountry;
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        TlsRangeEventsGen commonEventsGen = indicatorCreator.createCommonValuesGen(eventsGenInit, eventsGenInit.sslSubjectGen, eventsGenInit.locationGen);
        TlsRangeEventsGen uncommonHistoryGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesHistoryGen(uncommonHistoryGen, uncommonHistoryGen.sslSubjectGen, uncommonHistoryGen.locationGen);
        TlsRangeEventsGen uncommonAlertGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesAnomalyGen(uncommonAlertGen, uncommonAlertGen.sslSubjectGen, uncommonAlertGen.locationGen);
        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }

    public SslSubjectTlsAlert ssl_subject_abnormal_dst_port_for_dst_org_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        UncommonValueForContext<String, Integer> indicatorCreator = new UncommonValueForContext<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        TlsRangeEventsGen commonEventsGen = indicatorCreator.createCommonValuesGen(eventsGenInit, eventsGenInit.dstOrgGen, eventsGenInit.dstPortGen);
        TlsRangeEventsGen uncommonHistoryGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesHistoryGen(uncommonHistoryGen, uncommonHistoryGen.dstOrgGen, uncommonHistoryGen.dstPortGen);
        TlsRangeEventsGen uncommonAlertGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesAnomalyGen(uncommonAlertGen, uncommonAlertGen.dstOrgGen, uncommonAlertGen.dstPortGen);
        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_ja3_for_source_netname_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        UncommonValueForContext<String,String> indicatorCreator = new UncommonValueForContext<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        TlsRangeEventsGen commonEventsGen = indicatorCreator.createCommonValuesGen(eventsGenInit, eventsGenInit.srcNetnameGen, eventsGenInit.ja3Gen);
        TlsRangeEventsGen uncommonHistoryGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesHistoryGen(uncommonHistoryGen, uncommonHistoryGen.srcNetnameGen, uncommonHistoryGen.ja3Gen);
        TlsRangeEventsGen uncommonAlertGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesAnomalyGen(uncommonAlertGen, uncommonAlertGen.srcNetnameGen, uncommonAlertGen.ja3Gen);
        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }


    public SslSubjectTlsAlert ssl_subject_abnormal_ssl_subject_for_ja3_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        UncommonEntityForContext<String> indicatorCreator = new UncommonEntityForContext<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        TlsRangeEventsGen commonEventsGen = indicatorCreator.createCommonValuesGen(eventsGenInit, eventsGenInit.ja3Gen, eventsGenInit.sslSubjectGen);
        TlsRangeEventsGen uncommonHistoryGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesHistoryGen(uncommonHistoryGen, uncommonHistoryGen.ja3Gen, uncommonHistoryGen.sslSubjectGen);
        TlsRangeEventsGen uncommonAlertGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesAnomalyGen(uncommonAlertGen, uncommonAlertGen.ja3Gen, uncommonAlertGen.sslSubjectGen);
        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }

    public SslSubjectTlsAlert ssl_subject_abnormal_domain_for_ja3_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        UncommonValueForContext<String,String> indicatorCreator = new UncommonValueForContext<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        TlsRangeEventsGen commonEventsGen = indicatorCreator.createCommonValuesGen(eventsGenInit, eventsGenInit.ja3Gen, eventsGenInit.hostnameGen);
        TlsRangeEventsGen uncommonHistoryGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesHistoryGen(uncommonHistoryGen, uncommonHistoryGen.ja3Gen, uncommonHistoryGen.hostnameGen);
        TlsRangeEventsGen uncommonAlertGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesAnomalyGen(uncommonAlertGen, uncommonAlertGen.ja3Gen, uncommonAlertGen.hostnameGen);
        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }

    public SslSubjectTlsAlert ssl_subject_abnormal_dst_port_for_ja3_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        UncommonValueForContext<String,Integer> indicatorCreator = new UncommonValueForContext<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(HISTORICAL_DATA_COMMON_VALUES);
        TlsRangeEventsGen commonEventsGen = indicatorCreator.createCommonValuesGen(eventsGenInit, eventsGenInit.ja3Gen, eventsGenInit.dstPortGen);
        TlsRangeEventsGen uncommonHistoryGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesHistoryGen(uncommonHistoryGen, uncommonHistoryGen.ja3Gen, uncommonHistoryGen.dstPortGen);
        TlsRangeEventsGen uncommonAlertGen = commonEventsGen.copy();
        indicatorCreator.createUncommonValuesAnomalyGen(uncommonAlertGen, uncommonAlertGen.ja3Gen, uncommonAlertGen.dstPortGen);
        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }






    public SslSubjectTlsAlert high_number_of_bytes_sent_by_src_ip_to_domain_ssl_subject_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        AbnormalTraffic<String> indicatorCreator = new AbnormalTraffic<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(1);
        TlsRangeEventsGen eventsGenInitCopy = eventsGenInit.copy();

        indicatorCreator.createNormalTrafficHistoryGen(eventsGenInitCopy, eventsGenInitCopy.srcIpGenerator,eventsGenInitCopy.hostnameGen);

        TlsRangeEventsGen highTrafficAnomalyGen = new TlsRangeEventsGen(1);
        highTrafficAnomalyGen.sslSubjectGen.setGenerator(eventsGenInitCopy.sslSubjectGen.getGenerator());
        highTrafficAnomalyGen.srcIpGenerator.setGenerator(eventsGenInitCopy.srcIpGenerator.getGenerator());
        highTrafficAnomalyGen.hostnameGen.setGenerator(eventsGenInitCopy.hostnameGen.getGenerator());

        indicatorCreator.createHighTrafficAnomalyGen(highTrafficAnomalyGen, highTrafficAnomalyGen.srcIpGenerator, highTrafficAnomalyGen.hostnameGen);

        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }


    public SslSubjectTlsAlert high_number_of_bytes_sent_by_src_ip_to_dst_org_ssl_subject_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        AbnormalTraffic<String> indicatorCreator = new AbnormalTraffic<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(1);
        TlsRangeEventsGen eventsGenInitCopy = eventsGenInit.copy();

        indicatorCreator.createNormalTrafficHistoryGen(eventsGenInitCopy, eventsGenInitCopy.srcIpGenerator,eventsGenInitCopy.dstOrgGen);
        TlsRangeEventsGen highTrafficAnomalyGen = eventsGenInit.copy();
        indicatorCreator.createHighTrafficAnomalyGen(highTrafficAnomalyGen, highTrafficAnomalyGen.srcIpGenerator, highTrafficAnomalyGen.dstOrgGen);

        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }

    public SslSubjectTlsAlert high_number_of_bytes_sent_by_src_ip_to_ssl_subject_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        AbnormalTraffic<String> indicatorCreator = new AbnormalTraffic<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(1);
        TlsRangeEventsGen eventsGenInitCopy = eventsGenInit.copy();

        indicatorCreator.createNormalTrafficHistoryGen(eventsGenInitCopy, eventsGenInitCopy.srcIpGenerator,eventsGenInitCopy.ja3Gen);
        TlsRangeEventsGen highTrafficAnomalyGen = eventsGenInit.copy();
        indicatorCreator.createHighTrafficAnomalyGen(highTrafficAnomalyGen, highTrafficAnomalyGen.srcIpGenerator, highTrafficAnomalyGen.ja3Gen);

        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }

    public SslSubjectTlsAlert high_number_of_bytes_sent_to_domain_ssl_subject_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        AbnormalTraffic<String> indicatorCreator = new AbnormalTraffic<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(1);
        TlsRangeEventsGen eventsGenInitCopy = eventsGenInit.copy();

        indicatorCreator.createNormalTrafficHistoryGen(eventsGenInitCopy, eventsGenInitCopy.hostnameGen);
        TlsRangeEventsGen highTrafficAnomalyGen = eventsGenInit.copy();
        indicatorCreator.createHighTrafficAnomalyGen(highTrafficAnomalyGen, highTrafficAnomalyGen.hostnameGen);

        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }


    public SslSubjectTlsAlert high_number_of_bytes_sent_to_dst_org_ssl_subject_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        AbnormalTraffic<String> indicatorCreator = new AbnormalTraffic<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(1);
        TlsRangeEventsGen eventsGenInitCopy = eventsGenInit.copy();

        indicatorCreator.createNormalTrafficHistoryGen(eventsGenInitCopy, eventsGenInitCopy.dstOrgGen);
        TlsRangeEventsGen highTrafficAnomalyGen = eventsGenInit.copy();
        indicatorCreator.createHighTrafficAnomalyGen(highTrafficAnomalyGen, highTrafficAnomalyGen.dstOrgGen);

        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }

    public SslSubjectTlsAlert high_number_of_bytes_sent_to_ssl_subject_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        AbnormalTraffic<String> indicatorCreator = new AbnormalTraffic<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(1);
        TlsRangeEventsGen eventsGenInitCopy = eventsGenInit.copy();

        indicatorCreator.createNormalTrafficHistoryGen(eventsGenInitCopy);
        TlsRangeEventsGen highTrafficAnomalyGen = eventsGenInit.copy();
        indicatorCreator.createHighTrafficAnomalyGen(highTrafficAnomalyGen);

        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }


    public SslSubjectTlsAlert high_number_of_bytes_sent_by_src_ip_to_dst_port_ssl_subject_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        AbnormalTraffic<Integer> indicatorCreator = new AbnormalTraffic<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(1);
        TlsRangeEventsGen eventsGenInitCopy = eventsGenInit.copy();

        indicatorCreator.createNormalTrafficHistoryGen(eventsGenInitCopy, eventsGenInitCopy.srcIpGenerator,eventsGenInitCopy.dstPortGen);
        TlsRangeEventsGen highTrafficAnomalyGen = eventsGenInit.copy();
        indicatorCreator.createHighTrafficAnomalyGen(highTrafficAnomalyGen, highTrafficAnomalyGen.srcIpGenerator, highTrafficAnomalyGen.dstPortGen);

        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }

    public SslSubjectTlsAlert high_number_of_bytes_sent_to_dst_port_ssl_subject_outbound() {
        String name = new Object() {}.getClass().getEnclosingMethod().getName();
        LOGGER.info("Adding indicator: " + name);
        alert.indicatorNames.add(name);

        AbnormalTraffic<Integer> indicatorCreator = new AbnormalTraffic<>(alert.entity, TYPE, name, dataPeriod, uncommonStartDay);
        TlsRangeEventsGen eventsGenInit = new TlsRangeEventsGen(1);
        TlsRangeEventsGen eventsGenInitCopy = eventsGenInit.copy();

        indicatorCreator.createNormalTrafficHistoryGen(eventsGenInitCopy, eventsGenInitCopy.dstPortGen);
        TlsRangeEventsGen highTrafficAnomalyGen = eventsGenInit.copy();
        indicatorCreator.createHighTrafficAnomalyGen(highTrafficAnomalyGen, highTrafficAnomalyGen.dstPortGen);

        alert.indicators.add(indicatorCreator.getIndicator());
        return this;
    }

}

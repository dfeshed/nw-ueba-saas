package fortscale.monitoring.external.stats.linux.collector;

import fortscale.monitoring.external.stats.linux.collector.collectors.ExternalStatsOSProcessCollector;
import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsOSProcessCollectorMetrics;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileKeyMultipleValueParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileParser;
import fortscale.monitoring.external.stats.linux.collector.parsers.ExternalStatsProcFileSingleValueParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by galiar on 27/04/2016.
 */
public class ExternalStatsOSProcessCollectorTest {

    private static final double MB_PER_PAGE = 0.00390625; //4096 (page size assumed 4KB)/ 1048576 (bytes in MB)

    @Test
    public void testExternalOSProcessCollector() throws Exception{

        String pidName = "32120";
        ExternalStatsOSProcessCollector osProcessCollector = new ExternalStatsOSProcessCollector(pidName);
        String statFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/pid/stat";
        String cmdFilename = "src/test/resources/fortscale/monitoring/external/stats/linux/collector/parser/proc/files/pid/cmdline";
        String statsSeparator = " ";
        int indexOfKey = 0;

        ExternalStatsProcFileSingleValueParser cmdLineParser = new ExternalStatsProcFileSingleValueParser(cmdFilename,"\0"," ",new File(cmdFilename).getName());
        ExternalStatsProcFileKeyMultipleValueParser statParser = new ExternalStatsProcFileKeyMultipleValueParser(statFilename,statsSeparator,new File(statFilename).getName(),indexOfKey);
        Map<String,ExternalStatsProcFileParser> parserMap = new HashMap<>();
        parserMap.put(cmdLineParser.getName(),cmdLineParser);
        parserMap.put(statParser.getName(),statParser);

        osProcessCollector.collect(parserMap);
        ExternalStatsOSProcessCollectorMetrics processMetrics = osProcessCollector.getProcessMetrics();
        Assert.assertEquals(32120,processMetrics.getPid().longValue());
        Assert.assertEquals(0,processMetrics.getChildrenWaitTime().doubleValue(),0);
        Assert.assertEquals(391,processMetrics.getKernelTime().doubleValue(),0);
        Assert.assertEquals(12693504 ,processMetrics.getMemoryRSS().longValue());
        Assert.assertEquals(1040838656,processMetrics.getMemoryVSize().longValue());
        Assert.assertEquals(1,processMetrics.getNumThreads().longValue());
        Assert.assertEquals(771.0,processMetrics.getUserTime().doubleValue(),0);
        Assert.assertEquals("/usr/java/jdk1.8.0_65/bin/java-Xmx256M-server-XX:+UseParNewGC-XX:+UseConcMarkSweepGC-XX:+CMSClassUnloadingEnabled-XX:+CMSScavengeBeforeRemark-XX:+DisableExplicitGC-Djava.awt.headless=true-Xloggc:/var/run/cloudera-scm-agent/process/1509-kafka-KAFKA_BROKER/kafkaServer-gc.log-verbose:gc-XX:+PrintGCDetails-XX:+PrintGCDateStamps-XX:+PrintGCTimeStamps-Dcom.sun.management.jmxremote-Dcom.sun.management.jmxremote.authenticate=false-Dcom.sun.management.jmxremote.ssl=false-Dcom.sun.management.jmxremote.port=9393-Dkafka.logs.dir=/var/run/cloudera-scm-agent/process/1509-kafka-KAFKA_BROKER-Dlog4j.configuration=file:/var/run/cloudera-scm-agent/process/1509-kafka-KAFKA_BROKER/log4j.properties-cp:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../core/build/dependant-libs-2.10.4*/*.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../examples/build/libs//kafka-examples*.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../contrib/hadoop-consumer/build/libs//kafka-hadoop-consumer*.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../contrib/hadoop-producer/build/libs//kafka-hadoop-producer*.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../clients/build/libs/kafka-clients*.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/curator-client-2.7.1.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/curator-framework-2.7.1.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/grizzled-slf4j_2.10-1.0.2.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/guava-16.0.1.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/jackson-annotations-2.3.0.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/jackson-core-2.3.1.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/jackson-databind-2.3.1.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/javax.servlet-api-3.1.0.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/jetty-http-9.2.10.v20150310.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/jetty-io-9.2.10.v20150310.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/jetty-security-9.2.10.v20150310.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/jetty-server-9.2.10.v20150310.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/jetty-servlet-9.2.10.v20150310.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/jetty-util-9.2.10.v20150310.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/joda-convert-1.6.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/joda-time-2.3.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/jopt-simple-3.2.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/json4s-ast_2.10-3.2.11.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/json4s-core_2.10-3.2.11.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/json4s-jackson_2.10-3.2.11.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/juniversalchardet-1.0.3.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/kafka_2.10-0.8.2.0-kafka-1.3.2.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/kafka_2.10-0.8.2.0-kafka-1.3.2-sources.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/kafka-clients-0.8.2.0-kafka-1.3.2.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/kafka-http-metrics-reporter-1.1.0-kafka-1.3.2.jar:/opt/cloudera/parcels/KAFKA-0.8.2.0-1.kafka1.3.2.p0.15/lib/kafka/bin/../libs/log4j-1.2.17.jar:/opt/cloudera/pa",processMetrics.getProcessCommandLine());


    }

}

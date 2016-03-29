package fortscale.utils.kafka;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by amira on 29/03/2016.
 */
public class SimpleMetricsCountersReaderTest {

    // This ain't meant to be a unit test but just a test you can use to test the reader
    // on your dev machine, therefore the @Test is commented out - uncomment it if you want
    // to run the test and update the parameters to match what your looking for.
    @Test
    public void testSimpleMetricsCounters() {

        // Update the following parameters to match the data on the machine you test
        String zookeeper = "dev-amira";
        int zookeeperPort = 9092;
        String headerToCheck = "fortscale.streaming.task.ScoringTask";
        String clientName = "SimpleMetricsCountersReaderTest";
        String jobToCheck = "entity-events-scoring-task";
        int waitTimeBetweenMetricsChecks = 10;
        int checkRetries = 10;
        Collection<String> metricsToCapture = new ArrayList<>();
        metricsToCapture.add("event-score-message-epochime");
        metricsToCapture.add("event-score-message-count");

        SimpleMetricsCountersReader reader = new SimpleMetricsCountersReader(zookeeper, zookeeperPort, headerToCheck,
                clientName, jobToCheck, waitTimeBetweenMetricsChecks, checkRetries, metricsToCapture);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(reader);

        for(int i=0; i<1000; i++) {
            try {
                Thread.currentThread().sleep(1000);
                Integer count = -1;
                Integer epochime = -1;
                Object epochimeObj = reader.getMetric("event-score-message-epochime");
                if(epochimeObj!=null) {
                    epochime = (Integer)epochimeObj;
                }
                Object countObj = reader.getMetric("event-score-message-count");
                if(countObj!=null) {
                    count = (Integer)countObj;
                }

                System.out.println(String.format("[%d] epochime=%d, count=%d", i, epochime, count));
            } catch (InterruptedException ex) {
                System.out.println("Cached an InterruptedException...");
            }

        }
        reader.stop();
        executorService.shutdown();

    }
}

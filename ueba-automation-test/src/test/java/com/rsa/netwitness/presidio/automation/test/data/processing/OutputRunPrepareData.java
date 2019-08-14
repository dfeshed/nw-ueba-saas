package com.rsa.netwitness.presidio.automation.test.data.processing;

import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlBuilder;
import com.rsa.netwitness.presidio.automation.utils.common.ASCIIArtGenerator;
import com.rsa.netwitness.presidio.automation.utils.output.OutputDataProcessingHelper;
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.time.Instant.parse;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.collections.Lists.newArrayList;
import static presidio.data.generators.utils.TimeUtils.calcDaysBack;


public class OutputRunPrepareData extends AbstractTestNGSpringContextTests {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(OutputRunPrepareData.class.getName());
    private static ASCIIArtGenerator ART_GEN = new ASCIIArtGenerator();

    private OutputDataProcessingHelper dataProcessingHelper = new OutputDataProcessingHelper();

    private List<String> SMART_RECORD_CONF_NAMES = newArrayList("userId_hourly", "sslSubject_hourly", "ja3_hourly");
    private List<String> ENTITIES_TO_PROCESS = newArrayList("userId", "ja3", "sslSubject");

    @Parameters("historical_days_back")
    @BeforeClass
    public void beforeClass(@Optional("10") int historicalDaysBack) throws JSONException, InterruptedException {
        ART_GEN.printTextArt(getClass().getSimpleName());
        LOGGER.info("\t***** " + getClass().getSimpleName() + " started with historicalDaysBack=" + historicalDaysBack);

        List<List<? extends Callable<Integer>>> parallelTasksToExecute = Stream.of(

                processorRun(parse(calcDaysBack(historicalDaysBack) + "T00:00:00.00Z"), parse(calcDaysBack(2) + "T13:00:00.00Z")),
                recalculateUserScore(parse(calcDaysBack(historicalDaysBack) + "T00:00:00.00Z"), parse(calcDaysBack(2) + "T13:00:00.00Z")),

                processorRun(parse(calcDaysBack(2) + "T13:00:00.00Z"), parse(calcDaysBack(0) + "T00:00:00.00Z")),
                recalculateUserScore(parse(calcDaysBack(historicalDaysBack) + "T00:00:00.00Z"), parse(calcDaysBack(0) + "T00:00:00.00Z"))

        ).sequential().collect(toList());


        for (List<? extends Callable<Integer>> parallelTasks : parallelTasksToExecute) {
            LOGGER.info("********** Next cycle **********");
            ExecutorService executor = Executors.newWorkStealingPool();
            List<Future<Integer>> futures = executor.invokeAll(parallelTasks, 5, TimeUnit.MINUTES);
            List<Integer> results = futures.parallelStream().map(toIntResult).collect(toList());
            assertThat(results).containsOnly(0);
        }
    }

    @BeforeMethod
    public void beforeName(Method method){
        String testName = method.getName();
        LOGGER.info("Start running test: " + testName);
    }

    @Test
    public void calcUserScore() {
        RestHelper restHelper = new RestHelper();
        ParametersUrlBuilder url = restHelper.alerts().url().withNoParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        assertThat(alerts)
                .withFailMessage(url + "\nAlerts REST response is null or empty.")
                .isNotNull()
                .isNotEmpty();
    }




    private List<OutputDataProcessingHelper.ProcessorRun> processorRun(Instant startDate, Instant endDate) {
        return SMART_RECORD_CONF_NAMES.stream()
                .map(smartRecordConfName -> dataProcessingHelper.processorRun(startDate, endDate, smartRecordConfName))
                .collect(toList());
    }

    private List<OutputDataProcessingHelper.RecalculateUserScore> recalculateUserScore(Instant startDate, Instant endDate) {
        return ENTITIES_TO_PROCESS.stream()
                .map(entity -> dataProcessingHelper.recalculateUserScore(startDate, endDate, entity))
                .collect(toList());
    }



    private Function<Future<Integer>, Integer> toIntResult = future -> {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return -100;
    };
}